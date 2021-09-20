# wal（write ahead log）

参考 `nifi` 的 `write ahead log` 实现。

`nifi` 的存储包含三个部分：`FlowFile Repository`、`Content Repository` 和 `Provenance Repository`。`FlowFile Repository` 存储 `FlowFile` 的元数据，追踪 `FlowFile` 的状态，`FlowFile Repository` 的实现是插件式的，默认实现支持 `Write-Ahead-Log`。

`WriteAheadRepository` 用于为内存中的数据提供状态的持久化功能，以在系统重启、断电时能够提供数据恢复功能。`WriteAheadRepository` 不提供任何的查询功能。

`wal` 将更新操作写入编辑日志，系统重启时可以通过回放编辑日志中的记录的更新操作恢复数据。如果编辑日志过于庞大，不仅会占据过多磁盘空间还会延长数据恢复时间，`nifi` 提供了 `checkpoint` 功能。当 `nifi` 将内存数据写入磁盘时可以移除对应的编辑日志，降低需要的存储空间。

执行过 `checkpoint` 后，修改会继续写入新的编辑日志，当系统恢复时，先加载 `checkpoint` 数据，在回放编辑日志。

## `WriteAheadRepository`

`WriteAheadRepository` 的接口定义如下：

```java
public interface WriteAheadRepository<T> {

    int update(Collection<T> records, boolean forceSync) throws IOException;

    Collection<T> recoverRecords() throws IOException;

    Set<String> getRecoveredSwapLocations() throws IOException;

    int checkpoint() throws IOException;

    void shutdown() throws IOException;
}
```

* `#update(Collection, boolean)`。向 respository 写入数据，所有数据都会通过 `wal` 提供保障。
* `#recoverRecords()`。执行 recovery，通过 `wal` 恢复数据。
* `#checkpoint()`。执行 checkpoint。

## 序列化

向 repository 中写入的数据需要经过序列化后写入，读取时需要反序列化。

```java
public interface SerDe<T> {

    default void writeHeader(DataOutputStream out) throws IOException {
    }

    void serializeEdit(T previousRecordState, T newRecordState, DataOutputStream out) throws IOException;

    void serializeRecord(T record, DataOutputStream out) throws IOException;

    default void readHeader(DataInputStream in) throws IOException {
    }

    T deserializeEdit(DataInputStream in, Map<Object, T> currentRecordStates, int version) throws IOException;

    T deserializeRecord(DataInputStream in, int version) throws IOException;

    Object getRecordIdentifier(T record);

    UpdateType getUpdateType(T record);

    String getLocation(T record);

    int getVersion();

    default void close() throws IOException {
    }

    default void writeExternalFileReference(File externalFile, DataOutputStream out) throws IOException {
        throw new UnsupportedOperationException();
    }

    default boolean isWriteExternalFileReferenceSupported() {
        return false;
    }

    default boolean isMoreInExternalFile() throws IOException {
        return false;
    }
}
```

## `SequentialAccessWriteAheadLog`

`WriteAheadRepository` 默认实现为 `SequentialAccessWriteAheadLog`。

`Write-Ahead-Log` 需要实现编辑日志和 `checkpoint` 功能。

### `WriteAheadJournal`

编辑日志接口为 `WriteAheadJournal`。

```java
public interface WriteAheadJournal<T> extends Closeable {

    void writeHeader() throws IOException;

    void update(Collection<T> records, RecordLookup<T> recordLookup) throws IOException;

    JournalRecovery recoverRecords(Map<Object, T> recordMap, Set<String> swapLocations) throws IOException;

    void fsync() throws IOException;

    JournalSummary getSummary();

    boolean isHealthy();

    void dispose();
}
```

日志文件记录日志头（`header`）信息：

* 编辑日志实现类、版本
* 序列化器实现类和版本
* 序列化器的 `header` 信息

写入和读取日志头实现如下：

```java

public class LengthDelimitedJournal<T> implements WriteAheadJournal<T> {

	@Override
    public synchronized void writeHeader() throws IOException {
        try {
            final DataOutputStream outStream = new DataOutputStream(getOutputStream());
            outStream.writeUTF(LengthDelimitedJournal.class.getName());
            outStream.writeInt(JOURNAL_ENCODING_VERSION);

            serde = serdeFactory.createSerDe(null);
            outStream.writeUTF(serde.getClass().getName());
            outStream.writeInt(serde.getVersion());

            try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final DataOutputStream dos = new DataOutputStream(baos)) {

                serde.writeHeader(dos);
                dos.flush();

                final int serdeHeaderLength = baos.size();
                outStream.writeInt(serdeHeaderLength);
                baos.writeTo(outStream);
            }

            outStream.flush();
        } catch (final Throwable t) {
            poison(t);

            final IOException ioe = (t instanceof IOException) ? (IOException) t : new IOException("Failed to create journal file " + journalFile, t);
            logger.error("Failed to create new journal file {} due to {}", journalFile, ioe.toString(), ioe);
            throw ioe;
        }

        headerWritten = true;
    }

    private synchronized SerDeAndVersion validateHeader(final DataInputStream in) throws IOException {
        final String journalClassName = in.readUTF();
        logger.debug("Write Ahead Log Class Name for {} is {}", journalFile, journalClassName);
        if (!LengthDelimitedJournal.class.getName().equals(journalClassName)) {
            throw new IOException("Invalid header information - " + journalFile + " does not appear to be a valid journal file.");
        }

        final int encodingVersion = in.readInt();
        logger.debug("Encoding version for {} is {}", journalFile, encodingVersion);
        if (encodingVersion > JOURNAL_ENCODING_VERSION) {
            throw new IOException("Cannot read journal file " + journalFile + " because it is encoded using veresion " + encodingVersion
                + " but this version of the code only understands version " + JOURNAL_ENCODING_VERSION + " and below");
        }

        final String serdeClassName = in.readUTF();
        logger.debug("Serde Class Name for {} is {}", journalFile, serdeClassName);
        final SerDe<T> serde;
        try {
            serde = serdeFactory.createSerDe(serdeClassName);
        } catch (final IllegalArgumentException iae) {
            throw new IOException("Cannot read journal file " + journalFile + " because the serializer/deserializer used was " + serdeClassName
                + " but this repository is configured to use a different type of serializer/deserializer");
        }

        final int serdeVersion = in.readInt();
        logger.debug("Serde version is {}", serdeVersion);
        if (serdeVersion > serde.getVersion()) {
            throw new IOException("Cannot read journal file " + journalFile + " because it is encoded using veresion " + encodingVersion
                + " of the serializer/deserializer but this version of the code only understands version " + serde.getVersion() + " and below");
        }

        final int serdeHeaderLength = in.readInt();
        final InputStream serdeHeaderIn = new LimitingInputStream(in, serdeHeaderLength);
        final DataInputStream dis = new DataInputStream(serdeHeaderIn);
        serde.readHeader(dis);

        return new SerDeAndVersion(serde, serdeVersion);
    }

	private class SerDeAndVersion {
        private final SerDe<T> serde;
        private final int version;

        public SerDeAndVersion(final SerDe<T> serde, final int version) {
            this.serde = serde;
            this.version = version;
        }

        public SerDe<T> getSerDe() {
            return serde;
        }

        public int getVersion() {
            return version;
        }
    }
}
```

