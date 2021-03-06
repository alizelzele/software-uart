package ir.kia.communication.uart;

/**
 * @author akia
 * @since 2016-10
 */
public class SerialConfig {
    private final int baudRate;
    private final int bits;
    private final boolean parityBit;
    private final int stopBits;
    private final boolean inverted;

    public static final SerialConfig DEFAULT = new SerialConfig(9600, 8, false, 1, false);

    public SerialConfig(int baudRate, String config, boolean inverted) {
        this.baudRate = baudRate;
        this.bits = Character.getNumericValue(config.charAt(0));
        this.parityBit = Character.toLowerCase(config.charAt(1)) != 'n';
        this.stopBits = Character.getNumericValue(config.charAt(2));
        this.inverted = inverted;
        checkValues();
    }

    public SerialConfig(int baudRate, int bits, boolean parityBit, int stopBits, boolean inverted) {
        this.baudRate = baudRate;
        this.bits = bits;
        this.parityBit = parityBit;
        this.stopBits = stopBits;
        this.inverted = inverted;
        checkValues();
    }

    private void checkValues() {
        if (bits > 8 || bits < 5) {
            throw new SerialConfigException("bits", bits, "5-8");
        }
        if (stopBits > 2 || stopBits < 1) {
            throw new SerialConfigException("stopBits", stopBits, "1,2");
        }
    }

    public int getBaudRate() {
        return baudRate;
    }

    public int getBits() {
        return bits;
    }

    public boolean hasParityBit() {
        return parityBit;
    }

    public int getStopBits() {
        return stopBits;
    }

    public boolean isInverted() {
        return inverted;
    }

    /**
     * calculate count of bits required to complete each request
     * @return count of bits
     */
    public int getDataCount() {
        return 1 //start bit
                + bits
                + (parityBit ? 1 : 0)
                + stopBits;
    }
}
