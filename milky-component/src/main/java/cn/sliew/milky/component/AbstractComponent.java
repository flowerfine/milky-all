package cn.sliew.milky.component;

import cn.sliew.milky.common.constant.*;

import java.util.Collection;
import java.util.UUID;

public abstract class AbstractComponent implements Component {

    private final String name;
    private final String identifier;

    private final DefaultAttributeMap attributeMap = new DefaultAttributeMap();
    private final DefaultTagSet tagSet = new DefaultTagSet();

    public AbstractComponent(String name) {
        this.name = name;
        this.identifier = UUID.randomUUID().toString();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public <T> Collection<Attribute<T>> attrs() {
        return attributeMap.attrs();
    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {
        return attributeMap.attr(key);
    }

    @Override
    public <T> boolean hasAttr(AttributeKey<T> key) {
        return attributeMap.hasAttr(key);
    }

    @Override
    public Collection<Tag> tags() {
        return tagSet.tags();
    }

    @Override
    public void tag(Tag tag) {
        tagSet.tag(tag);
    }

    @Override
    public boolean hasTag(Tag tag) {
        return tagSet.hasTag(tag);
    }
}
