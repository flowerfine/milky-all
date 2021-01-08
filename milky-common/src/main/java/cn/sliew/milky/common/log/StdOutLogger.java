/**
 * Copyright 2009-2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.sliew.milky.common.log;

/**
 * @author Clinton Begin
 */
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
        System.out.println(msg);
    }

    @Override
    public void error(String format, Object arg) {
        FormattingTuple message = MessageFormatter.format(format, arg);
        System.out.println(message.getMessage());
    }

    @Override
    public void error(String format, Object argA, Object argB) {
        FormattingTuple message = MessageFormatter.format(format, argA, argB);
        System.out.println(message.getMessage());
    }

    @Override
    public void error(String format, Object... arguments) {
        FormattingTuple message = MessageFormatter.arrayFormat(format, arguments);
        System.out.println(message.getMessage());
    }

    @Override
    public void error(String msg, Throwable t) {
        System.err.println(msg);
        t.printStackTrace(System.err);
    }
}