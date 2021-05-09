package cn.sliew.milky.config;

public interface PropertyPlaceholderResolver extends PropertyResolver {

    /**
     * Resolve ${...} placeholders in the given text, replacing them with corresponding
     * property values as resolved by {@link #getProperty}. Unresolvable placeholders with
     * no default value are ignored and passed through unchanged.
     *
     * @param text the String to resolve
     * @return the resolved String (never {@code null})
     * @throws IllegalArgumentException if given text param is {@code null}
     * @see #resolveRequiredPlaceholders
     * @see #setPlaceholderPrefix(String)
     * @see #setPlaceholderSuffix(String)
     * @see #setValueSeparator(String)
     */
    String resolvePlaceholders(String text);

    /**
     * Resolve ${...} placeholders in the given text, replacing them with corresponding
     * property values as resolved by {@link #getProperty}. Unresolvable placeholders with
     * no default value will cause an IllegalArgumentException to be thrown.
     *
     * @return the resolved String (never {@code null})
     * @throws IllegalArgumentException if given text is {@code null}
     *                                  or if any placeholders are unresolvable
     * @see #setPlaceholderPrefix(String)
     * @see #setPlaceholderSuffix(String)
     * @see #setValueSeparator(String)
     */
    String resolveRequiredPlaceholders(String text) throws IllegalArgumentException;

    /**
     * Set the prefix that placeholders replaced by this resolver must begin with.
     */
    void setPlaceholderPrefix(String placeholderPrefix);

    /**
     * Set the suffix that placeholders replaced by this resolver must end with.
     */
    void setPlaceholderSuffix(String placeholderSuffix);

    /**
     * Specify the separating character between the placeholders replaced by this
     * resolver and their associated default value, or {@code null} if no such
     * special character should be processed as a value separator.
     */
    void setValueSeparator(String valueSeparator);

}
