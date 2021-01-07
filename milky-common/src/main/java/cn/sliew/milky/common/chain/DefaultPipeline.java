package cn.sliew.milky.common.chain;

import cn.sliew.milky.common.log.Logger;
import cn.sliew.milky.common.log.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

public class DefaultPipeline implements Pipeline {

    static final Logger logger = LoggerFactory.getLogger(DefaultPipeline.class);

    private static final String HEAD_NAME = "HeadContext#0";
    private static final String TAIL_NAME = "TailContext#0";

    private static final ThreadLocal<Map<Class<?>, String>> nameCaches = ThreadLocal.withInitial(() -> new WeakHashMap<>());

    final AbstractPipelineProcess head;
    final AbstractPipelineProcess tail;

    protected DefaultPipeline() {
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
            newCtx = newContext(null, name, command);
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
            newCtx = newContext(null, name, command);
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
            ctx = getContextOrDie(baseName);
            newCtx = newContext(null, name, command);
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
            ctx = getContextOrDie(baseName);
            newCtx = newContext(null, name, command);
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
        return null;
    }

    @Override
    public Command removeLast() {
        return null;
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
        List<String> list = new ArrayList<String>();
        AbstractPipelineProcess ctx = head.next;
        for (;;) {
            if (ctx == null) {
                return list;
            }
            list.add(ctx.name());
            ctx = ctx.next;
        }
    }

    @Override
    public Map<String, Command> toMap() {
        return null;
    }

    @Override
    public Pipeline fireEvent() {
        return null;
    }

    @Override
    public Pipeline fireExceptionCaught(Throwable cause) {
        return null;
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

    final class TailContext extends AbstractPipelineProcess implements Command {

        TailContext(DefaultPipeline pipeline) {
            super(pipeline, null, TAIL_NAME);
        }

        @Override
        public Command command() {
            return this;
        }

        @Override
        public Processing execute(PipelineProcess process, Map context, Future future) {
            return null;
        }

        @Override
        public void exceptionCaught(Command command, Throwable cause) throws PipelineException {

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
        public Processing execute(PipelineProcess process, Map context, Future future) {
            return null;
        }

        @Override
        public void exceptionCaught(Command command, Throwable cause) throws PipelineException {

        }
    }
}
