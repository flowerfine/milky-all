package cn.sliew.milky.common.constant;

import java.util.Collection;

/**
 * todo Tag
 */
public interface TagSet {

    Collection<Tag> tags();

    void tag(Tag tag);

    boolean hasTag(Tag tag);
}
