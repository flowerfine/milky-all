package cn.sliew.milky.common.explain;

import cn.sliew.milky.common.util.StringUtils;

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
    private long cost;                                                   // what it costs
    private Throwable cause;                                             // what exception it causes
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

    public Explanation name(String name) {
        this.name = name;
        return this;
    }

    /**
     * The time cost.
     */
    public long cost() {
        return cost;
    }

    public Explanation cost(long cost) {
        this.cost = cost;
        return this;
    }

    /**
     * A description of this explanation node.
     */
    public String description() {
        return description;
    }

    public Explanation description(String description) {
        this.description = description;
        return this;
    }

    /**
     * The throwable reason, maybe null.
     */
    public Throwable cause() {
        return cause;
    }

    public Explanation cause(Throwable cause) {
        this.cause = cause;
        return this;
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
        StringBuilder sb = new StringBuilder();
        sb.append(name());
        if (StringUtils.isNotBlank(description())) {
            sb.append(": ");
            sb.append(description());
        }
        if (cause() != null) {
            sb.append(" (exception: ");
            sb.append(cause());
            sb.append(")");
        }
        if (cost() > 0) {
            sb.append(" -- ");
            sb.append(cost());
            sb.append("ms");
        }

        return sb.toString();
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
