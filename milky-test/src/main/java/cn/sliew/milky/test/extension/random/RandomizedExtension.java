package cn.sliew.milky.test.extension.random;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

public class RandomizedExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final String RANDOMIZED_CONTEXT = "RandomizedContext";

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        RandomizedContext randomizedContext = RandomizedContext.current();
        randomizedContext.initializeContext(context);
        getStore(context).put(RANDOMIZED_CONTEXT, randomizedContext);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        getStore(context).remove(RANDOMIZED_CONTEXT, RandomizedContext.class);
        RandomizedContext.remove();
    }

    private Store getStore(ExtensionContext context) {
        return context.getStore(Namespace.create(getClass(), context.getRequiredTestMethod()));
    }
}
