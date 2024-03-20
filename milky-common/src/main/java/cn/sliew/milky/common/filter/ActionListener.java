package cn.sliew.milky.common.filter;

import cn.sliew.milky.common.concurrent.CallableWrapper;
import cn.sliew.milky.common.concurrent.RunnableWrapper;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * A listener for action responses or failures.
 */
public interface ActionListener<Response> {
    /**
     * Handle action response. This response may constitute a failure or a
     * success but it is up to the listener to make that decision.
     */
    void onResponse(Response response);

    /**
     * A failure caused by an exception at some phase of the task.
     */
    void onFailure(Throwable throwable);

    default CompletableFuture<Response> toFuture() {
        CompletableFuture<Response> future = new CompletableFuture();
        future.whenComplete((response, throwable) -> {
            if (throwable != null) {
                onFailure(throwable);
            } else {
                onResponse(response);
            }
        });
        return future;
    }

    default BiConsumer<Response, Throwable> toBiConsumer() {
        return new BiConsumer<Response, Throwable>() {
            @Override
            public void accept(Response response, Throwable throwable) {
                if (throwable != null) {
                    onFailure(throwable);
                } else {
                    onResponse(response);
                }
            }
        };
    }

    static Runnable wrap(Runnable runnable, ActionListener listener) {
        return new RunnableWrapper() {
            @Override
            public void doRun() throws Exception {
                runnable.run();
            }

            @Override
            public void onAfter() {
                listener.onResponse(null);
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }
        };
    }

    static <T> Callable<T> wrap(Callable<T> callable, ActionListener<T> listener) {
        return new CallableWrapper<T>() {
            @Override
            public T doCall() throws Exception {
                return callable.call();
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }

            @Override
            public void onAfter(T result) throws Exception {
                listener.onResponse(result);
            }
        };
    }
}