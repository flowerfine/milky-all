package cn.sliew.milky.common.explain;

import cn.sliew.milky.common.stopwatch.StopWatchs;
import cn.sliew.milky.common.stopwatch.Stopwatch;

import java.util.*;

/**
 * Expert: Describes the .
 */
public class Explanation {

    private String name;                                                 // node name
    private String description;                                          // what it represents
    private Stopwatch stopwatch = StopWatchs.createStarted();            // how long it executes
    private List<Explanation> details = new LinkedList<>();              // sub-explanations

    public Explanation() {
    }

    public Explanation(String name) {
        this.name = name;
    }

    /**
     * Create a new explanation
     */
    public Explanation(String description, Collection<Explanation> details) {
        this.description = Objects.requireNonNull(description);
        this.details = Collections.unmodifiableList(new ArrayList<>(details));
        for (Explanation detail : details) {
            Objects.requireNonNull(detail);
        }
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

    public Explanation withDetail(Explanation detail) {
        this.details.add(detail);
        return this;
    }

    private String summary() {
        return String.format("%s: %s, cost %dms", name(), description(), stopwatch.elapsed().toMillis());
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
