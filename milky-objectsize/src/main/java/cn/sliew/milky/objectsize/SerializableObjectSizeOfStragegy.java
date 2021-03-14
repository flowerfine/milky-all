package cn.sliew.milky.objectsize;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class SerializableObjectSizeOfStragegy implements ObjectSizeOfStrategy {

    /**
     * Records the size of an object and it's dependents as if they were serialized.
     */
    private final class SizeRecodingOutputStream extends ObjectOutputStream {

        private long totalSize = 0;

        /**
         * Construct.
         *
         * @throws IOException
         */
        public SizeRecodingOutputStream() throws IOException {
            super(new OutputStream() {

                @Override
                public void write(int b) throws IOException {
                }
            });
            enableReplaceObject(true);
        }

        /**
         * Gets the calculated size.
         *
         * @return
         */
        public long getTotalSize() {
            return totalSize;
        }

        @Override
        protected Object replaceObject(Object obj) throws IOException {
            if (obj != null) {
                totalSize += sizeOf(obj);
            }
            return obj;
        }
    }

    @Override
    public long sizeOf(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof Serializable) {
            try {
                SizeRecodingOutputStream recorder = new SizeRecodingOutputStream();
                recorder.writeObject(obj);
                return recorder.getTotalSize();
            } catch (IOException e) {
                return -1;
            }
        }
        return -1;
    }
}
