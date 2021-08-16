package cn.sliew.milky.common.constant;

import java.util.Set;

/**
 * todo Tag
 */
public interface TagSet {

    Set<Tag> tags();

    boolean hasTag(String tag);
}
