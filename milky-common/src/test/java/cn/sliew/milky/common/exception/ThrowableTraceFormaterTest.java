package cn.sliew.milky.common.exception;

import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class ThrowableTraceFormaterTest extends MilkyTestCase {

    @Test
    void testPrintThrowableStackString1() {
        try {
            failure();
        } catch (Exception e) {
            System.err.println(ThrowableTraceFormater.readStackTrace(e));
        }
    }

    @Test
    void testPrintThrowableStackString2() {
        try {
            failure();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testPrintThrowableStackString3() {
        try {
            failure();
        } catch (Exception e) {
            ThrowableTraceFormater formater = new ThrowableTraceFormater();
            System.err.println(formater.formatThrowable(e));
        }
    }

    @Test
    void testPrintThrowableStackString4() {
        try {
            failure();
        } catch (Exception e) {
            List<String> strings = Arrays.asList("org.junit", "com.intellij");
            ThrowableTraceFormater formater = new ThrowableTraceFormater(strings);
            System.err.println(formater.formatThrowable(e));
        }
    }

    private void failure() {
        try {
            int i = 10 / 0;
        } catch (ArithmeticException e) {
            throw new IllegalStateException(e);
        }
    }
}
