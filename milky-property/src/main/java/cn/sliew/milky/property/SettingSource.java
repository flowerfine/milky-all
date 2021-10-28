package cn.sliew.milky.property;

import java.util.Set;

public interface SettingSource extends Mergeable {

    boolean isEmpty();
    int size();

    Set<String> getKeySet();
}
