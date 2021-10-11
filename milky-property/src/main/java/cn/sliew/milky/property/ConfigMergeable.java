package cn.sliew.milky.property;

public interface ConfigMergeable {

    ConfigMergeable withFallback(ConfigMergeable fallback);
}