向编辑日志中写入事物的过程分为 2 步：

* 序列化 `record` 状态
* 结合事务，将 `record` 状态写入编辑日志中

```java
public class LengthDelimitedJournal<T> implements WriteAheadJournal<T> {

	private final File journalFile;
    private final File overflowDirectory;
    private final long initialTransactionId;private final int maxInHeapSerializationBytes;

    private SerDe<T> serde;
    private FileOutputStream fileOut;
    private BufferedOutputStream bufferedOut;

    private long currentTransactionId;
    private int transactionCount;
    private boolean headerWritten = false;

    private final ByteBuffer transactionPreamble = ByteBuffer.allocate(12); // guarded by synchronized block

	@Override
    public void update(final Collection<T> records, final RecordLookup<T> recordLookup) throws IOException {
        if (!headerWritten) {
            throw new IllegalStateException("Cannot update journal file " + journalFile + " because no header has been written yet.");
        }

        if (records.isEmpty()) {
            return;
        }

        checkState();

        File overflowFile = null;
        final ByteArrayDataOutputStream bados = streamPool.borrowObject();

        try {
            FileOutputStream overflowFileOut = null;

            try {
                DataOutputStream dataOut = bados.getDataOutputStream();
                for (final T record : records) {
                    final Object recordId = serde.getRecordIdentifier(record);
                    final T previousRecordState = recordLookup.lookup(recordId);
                    serde.serializeEdit(previousRecordState, record, dataOut);

                    final int size = bados.getByteArrayOutputStream().size();
                    if (serde.isWriteExternalFileReferenceSupported() && size > maxInHeapSerializationBytes) {
                        if (!overflowDirectory.exists()) {
                            createOverflowDirectory(overflowDirectory.toPath());
                        }

                        // If we have exceeded our threshold for how much to serialize in memory,
                        // flush the in-memory representation to an 'overflow file' and then update
                        // the Data Output Stream that is used to write to the file also.
                        overflowFile = new File(overflowDirectory, UUID.randomUUID().toString());
                        logger.debug("Length of update with {} records exceeds in-memory max of {} bytes. Overflowing to {}", records.size(), maxInHeapSerializationBytes, overflowFile);

                        overflowFileOut = new FileOutputStream(overflowFile);
                        bados.getByteArrayOutputStream().writeTo(overflowFileOut);
                        bados.getByteArrayOutputStream().reset();

                        // change dataOut to point to the File's Output Stream so that all subsequent records are written to the file.
                        dataOut = new DataOutputStream(new BufferedOutputStream(overflowFileOut));

                        // We now need to write to the ByteArrayOutputStream a pointer to the overflow file
                        // so that what is written to the actual journal is that pointer.
                        serde.writeExternalFileReference(overflowFile, bados.getDataOutputStream());
                    }
                }

                dataOut.flush();

                // If we overflowed to an external file, we need to be sure that we sync to disk before
                // updating the Journal. Otherwise, we could get to a state where the Journal was flushed to disk without the
                // external file being flushed. This would result in a missed update to the FlowFile Repository.
                if (overflowFileOut != null) {
                    if (logger.isDebugEnabled()) { // avoid calling File.length() if not necessary
                        logger.debug("Length of update to overflow file is {} bytes", overflowFile.length());
                    }

                    overflowFileOut.getFD().sync();
                }
            } finally {
                if (overflowFileOut != null) {
                    try {
                        overflowFileOut.close();
                    } catch (final Exception e) {
                        logger.warn("Failed to close open file handle to overflow file {}", overflowFile, e);
                    }
                }
            }

            final ByteArrayOutputStream baos = bados.getByteArrayOutputStream();
            final OutputStream out = getOutputStream();

            final long transactionId;
            synchronized (this) {
                checkState();

                try {
                    transactionId = currentTransactionId++;
                    transactionCount++;

                    transactionPreamble.clear();
                    transactionPreamble.putLong(transactionId);
                    transactionPreamble.putInt(baos.size());

                    out.write(TRANSACTION_FOLLOWS);
                    out.write(transactionPreamble.array());
                    baos.writeTo(out);
                    out.flush();
                } catch (final Throwable t) {
                    // While the outter Throwable that wraps this "catch" will call Poison, it is imperative that we call poison()
                    // before the synchronized block is excited. Otherwise, another thread could potentially corrupt the journal before
                    // the poison method closes the file.
                    poison(t);
                    throw t;
                }
            }

            logger.debug("Wrote Transaction {} to journal {} with length {} and {} records", transactionId, journalFile, baos.size(), records.size());
        } catch (final Throwable t) {
            poison(t);

            if (overflowFile != null) {
                if (!overflowFile.delete() && overflowFile.exists()) {
                    logger.warn("Failed to cleanup temporary overflow file " + overflowFile + " - this file should be cleaned up manually.");
                }
            }

            throw t;
        } finally {
            streamPool.returnObject(bados);
        }
    }

    // Visible/overrideable for testing.
    protected void createOverflowDirectory(final Path path) throws IOException {
        Files.createDirectories(path);
    }

    private synchronized OutputStream getOutputStream() throws FileNotFoundException {
        if (fileOut == null) {
            fileOut = new FileOutputStream(journalFile);
            bufferedOut = new BufferedOutputStream(fileOut);
        }

        return bufferedOut;
    }
}
```

