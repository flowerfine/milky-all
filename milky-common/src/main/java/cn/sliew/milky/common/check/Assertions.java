package cn.sliew.milky.common.check;

/**
 * Provides a static final field that can be used to check if assertions are enabled. Since this field might be used elsewhere to check if
 * assertions are enabled, if you are running with assertions enabled for specific packages or classes, you should enable assertions on this
 * class too (e.g., {@code -ea org.elasticsearch.Assertions -ea org.elasticsearch.cluster.service.MasterService}).
 */
public final class Assertions {

    private Assertions() {

    }

    public static final boolean ENABLED;

    static {
        boolean enabled = false;
        /*
         * If assertions are enabled, the following line will be evaluated and enabled will have the value true, otherwise when assertions
         * are disabled enabled will have the value false.
         */
        // noinspection ConstantConditions,AssertWithSideEffects
        assert enabled = true;
        // noinspection ConstantConditions
        ENABLED = enabled;
    }

}