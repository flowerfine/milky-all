package cn.sliew.milky.log;

public class StdOutLogger extends AbstractLogger {

    StdOutLogger(String name) {
        super(name);
        // Do Nothing
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void trace(String msg) {
        System.out.println(msg);
    }

    @Override
    public void trace(String format, Object arg) {
        FormattingTuple message = MessageFormatter.format(format, arg);
        System.out.println(message.getMessage());
    }

    @Override
    public void trace(String format, Object argA, Object argB) {
        FormattingTuple message = MessageFormatter.format(format, argA, argB);
        System.out.println(message.getMessage());
    }

    @Override
    public void trace(String format, Object... arguments) {
        FormattingTuple message = MessageFormatter.arrayFormat(format, arguments);
        System.out.println(message.getMessage());
    }

    @Override
    public void trace(String msg, Throwable t) {
        System.err.println(msg);
        t.printStackTrace(System.err);
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(String msg) {
        System.out.println(msg);
    }

    @Override
    public void debug(String format, Object arg) {
        FormattingTuple message = MessageFormatter.format(format, arg);
        System.out.println(message.getMessage());
    }

    @Override
    public void debug(String format, Object argA, Object argB) {
        FormattingTuple message = MessageFormatter.format(format, argA, argB);
        System.out.println(message.getMessage());
    }

    @Override
    public void debug(String format, Object... arguments) {
        FormattingTuple message = MessageFormatter.arrayFormat(format, arguments);
        System.out.println(message.getMessage());
    }

    @Override
    public void debug(String msg, Throwable t) {
        System.err.println(msg);
        t.printStackTrace(System.err);
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(String msg) {
        System.out.println(msg);
    }

    @Override
    public void info(String format, Object arg) {
        FormattingTuple message = MessageFormatter.format(format, arg);
        System.out.println(message.getMessage());
    }

    @Override
    public void info(String format, Object argA, Object argB) {
        FormattingTuple message = MessageFormatter.format(format, argA, argB);
        System.out.println(message.getMessage());
    }

    @Override
    public void info(String format, Object... arguments) {
        FormattingTuple message = MessageFormatter.arrayFormat(format, arguments);
        System.out.println(message.getMessage());
    }

    @Override
    public void info(String msg, Throwable t) {
        System.err.println(msg);
        t.printStackTrace(System.err);
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(String msg) {
        System.out.println(msg);
    }

    @Override
    public void warn(String format, Object arg) {
        FormattingTuple message = MessageFormatter.format(format, arg);
        System.out.println(message.getMessage());
    }

    @Override
    public void warn(String format, Object... arguments) {
        FormattingTuple message = MessageFormatter.arrayFormat(format, arguments);
        System.out.println(message.getMessage());
    }

    @Override
    public void warn(String format, Object argA, Object argB) {
        FormattingTuple message = MessageFormatter.format(format, argA, argB);
        System.out.println(message.getMessage());
    }

    @Override
    public void warn(String msg, Throwable t) {
        System.err.println(msg);
        t.printStackTrace(System.err);
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(String msg) {
        System.err.println(msg);
    }

    @Override
    public void error(String format, Object arg) {
        FormattingTuple message = MessageFormatter.format(format, arg);
        System.err.println(message.getMessage());
    }

    @Override
    public void error(String format, Object argA, Object argB) {
        FormattingTuple message = MessageFormatter.format(format, argA, argB);
        System.err.println(message.getMessage());
    }

    @Override
    public void error(String format, Object... arguments) {
        FormattingTuple message = MessageFormatter.arrayFormat(format, arguments);
        System.err.println(message.getMessage());
    }

    @Override
    public void error(String msg, Throwable t) {
        System.err.println(msg);
        t.printStackTrace(System.err);
    }
}