`ByteArrayDataOutputStream` 是 `DataOutputStream` 和 `ByteArrayOutputStream` 的包装类，它内部将 `DataOutputStream` 的输出写入到 `ByteArrayOutputStream` 中，保存在内存中。

因此在序列化 `record` 状态的时候，如果 `record` 状态过大，会导致 `ByteArrayOutputStream` 溢出。`LengthDelimitedJournal` 的解决办法是将溢出的数据写入到磁盘中，同时在 `ByteArrayOutputStream` 中写入溢出数据文件的指针。

```java
public class ByteArrayDataOutputStream {
    private final ByteArrayOutputStream baos;
    private final DataOutputStream dos;

    public ByteArrayDataOutputStream(final int initialBufferSize) {
        this.baos = new ByteArrayOutputStream(initialBufferSize);
        this.dos = new DataOutputStream(baos);
    }

    public DataOutputStream getDataOutputStream() {
        return dos;
    }

    public ByteArrayOutputStream getByteArrayOutputStream() {
        return baos;
    }
}
```

写入事务的过程：

* 当前事务 id 自增，事务数量 + 1
* 向编辑日志中写入 `LengthDelimitedJournal#TRANSACTION_FOLLOWS` 标识，写入此次事务信息。
* 将序列化后的状态或者数据文件指针写入到编辑日志中

从编辑日志中恢复的过程也就是反向读取编辑日志中的事务并应用到 record 记录的过程。

