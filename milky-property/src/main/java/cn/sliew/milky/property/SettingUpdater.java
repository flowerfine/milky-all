package cn.sliew.milky.property;

/**
 * Transactional interface to update settings.
 * @see Setting
 * @param <T> the type of the value of the setting
 */
public interface SettingUpdater<T> {

    /**
     * Returns true if this updaters setting has changed with the current update
     * @param current the current settings
     * @param previous the previous setting
     * @return true if this updaters setting has changed with the current update
     */
    boolean hasChanged(SettingSource current, SettingSource previous);

    /**
     * Returns the instance value for the current settings. This method is stateless and idempotent.
     * This method will throw an exception if the source of this value is invalid.
     */
    T getValue(SettingSource current, SettingSource previous);

    /**
     * Applies the given value to the updater. This methods will actually run the update.
     */
    void apply(T value, SettingSource current, SettingSource previous);

    /**
     * Updates this updaters value if it has changed.
     * @return <code>true</code> iff the value has been updated.
     */
    default boolean apply(SettingSource current, SettingSource previous) {
        if (hasChanged(current, previous)) {
            T value = getValue(current, previous);
            apply(value, current, previous);
            return true;
        }
        return false;
    }

    /**
     * Returns a callable runnable that calls {@link #apply(Object, SettingSource, SettingSource)} if the settings
     * actually changed. This allows to defer the update to a later point in time while keeping type safety.
     * If the value didn't change the returned runnable is a noop.
     */
    default Runnable updater(SettingSource current, SettingSource previous) {
        if (hasChanged(current, previous)) {
            T value = getValue(current, previous);
            return () -> { apply(value, current, previous);};
        }
        return () -> {};
    }
}
