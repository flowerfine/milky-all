package cn.sliew.milky.property;

public interface Key {

    String getName();

    /**
     * foo, bar, foo.bar.*, foo.*.bar
     */
    String getDisplayName();

    String getDescription();
}