```java
public class LengthDelimitedJournal<T> implements WriteAheadJournal<T> {

	@Override
    public JournalRecovery recoverRecords(final Map<Object, T> recordMap, final Set<String> swapLocations) throws IOException {
        long maxTransactionId = -1L;
        int updateCount = 0;

        boolean eofException = false;
        logger.info("Recovering records from journal {}", journalFile);
        final double journalLength = journalFile.length();

        try (final InputStream fis = new FileInputStream(journalFile);
            final InputStream bufferedIn = new BufferedInputStream(fis);
            final ByteCountingInputStream byteCountingIn = new ByteCountingInputStream(bufferedIn);
            final DataInputStream in = new DataInputStream(byteCountingIn)) {

            try {
                // Validate that the header is what we expect and obtain the appropriate SerDe and Version information
                final SerDeAndVersion serdeAndVersion = validateHeader(in);
                final SerDe<T> serde = serdeAndVersion.getSerDe();

                // Ensure that we get a valid transaction indicator
                int transactionIndicator = in.read();
                if (transactionIndicator != TRANSACTION_FOLLOWS && transactionIndicator != JOURNAL_COMPLETE && transactionIndicator != -1) {
                    throw new IOException("After reading " + byteCountingIn.getBytesConsumed() + " bytes from " + journalFile + ", encountered unexpected value of "
                        + transactionIndicator + " for the Transaction Indicator. This journal may have been corrupted.");
                }

                long consumedAtLog = 0L;

                // We don't want to apply the updates in a transaction until we've finished recovering the entire
                // transaction. Otherwise, we could apply say 8 out of 10 updates and then hit an EOF. In such a case,
                // we want to rollback the entire transaction. We handle this by not updating recordMap or swapLocations
                // variables directly but instead keeping track of the things that occurred and then once we've read the
                // entire transaction, we can apply those updates to the recordMap and swapLocations.
                final Map<Object, T> transactionRecordMap = new HashMap<>();
                final Set<Object> idsRemoved = new HashSet<>();
                final Set<String> swapLocationsRemoved = new HashSet<>();
                final Set<String> swapLocationsAdded = new HashSet<>();
                int transactionUpdates = 0;

                // While we have a transaction to recover, recover it
                while (transactionIndicator == TRANSACTION_FOLLOWS) {
                    transactionRecordMap.clear();
                    idsRemoved.clear();
                    swapLocationsRemoved.clear();
                    swapLocationsAdded.clear();
                    transactionUpdates = 0;

                    // Format is <Transaction ID: 8 bytes> <Transaction Length: 4 bytes> <Transaction data: # of bytes indicated by Transaction Length Field>
                    final long transactionId = in.readLong();
                    maxTransactionId = Math.max(maxTransactionId, transactionId);
                    final int transactionLength = in.readInt();

                    // Use SerDe to deserialize the update. We use a LimitingInputStream to ensure that the SerDe is not able to read past its intended
                    // length, in case there is a bug in the SerDe. We then use a ByteCountingInputStream so that we can ensure that all of the data has
                    // been read and throw EOFException otherwise.
                    final InputStream transactionLimitingIn = new LimitingInputStream(in, transactionLength);
                    final ByteCountingInputStream transactionByteCountingIn = new ByteCountingInputStream(transactionLimitingIn);
                    final DataInputStream transactionDis = new DataInputStream(transactionByteCountingIn);

                    while (transactionByteCountingIn.getBytesConsumed() < transactionLength || serde.isMoreInExternalFile()) {
                        final T record = serde.deserializeEdit(transactionDis, recordMap, serdeAndVersion.getVersion());

                        // Update our RecordMap so that we have the most up-to-date version of the Record.
                        final Object recordId = serde.getRecordIdentifier(record);
                        final UpdateType updateType = serde.getUpdateType(record);

                        switch (updateType) {
                            case DELETE: {
                                idsRemoved.add(recordId);
                                transactionRecordMap.remove(recordId);
                                break;
                            }
                            case SWAP_IN: {
                                final String location = serde.getLocation(record);
                                if (location == null) {
                                    logger.error("Recovered SWAP_IN record from edit log, but it did not contain a Location; skipping record");
                                } else {
                                    swapLocationsRemoved.add(location);
                                    swapLocationsAdded.remove(location);
                                    transactionRecordMap.put(recordId, record);
                                }
                                break;
                            }
                            case SWAP_OUT: {
                                final String location = serde.getLocation(record);
                                if (location == null) {
                                    logger.error("Recovered SWAP_OUT record from edit log, but it did not contain a Location; skipping record");
                                } else {
                                    swapLocationsRemoved.remove(location);
                                    swapLocationsAdded.add(location);
                                    idsRemoved.add(recordId);
                                    transactionRecordMap.remove(recordId);
                                }

                                break;
                            }
                            default: {
                                transactionRecordMap.put(recordId, record);
                                idsRemoved.remove(recordId);
                                break;
                            }
                        }

                        transactionUpdates++;
                    }

                    // Apply the transaction
                    for (final Object id : idsRemoved) {
                        recordMap.remove(id);
                    }
                    recordMap.putAll(transactionRecordMap);
                    swapLocations.removeAll(swapLocationsRemoved);
                    swapLocations.addAll(swapLocationsAdded);
                    updateCount += transactionUpdates;

                    // Check if there is another transaction to read
                    transactionIndicator = in.read();
                    if (transactionIndicator != TRANSACTION_FOLLOWS && transactionIndicator != JOURNAL_COMPLETE && transactionIndicator != -1) {
                        throw new IOException("After reading " + byteCountingIn.getBytesConsumed() + " bytes from " + journalFile + ", encountered unexpected value of "
                            + transactionIndicator + " for the Transaction Indicator. This journal may have been corrupted.");
                    }

                    // If we have a very large journal (for instance, if checkpoint is not called for a long time, or if there is a problem rolling over
                    // the journal), then we want to occasionally notify the user that we are, in fact, making progress, so that it doesn't appear that
                    // NiFi has become "stuck".
                    final long consumed = byteCountingIn.getBytesConsumed();
                    if (consumed - consumedAtLog > 50_000_000) {
                        final double percentage = consumed / journalLength * 100D;
                        final String pct = new DecimalFormat("#.00").format(percentage);
                        logger.info("{}% of the way finished recovering journal {}, having recovered {} updates", pct, journalFile, updateCount);
                        consumedAtLog = consumed;
                    }
                }
            } catch (final EOFException eof) {
                eofException = true;
                logger.warn("Encountered unexpected End-of-File when reading journal file {}; assuming that NiFi was shutdown unexpectedly and continuing recovery", journalFile);
            } catch (final Exception e) {
                // If the stream consists solely of NUL bytes, then we want to treat it
                // the same as an EOF because we see this happen when we suddenly lose power
                // while writing to a file. However, if that is not the case, then something else has gone wrong.
                // In such a case, there is not much that we can do but to re-throw the Exception.
                if (remainingBytesAllNul(in)) {
                    logger.warn("Failed to recover some of the data from Write-Ahead Log Journal because encountered trailing NUL bytes. "
                        + "This will sometimes happen after a sudden power loss. The rest of this journal file will be skipped for recovery purposes."
                        + "The following Exception was encountered while recovering the updates to the journal:", e);
                } else {
                    throw e;
                }
            }
        }

        logger.info("Successfully recovered {} updates from journal {}", updateCount, journalFile);
        return new StandardJournalRecovery(updateCount, maxTransactionId, eofException);
    }

}

```

### `WriteAheadSnapshot`

`WriteAheadSnapshot` 提供了 `checkpoint` 的功能。

```java
public interface WriteAheadSnapshot<T> {
    SnapshotCapture<T> prepareSnapshot(long maxTransactionId);

    SnapshotCapture<T> prepareSnapshot(long maxTransactionId, Set<String> swapLocations);

    void writeSnapshot(SnapshotCapture<T> snapshot) throws IOException;

    SnapshotRecovery<T> recover() throws IOException;

    void update(Collection<T> records);

    int getRecordCount();
}
```

因为 `checkpoint` 的时候需要获取 repository 中记录的 record，而编辑日志中存储的是事务，如果从编辑日志回放来获取 record，代价巨大。

`WriteAheadSnapshot` 实时维护 repository 中数据，提供内存形式的存储。

`WriteAheadSnapshot` 实现类为 `HashMapSnapshot`，提供以 `Map` 形式的存储。

snapshot 更新 record 的过程即是对 `recordMap` 进行增删改查操作。

```java
public enum UpdateType {

    CREATE,UPDATE,DELETE,SWAP_OUT,SWAP_IN;
}

public class HashMapSnapshot<T> implements WriteAheadSnapshot<T>, RecordLookup<T> {

	private final ConcurrentMap<Object, T> recordMap = new ConcurrentHashMap<>();

	@Override
    public void update(final Collection<T> records) {
        // This implementation of Snapshot keeps a ConcurrentHashMap of all 'active' records
        // (meaning records that have not been removed and are not swapped out), keyed by the
        // Record Identifier. It keeps only the most up-to-date version of the Record. This allows
        // us to write the snapshot very quickly without having to re-process the journal files.
        // For each update, then, we will update the record in the map.
        for (final T record : records) {
            final Object recordId = serdeFactory.getRecordIdentifier(record);
            final UpdateType updateType = serdeFactory.getUpdateType(record);

            switch (updateType) {
                case DELETE:
                    recordMap.remove(recordId);
                    break;
                case SWAP_OUT:
                    final String location = serdeFactory.getLocation(record);
                    if (location == null) {
                        logger.error("Received Record (ID=" + recordId + ") with UpdateType of SWAP_OUT but "
                            + "no indicator of where the Record is to be Swapped Out to; these records may be "
                            + "lost when the repository is restored!");
                    } else {
                        recordMap.remove(recordId);
                        this.swapLocations.add(location);
                    }
                    break;
                case SWAP_IN:
                    final String swapLocation = serdeFactory.getLocation(record);
                    if (swapLocation == null) {
                        logger.error("Received Record (ID=" + recordId + ") with UpdateType of SWAP_IN but no "
                            + "indicator of where the Record is to be Swapped In from; these records may be duplicated "
                            + "when the repository is restored!");
                    } else {
                        swapLocations.remove(swapLocation);
                    }
                    recordMap.put(recordId, record);
                    break;
                default:
                    recordMap.put(recordId, record);
                    break;
            }
        }
    }
}
```

