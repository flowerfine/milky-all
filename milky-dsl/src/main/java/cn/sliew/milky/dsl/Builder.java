package cn.sliew.milky.dsl;

/**
 * Interface for building an Object
 *
 * @param <O> The type of the Object being built
 */
public interface Builder<O> {

    /**
     * Builds the object and returns it or null.
     *
     * @return the Object to be built or null if the implementation allows it.
     * @throws Exception if an error occurred when building the Object
     */
    O build() throws Exception;
}
