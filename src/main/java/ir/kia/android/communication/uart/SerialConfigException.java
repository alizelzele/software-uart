package ir.kia.android.communication.uart;

/**
 * @author akia
 * @since 2016-10
 */
public class SerialConfigException extends RuntimeException {
    private String field;
    private Object value;
    private String acceptedValues;

    public SerialConfigException(String field, Object value, String acceptedValues) {
        super("wrong field value for field: " + field);
        this.field = field;
        this.value = value;
        this.acceptedValues = acceptedValues;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    public String getAcceptedValues() {
        return acceptedValues;
    }
}
