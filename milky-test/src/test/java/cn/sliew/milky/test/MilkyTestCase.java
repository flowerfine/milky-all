package cn.sliew.milky.test;

import org.junit.jupiter.api.*;

public class MilkyTestCase {

    @Test
    public void testReporterInfoInject(TestReporter reporter) {
        reporter.publishEntry(getClass().getSimpleName());
    }

    @Test
    public void testTestInfoInject(TestInfo test) {
        System.out.println(test);
    }

    @RepeatedTest(10)
    public void testRepetitionInfoInject(RepetitionInfo repetition) {
        System.out.println(repetition);
    }

}
