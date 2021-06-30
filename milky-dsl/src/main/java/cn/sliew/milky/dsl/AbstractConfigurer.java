package cn.sliew.milky.dsl;

import static cn.sliew.milky.common.check.Ensures.checkState;

/**
 * A base class for {@link Configurer} that allows subclasses to only implement
 * the methods they are interested in. It also provides a mechanism for using the
 * {@link Configurer} and when done gaining access to the {@link Builder}
 * that is being configured.
 *
 * @param <O> The Object being built by B
 * @param <B> The Builder that is building O and is configured by
 *            {@link AbstractConfigurer}
 */
public abstract class AbstractConfigurer<O, B extends Builder<O>> implements Configurer<O, B> {

    private B builder;

    @Override
    public void init(B builder) throws Exception {

    }

    @Override
    public void configure(B builder) throws Exception {

    }

    /**
     * Return the {@link Builder} when done using the {@link Configurer}.
     * This is useful for method chaining.
     *
     * @return the {@link Builder} for further customizations
     */
    public B and() {
        return getBuilder();
    }

    /**
     * Gets the {@link Builder}. Cannot be null.
     *
     * @return the {@link Builder}
     * @throws IllegalStateException if {@link Builder} is null
     */
    protected final B getBuilder() {
        checkState(this.builder != null, () -> "builder cannot be null");
        return this.builder;
    }

    /**
     * Sets the {@link Builder} to be used. This is automatically set when using
     * {@link AbstractConfiguredBuilder#apply(AbstractConfigurer)}
     *
     * @param builder the {@link Builder} to set
     */
    public void setBuilder(B builder) {
        this.builder = builder;
    }
}