`WriteAheadSnapshot` 进行 `checkpoint` 的过程：

* 通过 `#prepareSnapshot` 方法获取当前记录的 record
* `#writeSnapshot(SnapshotCapture)` 方法持久化当前记录的 record

`HashMapSnapshot` 使用 `Map` 做内存存储，获取当前的 record 比较简单。

```java
public class HashMapSnapshot<T> implements WriteAheadSnapshot<T>, RecordLookup<T> {

    private final ConcurrentMap<Object, T> recordMap = new ConcurrentHashMap<>();

    @Override
    public SnapshotCapture<T> prepareSnapshot(final long maxTransactionId) {
        return prepareSnapshot(maxTransactionId, this.swapLocations);
    }

    @Override
    public SnapshotCapture<T> prepareSnapshot(final long maxTransactionId, final Set<String> swapFileLocations) {
        return new Snapshot(new HashMap<>(recordMap), new HashSet<>(swapFileLocations), maxTransactionId);
    }

    public class Snapshot implements SnapshotCapture<T> {
        private final Map<Object, T> records;
        private final long maxTransactionId;
        private final Set<String> swapLocations;

        public Snapshot(final Map<Object, T> records, final Set<String> swapLocations, final long maxTransactionId) {
            this.records = records;
            this.swapLocations = swapLocations;
            this.maxTransactionId = maxTransactionId;
        }

        @Override
        public final Map<Object, T> getRecords() {
            return records;
        }

        @Override
        public long getMaxTransactionId() {
            return maxTransactionId;
        }

        @Override
        public Set<String> getSwapLocations() {
            return swapLocations;
        }
    }
}
```

写 checkpoint 的过程即是将 snapshot 中的数据写到磁盘文件中进行持久化存储。

* 向 `checkpoint.partial` 中写入 header 信息
* 写入所有的 record 和 swapLocation
* 将 `checkpoint.partial` 重命名为 `checkpoint`

```java
public class HashMapSnapshot<T> implements WriteAheadSnapshot<T>, RecordLookup<T> {

	private final File storageDirectory;

	@Override
    public synchronized void writeSnapshot(final SnapshotCapture<T> snapshot) throws IOException {
        final SerDe<T> serde = serdeFactory.createSerDe(null);

        final File snapshotFile = getSnapshotFile();
        final File partialFile = getPartialFile();

        // We must ensure that we do not overwrite the existing Snapshot file directly because if NiFi were
        // to be killed or crash when we are partially finished, we'd end up with no viable Snapshot file at all.
        // To avoid this, we write to a 'partial' file, then delete the existing Snapshot file, if it exists, and
        // rename 'partial' to Snaphsot. That way, if NiFi crashes, we can still restore the Snapshot by first looking
        // for a Snapshot file and restoring it, if it exists. If it does not exist, then we restore from the partial file,
        // assuming that NiFi crashed after deleting the Snapshot file and before renaming the partial file.
        //
        // If there is no Snapshot file currently but there is a Partial File, then this indicates
        // that we have deleted the Snapshot file and failed to rename the Partial File. We don't want
        // to overwrite the Partial file, because doing so could potentially lose data. Instead, we must
        // first rename it to Snapshot and then write to the partial file.
        if (!snapshotFile.exists() && partialFile.exists()) {
            final boolean rename = partialFile.renameTo(snapshotFile);
            if (!rename) {
                throw new IOException("Failed to rename partial snapshot file " + partialFile + " to " + snapshotFile);
            }
        }

        // Write to the partial file.
        try (final FileOutputStream fileOut = new FileOutputStream(getPartialFile());
            final OutputStream bufferedOut = new BufferedOutputStream(fileOut);
            final DataOutputStream dataOut = new DataOutputStream(bufferedOut)) {

            // Write out the header
            dataOut.writeUTF(HashMapSnapshot.class.getName());
            dataOut.writeInt(getVersion());
            dataOut.writeUTF(serde.getClass().getName());
            dataOut.writeInt(serde.getVersion());
            dataOut.writeLong(snapshot.getMaxTransactionId());
            dataOut.writeInt(snapshot.getRecords().size());
            serde.writeHeader(dataOut);

            // Serialize each record
            for (final T record : snapshot.getRecords().values()) {
                logger.trace("Checkpointing {}", record);
                serde.serializeRecord(record, dataOut);
            }

            // Write out the number of swap locations, followed by the swap locations themselves.
            dataOut.writeInt(snapshot.getSwapLocations().size());
            for (final String swapLocation : snapshot.getSwapLocations()) {
                dataOut.writeUTF(swapLocation);
            }

            // Ensure that we flush the Buffered Output Stream and then perform an fsync().
            // This ensures that the data is fully written to disk before we delete the existing snapshot.
            dataOut.flush();
            fileOut.getChannel().force(false);
        }

        // If the snapshot file exists, delete it
        if (snapshotFile.exists()) {
            if (!snapshotFile.delete()) {
                logger.warn("Unable to delete existing Snapshot file " + snapshotFile);
            }
        }

        // Rename the partial file to Snapshot.
        final boolean rename = partialFile.renameTo(snapshotFile);
        if (!rename) {
            throw new IOException("Failed to rename partial snapshot file " + partialFile + " to " + snapshotFile);
        }
    }

    private File getPartialFile() {
        return new File(storageDirectory, "checkpoint.partial");
    }

    private File getSnapshotFile() {
        return new File(storageDirectory, "checkpoint");
    }
}
```

