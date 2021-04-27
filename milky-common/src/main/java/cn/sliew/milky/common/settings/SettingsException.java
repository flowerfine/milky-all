package cn.sliew.milky.common.settings;

/**
 * A generic failure to handle settings.
 */
public class SettingsException extends RuntimeException {

    public SettingsException() {
        super();
    }

    public SettingsException(String message) {
        super(message);
    }

    public SettingsException(Throwable cause) {
        super(cause);
    }

    public SettingsException(String message, Throwable cause) {
        super(message, cause);
    }

    protected SettingsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}