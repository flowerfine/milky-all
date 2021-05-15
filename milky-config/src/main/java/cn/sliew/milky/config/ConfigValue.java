package cn.sliew.milky.config;

public interface ConfigValue {

    String getPropertyName();

    String getPropertyValue();

    String getPropertyRawValue();

    String getSourceName();

    int getSourceOrdinal();
}
