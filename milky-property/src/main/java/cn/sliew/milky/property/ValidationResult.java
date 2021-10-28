package cn.sliew.milky.property;

/**
 * {@link Setting} validate result with explanation.
 */
public class ValidationResult {

    private final Setting setting;
    private final String value;
    private final String explanation;
    private final boolean valid;

    public ValidationResult(Setting setting, String value, String explanation, boolean valid) {
        this.setting = setting;
        this.value = value;
        this.explanation = explanation;
        this.valid = valid;
    }

    /**
     * @return the setting being validated
     */
    public Setting getSetting() {
        return setting;
    }

    /**
     * @return the value that was tested for validity
     */
    public String getValue() {
        return value;
    }

    /**
     * @return an explanation of the validation result
     */
    public String getExplanation() {
        return this.explanation;
    }

    /**
     * @return true if current result is valid; false otherwise
     */
    public boolean isValid() {
        return this.valid;
    }
}