checkpoint 文件执行 recovery 的流程和编辑日志类似，即是对写入 snapshot 的逆操作。

* 正常状态应为 `checkpoint` 文件，如果 `checkpoint.partial` 存在则说明 checkpoint 时发生异常，没有正常结束，将 `checkpoint.partial` 重命名为 `checkpoint` 文件，继续读取。如果 `checkpoint.partial` 和 `checkpoint` 都不存在，说明未发生过 checkpoint。
* 读取 header 信息
* 读取 record 数据
* 读取 swapLocations 数据

```java
public class HashMapSnapshot<T> implements WriteAheadSnapshot<T>, RecordLookup<T> {

    @Override
    public SnapshotRecovery<T> recover() throws IOException {
        final File partialFile = getPartialFile();
        final File snapshotFile = getSnapshotFile();
        final boolean partialExists = partialFile.exists();
        final boolean snapshotExists = snapshotFile.exists();

        // If there is no snapshot (which is the case before the first snapshot is ever created), then just
        // return an empty recovery.
        if (!partialExists && !snapshotExists) {
            return SnapshotRecovery.emptyRecovery();
        }

        if (partialExists && snapshotExists) {
            // both files exist -- assume NiFi crashed/died while checkpointing. Delete the partial file.
            Files.delete(partialFile.toPath());
        } else if (partialExists) {
            // partial exists but snapshot does not -- we must have completed
            // creating the partial, deleted the snapshot
            // but crashed before renaming the partial to the snapshot. Just
            // rename partial to snapshot
            Files.move(partialFile.toPath(), snapshotFile.toPath());
        }

        if (snapshotFile.length() == 0) {
            logger.warn("{} Found 0-byte Snapshot file; skipping Snapshot file in recovery", this);
            return SnapshotRecovery.emptyRecovery();
        }

        // At this point, we know the snapshotPath exists because if it didn't, then we either returned null
        // or we renamed partialPath to snapshotPath. So just Recover from snapshotPath.
        try (final DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(snapshotFile)))) {
            // Ensure that the header contains the information that we expect and retrieve the relevant information from the header.
            final SnapshotHeader header = validateHeader(dataIn);

            final SerDe<T> serde = header.getSerDe();
            final int serdeVersion = header.getSerDeVersion();
            final int numRecords = header.getNumRecords();
            final long maxTransactionId = header.getMaxTransactionId();

            // Read all of the records that we expect to receive.
            for (int i = 0; i < numRecords; i++) {
                final T record = serde.deserializeRecord(dataIn, serdeVersion);
                if (record == null) {
                    throw new EOFException();
                }

                final UpdateType updateType = serde.getUpdateType(record);
                if (updateType == UpdateType.DELETE) {
                    logger.warn("While recovering from snapshot, found record with type 'DELETE'; this record will not be restored");
                    continue;
                }

                logger.trace("Recovered from snapshot: {}", record);
                recordMap.put(serde.getRecordIdentifier(record), record);
            }

            // Determine the location of any swap files.
            final int numSwapRecords = dataIn.readInt();
            final Set<String> swapLocations = new HashSet<>();
            for (int i = 0; i < numSwapRecords; i++) {
                swapLocations.add(dataIn.readUTF());
            }
            this.swapLocations.addAll(swapLocations);

            logger.info("{} restored {} Records and {} Swap Files from Snapshot, ending with Transaction ID {}",
                new Object[] {this, numRecords, swapLocations.size(), maxTransactionId});

            return new StandardSnapshotRecovery<>(recordMap, swapLocations, snapshotFile, maxTransactionId);
        }
    }

    private SnapshotHeader validateHeader(final DataInputStream dataIn) throws IOException {
        final String snapshotClass = dataIn.readUTF();
        logger.debug("Snapshot Class Name for {} is {}", storageDirectory, snapshotClass);
        if (!snapshotClass.equals(HashMapSnapshot.class.getName())) {
            throw new IOException("Write-Ahead Log Snapshot located at " + storageDirectory + " was written using the "
                + snapshotClass + " class; cannot restore using " + getClass().getName());
        }

        final int snapshotVersion = dataIn.readInt();
        logger.debug("Snapshot version for {} is {}", storageDirectory, snapshotVersion);
        if (snapshotVersion > getVersion()) {
            throw new IOException("Write-Ahead Log Snapshot located at " + storageDirectory + " was written using version "
                + snapshotVersion + " of the " + snapshotClass + " class; cannot restore using Version " + getVersion());
        }

        final String serdeEncoding = dataIn.readUTF(); // ignore serde class name for now
        logger.debug("Serde encoding for Snapshot at {} is {}", storageDirectory, serdeEncoding);

        final int serdeVersion = dataIn.readInt();
        logger.debug("Serde version for Snapshot at {} is {}", storageDirectory, serdeVersion);

        final long maxTransactionId = dataIn.readLong();
        logger.debug("Max Transaction ID for Snapshot at {} is {}", storageDirectory, maxTransactionId);

        final int numRecords = dataIn.readInt();
        logger.debug("Number of Records for Snapshot at {} is {}", storageDirectory, numRecords);

        final SerDe<T> serde = serdeFactory.createSerDe(serdeEncoding);
        serde.readHeader(dataIn);

        return new SnapshotHeader(serde, serdeVersion, maxTransactionId, numRecords);
    }
}
```

### `SequentialAccessWriteAheadLog`

`Write-Ahead-Log` 的实现是日志和 checkpoint 功能完成的，`SequentialAccessWriteAheadLog` 将日志和 checkpoint 功能代理给 `LengthDelimitedJournal` 和 `HashMapSnapshot`。

