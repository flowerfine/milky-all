package cn.sliew.milky.common.diff;

/**
 * Abstract diffable object with simple diffs implementation that sends the entire object if object has changed or
 * nothing if object remained the same.
 */
public abstract class AbstractDiffable<T extends Diffable<T>> implements Diffable<T> {

    private static final Diff<?> EMPTY = new CompleteDiff<>();

    @SuppressWarnings("unchecked")
    @Override
    public Diff<T> diff(T previousState) {
        if (this.equals(previousState)) {
            return (Diff<T>) EMPTY;
        } else {
            return new CompleteDiff<>((T) this);
        }
    }

}

