package cn.sliew.milky.property;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public interface Settings<T> extends Mergeable {

    /**
     * @return the name of this {@code SettingSource}.
     */
    String getName();

    /**
     * @return the underlying source object for this {@code SettingSource}.
     */
    T getSource();

    boolean isEmpty();
    int size();

    Set<String> getKeySet();

    default boolean contains(String setting) {
        return (get(setting) != null);
    }
    <E> E get(String setting);
    <E> E get(String setting, E defaultValue);

    Settings<T> getByPrefix(String prefix);
    Settings filter(Predicate<String> predicate);

    default Settings<T> getAsSettings(String setting) {
        return getByPrefix(setting + ".");
    }

    List getAsList(String setting);
    Map<String, Settings> getGroups(String settingsPrefix);
}
