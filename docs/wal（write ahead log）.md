# wal（write ahead log）

参考 `nifi` 的 `write ahead log` 实现

`nifi` 提供 `WriteAheadRepository` 用于为内存中的数据提供状态的持久化功能，以在系统重启时能够提供数据恢复功能。`WriteAheadRepository` 不提供任何的查询功能。

`wal` 将更新操作写入编辑日志，系统重启时可以通过回放编辑日志中的记录的更新操作恢复数据。如果编辑日志过于庞大，不仅会占据过多磁盘空间还会延长数据恢复时间，`nifi` 提供了 `checkpoint` 功能。当 `nifi` 将内存数据写入磁盘时可以移除对应的编辑日志，降低需要的存储空间。

执行过 `checkpoint` 后，修改会被继续新的编辑日志。当系统恢复时，首先加载 `checkpoint` 数据，然后回放编辑日志。

编辑日志可以通过 `partition` 划分为许多份。

编辑日志。实现类为 `WriteAheadJournal`。

日志文件记录日志头（`header`）信息，包括日志实现类、版本，序列化器实现类和版本，序列化器的 `header` 信息。

`checkpoint`。实现类为 `WriteAheadSnapshot`。

`WriteAheadSnapshot` 可以创建捕获快照中的数据，创建 `checkpoint` 文件，执行数据恢复。