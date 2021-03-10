package cn.sliew.milky.resource.source;

import java.io.Serializable;

/**
 * Source representation used to navigate to its location.
 *
 * <p>Marker interface. user need to check instances for concrete
 * subclasses or subinterfaces.
 *
 * <p>Implementations of this interface need to ensure that they are
 * <em>serializable</em> and <em>immutable</em> since they may be used as data
 * transfer objects.
 */
public interface Source extends Serializable {

}
