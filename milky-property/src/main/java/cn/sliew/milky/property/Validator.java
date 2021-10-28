package cn.sliew.milky.property;

import java.util.Collections;
import java.util.Iterator;

/**
 * Validation for {@link Setting}'s value.
 */
public interface Validator<T> {

    /**
     * Validate this setting's value.
     *
     * @param value   the value of this setting
     * @param context the validation context
     */
    ValidationResult validate(T value, ValidationContext context);

    /**
     * The settings on which the validity of this setting depends.
     * The values of the specified settings are passed to {@link #validate(Object, ValidationContext)}.
     * By default this returns an empty iterator, indicating that this setting does not
     * depend on any other settings.
     *
     * @return the settings on which the validity of this setting depends.
     */
    default Iterator<Setting<T>> settings() {
        return Collections.emptyIterator();
    }

}
