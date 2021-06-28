package cn.sliew.milky.dsl;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractBuilder<O> implements Builder<O> {

    private AtomicBoolean started = new AtomicBoolean();

    private O object;

    @Override
    public final O build() throws Exception {
        if (this.started.compareAndSet(false, true)) {
            this.object = doBuild();
            return this.object;
        }
        throw new AlreadyBuiltException("This object has already been started to build");
    }

    /**
     * Gets the object that was built. If it has not been started to build yet an Exception is
     * thrown.
     *
     * @return the Object that was built
     */
    public final O getObject() {
        if (!this.started.get()) {
            throw new IllegalStateException("This object has not been started to build");
        }
        return this.object;
    }

    /**
     * Subclasses should implement this to perform the build.
     *
     * @return the object that should be returned by {@link #build()}.
     * @throws Exception if an error occurs
     */
    protected abstract O doBuild() throws Exception;
}
