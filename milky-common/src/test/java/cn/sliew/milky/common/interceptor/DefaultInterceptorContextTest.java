package cn.sliew.milky.common.interceptor;

import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class DefaultInterceptorContextTest extends MilkyTestCase {

    @Test
    public void testIntercept() {
        DefaultInterceptorContext<String, String> current = new DefaultInterceptorContext(Interceptor.EMPTY, null);
        current = new DefaultInterceptorContext(new ExecutorInterceptor(), current);
        current = new DefaultInterceptorContext(new FallbackInterceptor(), current);
        current = new DefaultInterceptorContext(new LogInterceptor(), current);
        String response = current.proceed("hello, intercepor!");

        // Interceptor.EMPTY return null.
        assertNull(response);
    }

    private static class LogInterceptor implements Interceptor<String, String> {

        @Override
        public String intercept(String s, InterceptorContext<String, String> context) {
            System.out.println("LogInterceptor before");
            String response = context.proceed(String.format("LogInterceptor(%s)", s));
            System.out.println("LogInterceptor after");
            return response;
        }
    }

    private static class FallbackInterceptor implements Interceptor<String, String> {

        @Override
        public String intercept(String s, InterceptorContext<String, String> context) {
            System.out.println("FallbackInterceptor before");
            String response = context.proceed(String.format("FallbackInterceptor(%s)", s));
            System.out.println("FallbackInterceptor after");
            return response;
        }
    }

    private static class ExecutorInterceptor implements Interceptor<String, String> {

        @Override
        public String intercept(String s, InterceptorContext<String, String> context) {
            System.out.println("ExecutorInterceptor before");
            String response = context.proceed(String.format("ExecutorInterceptor(%s)", s));
            System.out.println("ExecutorInterceptor after");
            return response;
        }
    }
}
