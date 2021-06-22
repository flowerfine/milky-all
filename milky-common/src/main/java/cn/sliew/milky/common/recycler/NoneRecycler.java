package cn.sliew.milky.common.recycler;

public class NoneRecycler<T> extends AbstractRecycler<T> {

    public NoneRecycler(Source<T> source) {
        super(source);
    }

    @Override
    public Value<T> obtain() {
        return new NoneValue<>(source.newInstance());
    }

    public static class NoneValue<T> implements Value<T> {

        T value;

        NoneValue(T value) {
            this.value = value;
        }

        @Override
        public T value() {
            return value;
        }

        @Override
        public boolean isRecycled() {
            return false;
        }

        @Override
        public void close() {
            if (value == null) {
                throw new IllegalStateException("recycler entry already released...");
            }
            value = null;
        }
    }
}
