package cn.sliew.milky.property;

public interface Mergeable {

    Mergeable withFallback(Mergeable fallback);
}
