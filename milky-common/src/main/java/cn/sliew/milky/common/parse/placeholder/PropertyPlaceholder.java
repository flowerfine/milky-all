package cn.sliew.milky.common.parse.placeholder;

import cn.sliew.milky.common.primitives.Strings;
import cn.sliew.milky.log.Logger;

import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;
import static cn.sliew.milky.common.check.Ensures.notBlank;

/**
 * Utility class for working with Strings that have placeholder values in them. A placeholder takes the form
 * {@code ${name}}. Using {@code PropertyPlaceholder} these placeholders can be substituted for
 * user-supplied values.
 * <p/>
 * Values for substitution can be supplied using a {@link Properties} instance or using a
 * {@link PropertyPlaceholder.PlaceholderResolver}.
 */
public class PropertyPlaceholder {

    private final Logger log;

    private final String placeholderPrefix;
    private final String placeholderSuffix;
    private final Optional<String> valueSeparator;
    private final boolean ignoreUnresolvablePlaceholders;

    /**
     * Creates a new {@code PropertyPlaceholderHelper} that uses the supplied prefix and suffix.
     * Unresolvable placeholders are ignored.
     *
     * @param placeholderPrefix the prefix that denotes the start of a placeholder
     * @param placeholderSuffix the suffix that denotes the end of a placeholder
     */
    public PropertyPlaceholder(Logger log, String placeholderPrefix, String placeholderSuffix) {
        this(log, placeholderPrefix, placeholderSuffix, null, true);
    }

    /**
     * Creates a new {@code PropertyPlaceholderHelper} that uses the supplied prefix and suffix.
     *
     * @param placeholderPrefix              the prefix that denotes the start of a placeholder
     * @param placeholderSuffix              the suffix that denotes the end of a placeholder
     * @param valueSeparator                 the separating character between the placeholder variable
     *                                       and the associated default value, if any
     * @param ignoreUnresolvablePlaceholders indicates whether unresolvable placeholders should
     *                                       be ignored ({@code true}) or cause an exception ({@code false})
     */
    public PropertyPlaceholder(Logger log,
                               String placeholderPrefix,
                               String placeholderSuffix,
                               String valueSeparator,
                               boolean ignoreUnresolvablePlaceholders) {
        checkNotNull(log, () -> "'log' must not be null");
        notBlank(placeholderPrefix, () -> "'placeholderPrefix' must not be blank");
        notBlank(placeholderSuffix, () -> "'placeholderSuffix' must not be blank");

        this.log = log;
        this.placeholderPrefix = placeholderPrefix;
        this.placeholderSuffix = placeholderSuffix;
        this.valueSeparator = Optional.ofNullable(valueSeparator);
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }

    /**
     * Replaces all placeholders of format {@code ${name}} with the corresponding
     * property from the supplied {@link Properties}.
     *
     * @param value      the value containing the placeholders to be replaced
     * @param properties the {@code Properties} to use for replacement
     * @return the supplied value with placeholders replaced inline
     */
    public String replacePlaceholders(String value, final Properties properties) {
        checkNotNull(properties, () -> "'properties' must not be null");

        PlaceholderResolver placeholderResolver = new PlaceholderResolver() {
            @Override
            public Optional<String> resolvePlaceholder(String placeholderName) {
                return Optional.ofNullable(properties.getProperty(placeholderName));
            }

            @Override
            public boolean shouldIgnoreMissing(String placeholderName) {
                return false;
            }

            @Override
            public boolean shouldRemoveMissingPlaceholder(String placeholderName) {
                return true;
            }
        };
        return replacePlaceholders(value, placeholderResolver);
    }

    /**
     * Replaces all placeholders of format <code>${name}</code> with the value returned from the supplied {@link PlaceholderResolver}.
     *
     * @param value               the value containing the placeholders to be replaced.
     * @param placeholderResolver the <code>PlaceholderResolver</code> to use for replacement.
     * @return the supplied value with placeholders replaced inline.
     * @throws NullPointerException if value is null
     */
    public String replacePlaceholders(String value, PlaceholderResolver placeholderResolver) {
        notBlank(value, () -> "'value' must not be blank");

        return parseStringValue(value, placeholderResolver, new HashSet<>(4));
    }

