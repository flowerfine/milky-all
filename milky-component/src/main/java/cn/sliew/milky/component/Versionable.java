package cn.sliew.milky.component;

import com.github.zafarkhaja.semver.Version;

/**
 * version marker interface.
 */
public interface Versionable {

    /**
     * @return component version
     */
    Version getVersion();
}
