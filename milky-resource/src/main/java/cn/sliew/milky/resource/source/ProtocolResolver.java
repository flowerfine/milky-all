package cn.sliew.milky.resource.source;

@FunctionalInterface
public interface ProtocolResolver {

    Resource resolve(String resource, ResourceLoader loader);
}