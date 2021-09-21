package cn.sliew.milky.thread;

class ExecutorUtil {

    private static int processors = Runtime.getRuntime().availableProcessors();

    private ExecutorUtil() {
        throw new UnsupportedOperationException("no instance");
    }

    /**
     * Constrains a value between minimum and maximum values
     * (inclusive).
     *
     * @param value the value to constrain
     * @param min   the minimum acceptable value
     * @param max   the maximum acceptable value
     * @return min if value is less than min, max if value is greater
     * than value, otherwise value
     */
    static int bounded(int value, int min, int max) {
        return Math.min(max, Math.max(min, value));
    }

    static int boundedByUpper(int value, int max) {
        return bounded(value, 1, max);
    }

    static int boundedBylower(int value, int min) {
        return bounded(value, min, Integer.MAX_VALUE);
    }

    static int halfProcessors() {
        return (processors + 1) / 2;
    }

    static int availableProcessors() {
        return processors;
    }

    static int oneAndhalfProcessors() {
        return ((processors * 3) / 2) + 1;
    }

    static int twiceProcessors() {
        return processors * 2;
    }


}
