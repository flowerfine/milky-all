package cn.sliew.milky.common.chain;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public interface Pipeline<K, V, C extends Map<K, V>> {

    /**
     * Inserts a {@link PipelineProcess} at the first position of this pipeline.
     *
     * @param name    the name of the handler to insert first
     * @param command the command to insert first
     * @throws IllegalArgumentException if there's an entry with the same name already in the pipeline
     * @throws NullPointerException     if the specified handler is {@code null}
     */
    Pipeline<K, V, C> addFirst(String name, Command<K, V, C> command);

    /**
     * Appends a {@link PipelineProcess} at the last position of this pipeline.
     *
     * @param name    the name of the handler to append
     * @param handler the handler to append
     * @throws IllegalArgumentException if there's an entry with the same name already in the pipeline
     * @throws NullPointerException     if the specified handler is {@code null}
     */
    Pipeline<K, V, C> addLast(String name, Command<K, V, C> handler);

    /**
     * Inserts a {@link Command} before an existing handler of this
     * pipeline.
     *
     * @param baseName the name of the existing handler
     * @param name     the name of the handler to insert before
     * @param handler  the handler to insert before
     * @throws NoSuchElementException   if there's no such entry with the specified {@code baseName}
     * @throws IllegalArgumentException if there's an entry with the same name already in the pipeline
     * @throws NullPointerException     if the specified baseName or handler is {@code null}
     */
    Pipeline<K, V, C> addBefore(String baseName, String name, Command<K, V, C> handler);

    /**
     * Inserts a {@link Command} after an existing handler of this
     * pipeline.
     *
     * @param baseName the name of the existing handler
     * @param name     the name of the handler to insert after
     * @param handler  the handler to insert after
     * @throws NoSuchElementException   if there's no such entry with the specified {@code baseName}
     * @throws IllegalArgumentException if there's an entry with the same name already in the pipeline
     * @throws NullPointerException     if the specified baseName or handler is {@code null}
     */
    Pipeline<K, V, C> addAfter(String baseName, String name, Command<K, V, C> handler);

    /**
     * Removes the {@link Command} with the specified name from this pipeline.
     *
     * @param name the name under which the {@link Command} was stored.
     * @return the removed handler
     * @throws NoSuchElementException if there's no such handler with the specified name in this pipeline
     * @throws NullPointerException   if the specified name is {@code null}
     */
    Pipeline<K, V, C> remove(String name);

    /**
     * Removes the specified {@link Command} from this pipeline.
     *
     * @param handler the {@link PipelineProcess} to remove
     * @throws NoSuchElementException if there's no such handler in this pipeline
     * @throws NullPointerException   if the specified handler is {@code null}
     */
    Pipeline<K, V, C> remove(Command<K, V, C> handler);

    /**
     * Removes the {@link Command} of the specified type from this pipeline.
     *
     * @param <T>         the type of the handler
     * @param handlerType the type of the handler
     * @return the removed handler
     * @throws NoSuchElementException if there's no such handler of the specified type in this pipeline
     * @throws NullPointerException   if the specified handler type is {@code null}
     */
    <T extends Command> T remove(Class<T> handlerType);

    /**
     * Removes the first {@link Command} in this pipeline.
     *
     * @return the removed handler
     * @throws NoSuchElementException if this pipeline is empty
     */
    Command<K, V, C> removeFirst();

    /**
     * Removes the last {@link Command} in this pipeline.
     *
     * @return the removed handler
     * @throws NoSuchElementException if this pipeline is empty
     */
    Command<K, V, C> removeLast();

    /**
     * Returns the first {@link Command} in this pipeline.
     *
     * @return the first handler.  {@code null} if this pipeline is empty.
     */
    Command<K, V, C> first();

    /**
     * Returns the last {@link Command} in this pipeline.
     *
     * @return the last handler.  {@code null} if this pipeline is empty.
     */
    Command<K, V, C> last();

    /**
     * Returns the {@link Command} with the specified name in this
     * pipeline.
     *
     * @return the handler with the specified name.
     * {@code null} if there's no such handler in this pipeline.
     */
    Command<K, V, C> get(String name);

    /**
     * Returns the {@link Command} of the specified type in this
     * pipeline.
     *
     * @return the handler of the specified handler type.
     * {@code null} if there's no such handler in this pipeline.
     */
    <T extends Command> T get(Class<T> handlerType);

    /**
     * Returns the context of the first {@link Command} in this pipeline.
     *
     * @return the context of the first handler.  {@code null} if this pipeline is empty.
     */
    PipelineProcess<K, V, C> firstContext();

    /**
     * Returns the context of the last {@link Command} in this pipeline.
     *
     * @return the context of the last handler.  {@code null} if this pipeline is empty.
     */
    PipelineProcess<K, V, C> lastContext();

    /**
     * Returns the context object of the specified {@link Command} in
     * this pipeline.
     *
     * @return the context object of the specified handler.
     * {@code null} if there's no such handler in this pipeline.
     */
    PipelineProcess<K, V, C> context(Command<K, V, C> handler);

    /**
     * Returns the context object of the {@link Command} with the
     * specified name in this pipeline.
     *
     * @return the context object of the handler with the specified name.
     * {@code null} if there's no such handler in this pipeline.
     */
    PipelineProcess<K, V, C> context(String name);

    /**
     * Returns the context object of the {@link Command} of the
     * specified type in this pipeline.
     *
     * @return the context object of the handler of the specified type.
     * {@code null} if there's no such handler in this pipeline.
     */
    PipelineProcess<K, V, C> context(Class<? extends Command> handlerType);

    /**
     * Returns the {@link List} of the handler names.
     */
    List<String> names();

    /**
     * Converts this pipeline into an ordered {@link Map} whose keys are
     * handler names and whose values are handlers.
     */
    Map<String, Command<K, V, C>> toMap();

    Pipeline fireEvent();

    Pipeline fireExceptionCaught(Throwable cause);
}