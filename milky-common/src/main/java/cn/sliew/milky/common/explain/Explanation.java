package cn.sliew.milky.common.explain;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Expert: Describes the .
 */
public class Explanation {

    public static final String DEFAULT_NAME = "default";
    public static final String DEFAULT_DESCRIPTION = "";

    private String name;                                                 // node name
    private String description;                                          // what it represents
    private List<Explanation> details = new LinkedList<>();              // sub-explanations

    public Explanation() {
        this(DEFAULT_NAME, DEFAULT_DESCRIPTION);
    }

    public Explanation(String name) {
        this(name, DEFAULT_DESCRIPTION);
    }

    public Explanation(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * A name of this explanation node.
     */
    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * A description of this explanation node.
     */
    public String description() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The sub-nodes of this explanation node.
     */
    public Explanation[] details() {
        return details.toArray(new Explanation[0]);
    }

    public Explanation withDetail(Explanation... details) {
        this.details.addAll(Arrays.asList(details));
        return this;
    }

    private String summary() {
        return String.format("%s: %s", name(), description());
    }

    /**
     * Render an explanation as text.
     */
    @Override
    public String toString() {
        return toString(0);
    }

    private String toString(int depth) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            buffer.append("  ");
        }
        buffer.append(summary());
        buffer.append("\n");

        Explanation[] details = details();
        for (int i = 0; i < details.length; i++) {
            buffer.append(details[i].toString(depth + 1));
        }

        return buffer.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Explanation that = (Explanation) o;
        return Objects.equals(description, that.description) &&
                Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, details);
    }

}