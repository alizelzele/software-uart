package ir.kia.android.communication.uart;

/**
 * @author akia
 * @since 2016-10
 */
class DummyTransReceiver implements Transceiver {
    private long startTransmit = System.nanoTime();
    private Long startReveice;
    private static String TO_SENT = "111100000000111111100000111111111111110111111111011111111100010111110001100111111111111";
    private final int baudRate;
    private final int pulseDuration;

    public DummyTransReceiver() {
        this(10);
    }

    public DummyTransReceiver(int baudRate) {
        this.baudRate = baudRate;
        this.pulseDuration = (int) (1e9d / baudRate);
    }

    @Override
    public void transmit(int status) {
        long delay = System.nanoTime() - startTransmit;
        startTransmit = System.nanoTime();
        System.out.println("sending " + status + " at " + delay);
    }

    @Override
    public int receive() {
        if (startReveice == null) {
            startReveice = System.nanoTime();
        }
        long timePassed = System.nanoTime() - startReveice;
        int index = (int) (timePassed / pulseDuration);
        index = index % TO_SENT.length();
        // show index on slow tests for debugging
        if (baudRate < 64) {
            System.out.println("read index: " + index);
        }
        return Character.getNumericValue(TO_SENT.charAt(index));
    }
}
