package cn.sliew.milky.common.chain;

import cn.sliew.milky.common.log.Logger;
import cn.sliew.milky.common.log.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DefaultPipeline<K, V> implements Pipeline<K, V> {

    static final Logger logger = LoggerFactory.getLogger(DefaultPipeline.class);

    private static final String HEAD_NAME = "HeadContext#0";
    private static final String TAIL_NAME = "TailContext#0";

    final AbstractPipelineProcess head;
    final AbstractPipelineProcess tail;

    final Executor executor;

    /**
     * todo final chain target，maybe not exists，need one invoker
     */
    final Object invoker = new Object();

    protected DefaultPipeline() {
        this.executor = Executors.newFixedThreadPool(2);

        tail = new TailContext(this);
        head = new HeadContext(this);
        head.next = tail;
        tail.prev = head;
    }

    // todo executor
    @Override
    public Pipeline addFirst(String name, Command command) {
        final AbstractPipelineProcess newCtx;
        synchronized (this) {
            checkDuplicateName(name);
            newCtx = newContext(executor, name, command);
            addFirst0(newCtx);
        }
        return this;
    }

    private void addFirst0(AbstractPipelineProcess newCtx) {
        AbstractPipelineProcess nextCtx = head.next;
        newCtx.prev = head;
        newCtx.next = nextCtx;
        head.next = newCtx;
        nextCtx.prev = newCtx;
    }

    @Override
    public Pipeline addLast(String name, Command command) {
        final AbstractPipelineProcess newCtx;
        synchronized (this) {
            checkDuplicateName(name);
            newCtx = newContext(executor, name, command);
            addLast0(newCtx);
        }
        return this;
    }

    private void addLast0(AbstractPipelineProcess newCtx) {
        AbstractPipelineProcess prev = tail.prev;
        newCtx.prev = prev;
        newCtx.next = tail;
        prev.next = newCtx;
        tail.prev = newCtx;
    }

    @Override
    public Pipeline addBefore(String baseName, String name, Command command) {
        final AbstractPipelineProcess newCtx;
        final AbstractPipelineProcess ctx;
        synchronized (this) {
            checkDuplicateName(name);
            ctx = getContextOrDie(baseName);
            newCtx = newContext(executor, name, command);
            addBefore0(ctx, newCtx);
        }
        return this;
    }

    private static void addBefore0(AbstractPipelineProcess ctx, AbstractPipelineProcess newCtx) {
        newCtx.prev = ctx.prev;
        newCtx.next = ctx;
        ctx.prev.next = newCtx;
        ctx.prev = newCtx;
    }

    @Override
    public Pipeline addAfter(String baseName, String name, Command command) {
        final AbstractPipelineProcess newCtx;
        final AbstractPipelineProcess ctx;
        synchronized (this) {
            checkDuplicateName(name);
            ctx = getContextOrDie(baseName);
            newCtx = newContext(executor, name, command);
            addAfter0(ctx, newCtx);
        }
        return this;
    }

    private static void addAfter0(AbstractPipelineProcess ctx, AbstractPipelineProcess newCtx) {
        newCtx.prev = ctx;
        newCtx.next = ctx.next;
        ctx.next.prev = newCtx;
        ctx.next = newCtx;
    }

    @Override
    public Pipeline remove(Command command) {
        remove(getContextOrDie(command));
        return this;
    }

    @Override
    public Command remove(String name) {
        return remove(getContextOrDie(name)).command();
    }

    @Override
    public final Command remove(Class commandType) {
        return remove(getContextOrDie(commandType)).command();
    }

    @Override
    public Command removeFirst() {
        if (head.next == tail) {
            throw new NoSuchElementException();
        }
        return remove(head.next).command();
    }

    @Override
    public Command removeLast() {
        if (head.next == tail) {
            throw new NoSuchElementException();
        }
        return remove(tail.prev).command();
    }

    private AbstractPipelineProcess remove(final AbstractPipelineProcess ctx) {
        synchronized (this) {
            atomicRemoveFromHandlerList(ctx);
        }
        return ctx;
    }

    /**
     * Method is synchronized to make the handler removal from the double linked list atomic.
     */
    private synchronized void atomicRemoveFromHandlerList(AbstractPipelineProcess ctx) {
        AbstractPipelineProcess prev = ctx.prev;
        AbstractPipelineProcess next = ctx.next;
        prev.next = next;
        next.prev = prev;
    }


    @Override
    public Command first() {
        PipelineProcess first = firstContext();
        if (first == null) {
            return null;
        }
        return first.command();
    }

    @Override
    public Command last() {
        AbstractPipelineProcess last = tail.prev;
        if (last == head) {
            return null;
        }
        return last.command();
    }

    @Override
    public Command get(String name) {
        PipelineProcess ctx = context(name);
        if (ctx == null) {
            return null;
        } else {
            return ctx.command();
        }
    }

    @Override
    public Command get(Class commandType) {
        PipelineProcess ctx = context(commandType);
        if (ctx == null) {
            return null;
        } else {
            return ctx.command();
        }
    }

    @Override
    public PipelineProcess firstContext() {
        AbstractPipelineProcess first = head.next;
        if (first == tail) {
            return null;
        }
        return head.next;
    }

    @Override
    public PipelineProcess lastContext() {
        AbstractPipelineProcess last = tail.prev;
        if (last == head) {
            return null;
        }
        return last;
    }

    @Override
    public PipelineProcess context(Command command) {
        AbstractPipelineProcess ctx = head.next;
        for (; ; ) {
            if (ctx == null) {
                return null;
            }
            if (ctx.command() == command) {
                return ctx;
            }
            ctx = ctx.next;
        }
    }

    @Override
    public PipelineProcess context(String name) {
        return context0(name);
    }

    @Override
    public PipelineProcess context(Class commandType) {
        AbstractPipelineProcess ctx = head.next;
        for (; ; ) {
            if (ctx == null) {
                return null;
            }
            if (commandType.isAssignableFrom(ctx.command().getClass())) {
                return ctx;
            }
            ctx = ctx.next;
        }
    }

    @Override
    public List<String> names() {
        List<String> list = new ArrayList<>();
        AbstractPipelineProcess ctx = head.next;
        for (; ; ) {
            if (ctx == null) {
                return list;
            }
            list.add(ctx.name());
            ctx = ctx.next;
        }
    }

    @Override
    public Map<String, Command<K, V>> toMap() {
        Map<String, Command<K, V>> map = new LinkedHashMap<>();
        AbstractPipelineProcess ctx = head.next;
        for (; ; ) {
            if (ctx == tail) {
                return map;
            }
            map.put(ctx.name(), ctx.command());
            ctx = ctx.next;
        }
    }

    @Override
    public Pipeline fireEvent(Context<K, V> context, CompletableFuture<?> future) {
        AbstractPipelineProcess.invokeEvent(head, context, future);
        return this;
    }

    @Override
    public Pipeline fireExceptionCaught(Context<K, V> context, Throwable cause, CompletableFuture<?> future) {
        AbstractPipelineProcess.invokeExceptionCaught(head, context, future, cause);
        return this;
    }

    private AbstractPipelineProcess newContext(Executor executor, String name, Command command) {
        return new DefaultPipelineProcess(this, executor, name, command);
    }

    private AbstractPipelineProcess context0(String name) {
        AbstractPipelineProcess context = head.next;
        while (context != tail) {
            if (context.name().equals(name)) {
                return context;
            }
            context = context.next;
        }
        return null;
    }

    private AbstractPipelineProcess getContextOrDie(String name) {
        AbstractPipelineProcess ctx = (AbstractPipelineProcess) context(name);
        if (ctx == null) {
            throw new NoSuchElementException(name);
        } else {
            return ctx;
        }
    }

    private AbstractPipelineProcess getContextOrDie(Command command) {
        AbstractPipelineProcess ctx = (AbstractPipelineProcess) context(command);
        if (ctx == null) {
            throw new NoSuchElementException(command.getClass().getName());
        } else {
            return ctx;
        }
    }

    private AbstractPipelineProcess getContextOrDie(Class<? extends Command> commandType) {
        AbstractPipelineProcess ctx = (AbstractPipelineProcess) context(commandType);
        if (ctx == null) {
            throw new NoSuchElementException(commandType.getName());
        } else {
            return ctx;
        }
    }

    private void checkDuplicateName(String name) {
        if (context0(name) != null) {
            throw new IllegalArgumentException("Duplicate command name: " + name);
        }
    }

    /**
     * Returns the {@link String} representation of this pipeline.
     */
    @Override
    public final String toString() {
        StringBuilder buf = new StringBuilder()
                .append(this.getClass().getSimpleName())
                .append('{');
        AbstractPipelineProcess ctx = head.next;
        for (; ; ) {
            if (ctx == tail) {
                break;
            }

            buf.append('(')
                    .append(ctx.name())
                    .append(" = ")
                    .append(ctx.command().getClass().getName())
                    .append(')');

            ctx = ctx.next;
            if (ctx == tail) {
                break;
            }

            buf.append(", ");
        }
        buf.append('}');
        return buf.toString();
    }


    final class TailContext extends AbstractPipelineProcess implements Command {

        TailContext(DefaultPipeline pipeline) {
            super(pipeline, null, TAIL_NAME);
        }

        @Override
        public Command command() {
            return this;
        }

        @Override
        public void onEvent(AbstractPipelineProcess process, Context context, CompletableFuture future) {
            if (future.isDone() || future.isCancelled()) {
                logger.warn("finished or cancelled event! process: {}, context: {}", process, context);
            } else {
                logger.warn("unhandled event triggered! process: {}, context: {}", process, context);
                future.complete(null);
            }
        }

        @Override
        public void exceptionCaught(AbstractPipelineProcess process, Context context, CompletableFuture future, Throwable cause) throws PipelineException {
            logger.warn("An exceptionCaught() event was fired, and it reached at the tail of the pipeline. " +
                    "It usually means the last handler in the pipeline did not handle the exception. context: {}", context, cause);
            future.completeExceptionally(cause);
        }

        @Override
        public String toString() {
            return String.format("%s(%s, %s)", PipelineProcess.class.getSimpleName(), name(), TailContext.class.getCanonicalName());
        }
    }

    final class HeadContext extends AbstractPipelineProcess implements Command {

        HeadContext(DefaultPipeline pipeline) {
            super(pipeline, null, HEAD_NAME);
        }

        @Override
        public Command command() {
            return this;
        }

        @Override
        public void onEvent(AbstractPipelineProcess process, Context context, CompletableFuture future) {
            process.fireEvent(context, future);
        }

        @Override
        public void exceptionCaught(AbstractPipelineProcess process, Context context, CompletableFuture future, Throwable cause) throws PipelineException {
            process.fireExceptionCaught(context, future, cause);
        }

        @Override
        public String toString() {
            return String.format("%s(%s, %s)", PipelineProcess.class.getSimpleName(), name(), HeadContext.class.getCanonicalName());
        }
    }
}
