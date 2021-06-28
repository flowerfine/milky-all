package cn.sliew.milky.dsl;

import cn.sliew.milky.log.Logger;
import cn.sliew.milky.log.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;
import static cn.sliew.milky.common.check.Ensures.checkState;

/**
 * <p>
 * A base {@link Builder} that allows {@link Configurer} to be applied to
 * it. This makes modifying the {@link Builder} a strategy that can be customized
 * and broken up into a number of {@link Configurer} objects that have more
 * specific goals than that of the {@link Builder}.
 * </p>
 *
 * @param <O> The object that this builder returns
 * @param <B> The type of this builder (that is returned by the base class)
 */
public abstract class AbstractConfiguredBuilder<O, B extends ConfigurableBuilder<O, B>> extends AbstractBuilder<O>
        implements ConfigurableBuilder<O, B> {

    private static final Logger log = LoggerFactory.getLogger(AbstractConfiguredBuilder.class);

    /**
     * 使用 {@link LinkedHashMap} 的原因是为了保证配置顺序与执行顺序一致。
     */
    private final Map<Class<? extends Configurer<O, B>>, Configurer<O, B>> configurers = new LinkedHashMap<>();

    private BuildState buildState = BuildState.UNBUILT;

    /**
     * Similar to {@link #build()} and {@link #getObject()} but checks the state to
     * determine if {@link #build()} needs to be called first.
     *
     * @return the result of {@link #build()} or {@link #getObject()}. If an error occurs
     * while building, returns null.
     */
    public O getOrBuild() {
        if (!isUnbuilt()) {
            return getObject();
        }
        try {
            return build();
        } catch (Exception ex) {
            this.log.debug("Failed to perform build. Returning null", ex);
            return null;
        }
    }

    /**
     * Executes the build using the {@link Configurer}'s that have been applied
     * using the following steps:
     *
     * <ul>
     * <li>Invokes {@link #beforeInit()} for any subclass to hook into</li>
     * <li>Invokes {@link Configurer#init(Builder)} for any
     * {@link Configurer} that was applied to this builder.</li>
     * <li>Invokes {@link #beforeConfigure()} for any subclass to hook into</li>
     * <li>Invokes {@link #performBuild()} which actually builds the Object</li>
     * </ul>
     */
    @Override
    protected final O doBuild() throws Exception {
        synchronized (this.configurers) {
            this.buildState = BuildState.INITIALIZING;
            beforeInit();
            init();
            this.buildState = BuildState.CONFIGURING;
            beforeConfigure();
            configure();
            this.buildState = BuildState.BUILDING;
            O result = performBuild();
            this.buildState = BuildState.BUILT;
            return result;
        }
    }

    /**
     * Invoked prior to invoking each {@link Configurer#init(Builder)}
     * method. Subclasses may override this method to hook into the lifecycle without
     * using a {@link Configurer}.
     */
    protected void beforeInit() throws Exception {
    }

    /**
     * Invoked prior to invoking each
     * {@link Configurer#configure(Builder)} method. Subclasses may
     * override this method to hook into the lifecycle without using a
     * {@link Configurer}.
     */
    protected void beforeConfigure() throws Exception {
    }

    /**
     * Subclasses must implement this method to build the object that is being returned.
     *
     * @return the Object to be buit or null if the implementation allows it
     */
    protected abstract O performBuild() throws Exception;

    @SuppressWarnings("unchecked")
    private void init() throws Exception {
        Collection<Configurer<O, B>> configurers = getConfigurers();
        for (Configurer<O, B> configurer : configurers) {
            configurer.init((B) this);
        }
    }

    @SuppressWarnings("unchecked")
    private void configure() throws Exception {
        Collection<Configurer<O, B>> configurers = getConfigurers();
        for (Configurer<O, B> configurer : configurers) {
            configurer.configure((B) this);
        }
    }

    /**
     * Applies a {@link AbstractConfigurer} to this {@link Builder} and
     * invokes {@link AbstractConfigurer#setBuilder(Builder)}.
     *
     * @param configurer
     * @return the {@link AbstractConfigurer} for further customizations
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public <C extends AbstractConfigurer<O, B>> C apply(C configurer) throws Exception {
        configurer.setBuilder((B) this);
        add(configurer);
        return configurer;
    }

    /**
     * Applies a {@link Configurer} to this {@link Builder} overriding any
     * {@link Configurer} of the exact same class. Note that object hierarchies
     * are not considered.
     *
     * @param configurer
     * @return the {@link AbstractConfigurer} for further customizations
     * @throws Exception
     */
    public <C extends Configurer<O, B>> C apply(C configurer) throws Exception {
        add(configurer);
        return configurer;
    }

    /**
     * Adds {@link Configurer} ensuring that it is allowed and invoking
     * {@link Configurer#init(Builder)} immediately if necessary.
     *
     * @param configurer the {@link Configurer} to add
     */
    @SuppressWarnings("unchecked")
    private <C extends Configurer<O, B>> void add(C configurer) {
        checkNotNull(configurer, () -> "configurer cannot be null");

        Class<? extends Configurer<O, B>> clazz = (Class<? extends Configurer<O, B>>) configurer.getClass();
        synchronized (this.configurers) {
            checkState(!this.buildState.isConfigured(), () -> "Cannot apply " + configurer + " to already built object");

            this.configurers.put(clazz, configurer);
        }
    }

    /**
     * Gets the {@link Configurer} by its class name or <code>null</code> if not
     * found. Note that object hierarchies are not considered.
     *
     * @param clazz
     * @return the {@link Configurer} for further customizations
     */
    @SuppressWarnings("unchecked")
    public <C extends Configurer<O, B>> C getConfigurer(Class<C> clazz) {
        return (C) this.configurers.get(clazz);
    }

    /**
     * Removes and returns the {@link Configurer} by its class name or
     * <code>null</code> if not found. Note that object hierarchies are not considered.
     *
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public <C extends Configurer<O, B>> C removeConfigurer(Class<C> clazz) {
        return (C) this.configurers.remove(clazz);
    }

    private Collection<Configurer<O, B>> getConfigurers() {
        return this.configurers.values();
    }

    /**
     * Determines if the object is unbuilt.
     *
     * @return true, if unbuilt else false
     */
    private boolean isUnbuilt() {
        synchronized (this.configurers) {
            return this.buildState == BuildState.UNBUILT;
        }
    }

    /**
     * The build state for the application
     */
    private enum BuildState {

        /**
         * This is the state before the {@link Builder#build()} is invoked
         */
        UNBUILT(0),

        /**
         * The state from when {@link Builder#build()} is first invoked until all the
         * {@link Configurer#init(Builder)} methods have been invoked.
         */
        INITIALIZING(1),

        /**
         * The state from after all {@link Configurer#init(Builder)} have
         * been invoked until after all the
         * {@link Configurer#configure(Builder)} methods have been
         * invoked.
         */
        CONFIGURING(2),

        /**
         * From the point after all the
         * {@link Configurer#configure(Builder)} have completed to just
         * after {@link AbstractConfiguredBuilder#performBuild()}.
         */
        BUILDING(3),

        /**
         * After the object has been completely built.
         */
        BUILT(4);

        private final int order;

        BuildState(int order) {
            this.order = order;
        }

        public boolean isInitializing() {
            return INITIALIZING.order == this.order;
        }

        /**
         * Determines if the state is CONFIGURING or later
         *
         * @return
         */
        public boolean isConfigured() {
            return this.order >= CONFIGURING.order;
        }

    }
}
