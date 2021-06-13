package cn.sliew.milky.common.diff;

import java.util.Optional;

public class CompleteDiff<T extends Diffable<T>> implements Diff<T> {

    private final T part;

    /**
     * Creates simple diff with changes
     */
    CompleteDiff(T part) {
        this.part = part;
    }

    /**
     * Creates simple diff without changes
     */
    CompleteDiff() {
        this.part = null;
    }

    @Override
    public T apply(T part) {
        return Optional.of(this.part).orElse(part);
    }
}