`WriteAheadRepository` 的存储目录存储 checkpoint 文件，存储目录下的 `journals` 存储日志文件。

```java
public class SequentialAccessWriteAheadLog<T> implements WriteAheadRepository<T> {
    
    private final File storageDirectory;
    private final File journalsDirectory;
    
    private final WriteAheadSnapshot<T> snapshot;
    
    public SequentialAccessWriteAheadLog(final File storageDirectory, final SerDeFactory<T> serdeFactory, final SyncListener syncListener) throws IOException {
        if (!storageDirectory.exists() && !storageDirectory.mkdirs()) {
            throw new IOException("Directory " + storageDirectory + " does not exist and cannot be created");
        }
        if (!storageDirectory.isDirectory()) {
            throw new IOException("File " + storageDirectory + " is a regular file and not a directory");
        }

        final HashMapSnapshot<T> hashMapSnapshot = new HashMapSnapshot<>(storageDirectory, serdeFactory);
        this.snapshot = hashMapSnapshot;
        
        this.storageDirectory = storageDirectory;
        this.journalsDirectory = new File(storageDirectory, "journals");
        if (!journalsDirectory.exists() && !journalsDirectory.mkdirs()) {
            throw new IOException("Directory " + journalsDirectory + " does not exist and cannot be created");
        }

        this.serdeFactory = serdeFactory;
    }
}
```

向 repository 写入 record 流程如下：

* 写入编辑日志
* 写入快照

```java
public class SequentialAccessWriteAheadLog<T> implements WriteAheadRepository<T> {

	private final ReadWriteLock journalRWLock = new ReentrantReadWriteLock();
    private final Lock journalReadLock = journalRWLock.readLock();
    private final Lock journalWriteLock = journalRWLock.writeLock();

	@Override
    public int update(final Collection<T> records, final boolean forceSync) throws IOException {
        if (!recovered) {
            throw new IllegalStateException("Cannot update repository until record recovery has been performed");
        }

        journalReadLock.lock();
        try {
            journal.update(records, recordLookup);

            if (forceSync) {
                journal.fsync();
                syncListener.onSync(PARTITION_INDEX);
            }

            snapshot.update(records);
        } finally {
            journalReadLock.unlock();
        }

        return PARTITION_INDEX;
    }
}
```

checkpoint 的过程会锁定 repository，期间无法写入。

* 关闭当前编辑日志。关闭编辑日志会向日志尾部写入关闭标识：`LengthDelimitedJournal#JOURNAL_COMPLETE`
* 创建 `SnapshotCapture`。捕获当前快照中的 record
* 创建新的编辑日志。编辑日志名称为事务 id 的起点
* 完成快照捕获和创建新的编辑日志后，已经可以放开锁定，接收写入。
* 将捕获的快照写入 checkpoint 文件，进行持久化
* checkpoint 持久化后可以删除历史的编辑日志

```java
public class SequentialAccessWriteAheadLog<T> implements WriteAheadRepository<T> {

	private final ReadWriteLock journalRWLock = new ReentrantReadWriteLock();
    private final Lock journalReadLock = journalRWLock.readLock();
    private final Lock journalWriteLock = journalRWLock.writeLock();

	private WriteAheadJournal<T> journal;
    private volatile long nextTransactionId = 0L;

	@Override
    public int checkpoint() throws IOException {
        return checkpoint(null);
    }

    private int checkpoint(final Set<String> swapLocations) throws IOException {
        final SnapshotCapture<T> snapshotCapture;

        final long startNanos = System.nanoTime();
        final File[] existingJournals;
        journalWriteLock.lock();
        try {
            if (journal != null) {
                final JournalSummary journalSummary = journal.getSummary();
                if (journalSummary.getTransactionCount() == 0 && journal.isHealthy()) {
                    logger.debug("Will not checkpoint Write-Ahead Log because no updates have occurred since last checkpoint");
                    return snapshot.getRecordCount();
                }

                try {
                    journal.fsync();
                } catch (final Exception e) {
                    logger.error("Failed to synch Write-Ahead Log's journal to disk at {}", storageDirectory, e);
                }

                try {
                    journal.close();
                } catch (final Exception e) {
                    logger.error("Failed to close Journal while attempting to checkpoint Write-Ahead Log at {}", storageDirectory);
                }

                nextTransactionId = Math.max(nextTransactionId, journalSummary.getLastTransactionId() + 1);
            }

            syncListener.onGlobalSync();

            final File[] existingFiles = journalsDirectory.listFiles(this::isJournalFile);
            existingJournals = (existingFiles == null) ? new File[0] : existingFiles;

            if (swapLocations == null) {
                snapshotCapture = snapshot.prepareSnapshot(nextTransactionId - 1);
            } else {
                snapshotCapture = snapshot.prepareSnapshot(nextTransactionId - 1, swapLocations);
            }


            // Create a new journal. We name the journal file <next transaction id>.journal but it is possible
            // that we could have an empty journal file already created. If this happens, we don't want to create
            // a new file on top of it because it would get deleted below when we clean up old journals. So we
            // will simply increment our transaction ID and try again.
            File journalFile = new File(journalsDirectory, nextTransactionId + ".journal");
            while (journalFile.exists()) {
                nextTransactionId++;
                journalFile = new File(journalsDirectory, nextTransactionId + ".journal");
            }

            journal = new LengthDelimitedJournal<>(journalFile, serdeFactory, streamPool, nextTransactionId);
            journal.writeHeader();

            logger.debug("Created new Journal starting with Transaction ID {}", nextTransactionId);
        } finally {
            journalWriteLock.unlock();
        }

        final long stopTheWorldMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
        snapshot.writeSnapshot(snapshotCapture);

        for (final File existingJournal : existingJournals) {
            final WriteAheadJournal journal = new LengthDelimitedJournal<>(existingJournal, serdeFactory, streamPool, nextTransactionId);
            journal.dispose();
        }

        final long totalNanos = System.nanoTime() - startNanos;
        final long millis = TimeUnit.NANOSECONDS.toMillis(totalNanos);
        logger.info("Checkpointed Write-Ahead Log with {} Records and {} Swap Files in {} milliseconds (Stop-the-world time = {} milliseconds), max Transaction ID {}",
                snapshotCapture.getRecords().size(), snapshotCapture.getSwapLocations().size(), millis, stopTheWorldMillis, snapshotCapture.getMaxTransactionId());

        return snapshotCapture.getRecords().size();
    }

    private boolean isJournalFile(final File file) {
        if (!file.isFile()) {
            return false;
        }

        final String filename = file.getName();
        return JOURNAL_FILENAME_PATTERN.matcher(filename).matches();
    }
}

public class LengthDelimitedJournal<T> implements WriteAheadJournal<T> {

    private static final byte JOURNAL_COMPLETE = 127;

    @Override
    public synchronized void close() throws IOException {
        if (closed) {
            return;
        }

        closed = true;

        try {
            if (fileOut != null) {
                if (!isPoisoned()) {
                    fileOut.write(JOURNAL_COMPLETE);
                }

                fileOut.close();
            }
        } catch (final IOException ioe) {
            poison(ioe);
        }
    }
}
```

