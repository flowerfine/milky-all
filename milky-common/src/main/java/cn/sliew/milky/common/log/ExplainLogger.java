package cn.sliew.milky.common.log;

import cn.sliew.milky.common.context.ExecuteContext;
import cn.sliew.milky.common.explain.Explanation;

/**
 * 日志装饰器，将日志信息添加到{@link Explanation}，暴露系统内部运行信息。
 */
public class ExplainLogger implements Logger {

    public final Logger logger;

    public ExplainLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public String name() {
        return this.logger.name();
    }

    @Override
    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        ExecuteContext context = ExecuteContext.getContext();
        this.logger.trace(msg);
    }

    @Override
    public void trace(String format, Object arg) {

    }

    @Override
    public void trace(String format, Object argA, Object argB) {

    }

    @Override
    public void trace(String format, Object... arguments) {

    }

    @Override
    public void trace(String msg, Throwable t) {

    }

    @Override
    public void trace(Throwable t) {

    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(String msg) {

    }

    @Override
    public void debug(String format, Object arg) {

    }

    @Override
    public void debug(String format, Object argA, Object argB) {

    }

    @Override
    public void debug(String format, Object... arguments) {

    }

    @Override
    public void debug(String msg, Throwable t) {

    }

    @Override
    public void debug(Throwable t) {

    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(String msg) {

    }

    @Override
    public void info(String format, Object arg) {

    }

    @Override
    public void info(String format, Object argA, Object argB) {

    }

    @Override
    public void info(String format, Object... arguments) {

    }

    @Override
    public void info(String msg, Throwable t) {

    }

    @Override
    public void info(Throwable t) {

    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(String msg) {

    }

    @Override
    public void warn(String format, Object arg) {

    }

    @Override
    public void warn(String format, Object... arguments) {

    }

    @Override
    public void warn(String format, Object argA, Object argB) {

    }

    @Override
    public void warn(String msg, Throwable t) {

    }

    @Override
    public void warn(Throwable t) {

    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void error(String msg) {

    }

    @Override
    public void error(String format, Object arg) {

    }

    @Override
    public void error(String format, Object argA, Object argB) {

    }

    @Override
    public void error(String format, Object... arguments) {

    }

    @Override
    public void error(String msg, Throwable t) {

    }

    @Override
    public void error(Throwable t) {

    }

    @Override
    public boolean isEnabled(LogLevel level) {
        return false;
    }

    @Override
    public void log(LogLevel level, String msg) {

    }

    @Override
    public void log(LogLevel level, String format, Object arg) {

    }

    @Override
    public void log(LogLevel level, String format, Object argA, Object argB) {

    }

    @Override
    public void log(LogLevel level, String format, Object... arguments) {

    }

    @Override
    public void log(LogLevel level, String msg, Throwable t) {

    }

    @Override
    public void log(LogLevel level, Throwable t) {

    }
}
