package cn.sliew.milky.common.parse.generic;

import cn.sliew.milky.common.primitives.Booleans;

import java.util.Properties;

public class PropertyParser {

    private static final String OPEN_TOKEN = "${";
    private static final String CLOSE_TOKEN = "}";

    private static final String ENABLE_DEFAULT_VALUE = "false";
    private static final String DEFAULT_VALUE_SEPARATOR = ":";

    private PropertyParser() {
        throw new IllegalStateException("no instance");
    }

    public static String parse(String string, Properties variables) {
        VariableTokenHandler handler = new VariableTokenHandler(variables);
        GenericTokenParser parser = new GenericTokenParser(OPEN_TOKEN, CLOSE_TOKEN, handler);
        return parser.parse(string);
    }

    private static class VariableTokenHandler implements TokenHandler {
        private final Properties variables;
        private final boolean enableDefaultValue;
        private final String defaultValueSeparator;

        private VariableTokenHandler(Properties variables) {
            this.variables = variables;
            this.enableDefaultValue = Booleans.parseBoolean(ENABLE_DEFAULT_VALUE);
            this.defaultValueSeparator = DEFAULT_VALUE_SEPARATOR;
        }

        private String getPropertyValue(String key, String defaultValue) {
            return (variables == null) ? defaultValue : variables.getProperty(key, defaultValue);
        }

        @Override
        public String handleToken(String content) {
            if (variables != null) {
                String key = content;
                if (enableDefaultValue) {
                    final int separatorIndex = content.indexOf(defaultValueSeparator);
                    String defaultValue = null;
                    if (separatorIndex >= 0) {
                        key = content.substring(0, separatorIndex);
                        defaultValue = content.substring(separatorIndex + defaultValueSeparator.length());
                    }
                    if (defaultValue != null) {
                        return variables.getProperty(key, defaultValue);
                    }
                }
                if (variables.containsKey(key)) {
                    return variables.getProperty(key);
                }
            }
            return OPEN_TOKEN + content + CLOSE_TOKEN;
        }
    }

}