recovery 的过程：

* 读取 checkpoint
* 读取所有的日志文件，并按照文件名进行排序。日志文件名为事务的起始事务 id
* 比较快照的事务 id 和日志文件的起始事务 id，逐个回放日志文件
* 回放完毕后，重新进行 checkpoint

```java
public class SequentialAccessWriteAheadLog<T> implements WriteAheadRepository<T> {

	@Override
    public synchronized Collection<T> recoverRecords() throws IOException {
        if (recovered) {
            throw new IllegalStateException("Cannot recover records from repository because record recovery has already commenced");
        }

        logger.info("Recovering records from Write-Ahead Log at {}", storageDirectory);

        final long recoverStart = System.nanoTime();
        recovered = true;
        snapshotRecovery = snapshot.recover();
        this.recoveredSwapLocations.addAll(snapshotRecovery.getRecoveredSwapLocations());

        final long snapshotRecoveryMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - recoverStart);

        final Map<Object, T> recoveredRecords = snapshotRecovery.getRecords();
        final Set<String> swapLocations = snapshotRecovery.getRecoveredSwapLocations();

        final File[] journalFiles = journalsDirectory.listFiles(this::isJournalFile);
        if (journalFiles == null) {
            throw new IOException("Cannot access the list of files in directory " + journalsDirectory + "; please ensure that appropriate file permissions are set.");
        }

        if (snapshotRecovery.getRecoveryFile() == null) {
            logger.info("No Snapshot File to recover from at {}. Now recovering records from {} journal files", storageDirectory, journalFiles.length);
        } else {
            logger.info("Successfully recovered {} records and {} swap files from Snapshot at {} with Max Transaction ID of {} in {} milliseconds. Now recovering records from {} journal files",
                recoveredRecords.size(), swapLocations.size(), snapshotRecovery.getRecoveryFile(), snapshotRecovery.getMaxTransactionId(),
                snapshotRecoveryMillis, journalFiles.length);
        }

        final List<File> orderedJournalFiles = Arrays.asList(journalFiles);
        Collections.sort(orderedJournalFiles, new Comparator<File>() {
            @Override
            public int compare(final File o1, final File o2) {
                final long transactionId1 = getMinTransactionId(o1);
                final long transactionId2 = getMinTransactionId(o2);

                return Long.compare(transactionId1, transactionId2);
            }
        });

        final long snapshotTransactionId = snapshotRecovery.getMaxTransactionId();

        int totalUpdates = 0;
        int journalFilesRecovered = 0;
        int journalFilesSkipped = 0;
        long maxTransactionId = snapshotTransactionId;

        for (final File journalFile : orderedJournalFiles) {
            final long journalMinTransactionId = getMinTransactionId(journalFile);
            if (journalMinTransactionId < snapshotTransactionId) {
                logger.debug("Will not recover records from journal file {} because the minimum Transaction ID for that journal is {} and the Transaction ID recovered from Snapshot was {}",
                    journalFile, journalMinTransactionId, snapshotTransactionId);

                journalFilesSkipped++;
                continue;
            }

            logger.debug("Min Transaction ID for journal {} is {}, so will recover records from journal", journalFile, journalMinTransactionId);
            journalFilesRecovered++;

            try (final WriteAheadJournal<T> journal = new LengthDelimitedJournal<>(journalFile, serdeFactory, streamPool, 0L)) {
                final JournalRecovery journalRecovery = journal.recoverRecords(recoveredRecords, swapLocations);
                final int updates = journalRecovery.getUpdateCount();

                logger.debug("Recovered {} updates from journal {}", updates, journalFile);
                totalUpdates += updates;
                maxTransactionId = Math.max(maxTransactionId, journalRecovery.getMaxTransactionId());
            }
        }

        logger.debug("Recovered {} updates from {} journal files and skipped {} journal files because their data was already encapsulated in the snapshot",
            totalUpdates, journalFilesRecovered, journalFilesSkipped);
        this.nextTransactionId = maxTransactionId + 1;

        final long recoverNanos = System.nanoTime() - recoverStart;
        final long recoveryMillis = TimeUnit.MILLISECONDS.convert(recoverNanos, TimeUnit.NANOSECONDS);
        logger.info("Successfully recovered {} records in {} milliseconds. Now checkpointing to ensure that Write-Ahead Log is in a consistent state", recoveredRecords.size(), recoveryMillis);

        this.recoveredSwapLocations.addAll(swapLocations);

        checkpoint(this.recoveredSwapLocations);

        return recoveredRecords.values();
    }

    private long getMinTransactionId(final File journalFile) {
        final String filename = journalFile.getName();
        final String numeral = filename.substring(0, filename.indexOf("."));
        return Long.parseLong(numeral);
    }

}
```