    protected String parseStringValue(String value,
                                      PlaceholderResolver placeholderResolver,
                                      Set<String> visitedPlaceholders) {
        int startIndex = value.indexOf(this.placeholderPrefix);
        if (startIndex == -1) {
            return value;
        }

        int offset = 0;
        StringBuilder result = new StringBuilder(value);
        while (startIndex != -1) {
            if (startIndex > 0 && result.charAt(startIndex - 1) == '\\') {
                // this placeholderPrefix is escaped by backslash, remove the backslash and continue.
                result.deleteCharAt(startIndex - 1);
                offset = startIndex + placeholderPrefix.length();
                startIndex = result.indexOf(this.placeholderPrefix, offset);
                continue;
            } else {
                offset = startIndex + placeholderPrefix.length();
            }
            int endIndex = findPlaceholderEndIndex(result, offset);
            if (endIndex != -1) {
                String placeholder = result.substring(startIndex + this.placeholderPrefix.length(), endIndex);
                String originalPlaceholder = placeholder;
                if (!visitedPlaceholders.add(originalPlaceholder)) {
                    throw new IllegalArgumentException(
                            "Circular placeholder reference '" + originalPlaceholder + "' in property definitions");
                }
                // Recursive invocation, parsing placeholders contained in the placeholder key.
                placeholder = parseStringValue(placeholder, placeholderResolver, visitedPlaceholders);

                // Now obtain the value for the fully resolved key...
                Optional<String> optionalPropVal = placeholderResolver.resolvePlaceholder(placeholder);
                if (!optionalPropVal.isPresent() && this.valueSeparator.isPresent()) {
                    int separatorIndex = placeholder.indexOf(this.valueSeparator.get());
                    if (separatorIndex != -1) {
                        String actualPlaceholder = placeholder.substring(0, separatorIndex);
                        String defaultValue = placeholder.substring(separatorIndex + this.valueSeparator.get().length());
                        optionalPropVal = placeholderResolver.resolvePlaceholder(actualPlaceholder);
                        if (!optionalPropVal.isPresent()) {
                            optionalPropVal = Optional.ofNullable(defaultValue);
                        }
                    }
                }
                if (!optionalPropVal.isPresent() && placeholderResolver.shouldIgnoreMissing(placeholder)) {
                    if (placeholderResolver.shouldRemoveMissingPlaceholder(placeholder)) {
                        optionalPropVal = Optional.of(Strings.empty());
                    } else {
                        return value;
                    }
                }
                if (optionalPropVal.isPresent()) {
                    // Recursive invocation, parsing placeholders contained in the
                    // previously resolved placeholder value.
                    String propertyValue = optionalPropVal.get();
                    propertyValue = parseStringValue(propertyValue, placeholderResolver, visitedPlaceholders);
                    result.replace(startIndex, endIndex + this.placeholderSuffix.length(), propertyValue);
                    if (log.isTraceEnabled()) {
                        log.trace("Resolved placeholder '" + placeholder + "'");
                    }
                    startIndex = result.indexOf(this.placeholderPrefix, startIndex + propertyValue.length());
                } else if (this.ignoreUnresolvablePlaceholders) {
                    // Proceed with unprocessed value.
                    startIndex = result.indexOf(this.placeholderPrefix, endIndex + this.placeholderSuffix.length());
                } else {
                    throw new IllegalArgumentException("Could not resolve placeholder '" +
                            placeholder + "'" + " in value \"" + value + "\"");
                }
                visitedPlaceholders.remove(originalPlaceholder);
            } else {
                startIndex = -1;
            }
        }
        return result.toString();
    }

    private int findPlaceholderEndIndex(StringBuilder buf, int offset) {
        int withinNestedPlaceholder = 0;
        while (offset < buf.length()) {
            if (Strings.substringMatch(buf, offset, this.placeholderSuffix)) {
                if (offset > 0 && buf.charAt(offset - 1) == '\\') {
                    // this placeholderSuffix is escaped by backslash, remove the backslash and continue.
                    buf.deleteCharAt(offset - 1);
                    offset = offset - 1 + this.placeholderSuffix.length();
                    continue;
                }
                if (withinNestedPlaceholder > 0) {
                    withinNestedPlaceholder--;
                    offset = offset + this.placeholderSuffix.length();
                } else {
                    return offset;
                }
            } else if (Strings.substringMatch(buf, offset, this.placeholderPrefix)) {
                withinNestedPlaceholder++;
                offset = offset + this.placeholderPrefix.length();
            } else {
                offset++;
            }
        }
        return -1;
    }

    /**
     * Strategy interface used to resolve replacement values for placeholders contained in Strings.
     *
     * @see PropertyPlaceholder
     */
    public interface PlaceholderResolver {

        /**
         * Resolves the supplied placeholder name into the replacement value.
         *
         * @param placeholderName the name of the placeholder to resolve.
         * @return the replacement value or <code>null</code> if no replacement is to be made.
         */
        Optional<String> resolvePlaceholder(String placeholderName);

        boolean shouldIgnoreMissing(String placeholderName);

        /**
         * Allows for special handling for ignored missing placeholders that may be resolved elsewhere.
         *
         * @param placeholderName the name of the placeholder to resolve.
         * @return true if the placeholder should be replaced with a empty string
         */
        boolean shouldRemoveMissingPlaceholder(String placeholderName);
    }

}
