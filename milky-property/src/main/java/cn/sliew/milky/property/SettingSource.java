package cn.sliew.milky.property;

import java.util.Set;

public interface SettingSource {

    boolean isEmpty();
    int size();

    Set<String> getKeySet();
}
