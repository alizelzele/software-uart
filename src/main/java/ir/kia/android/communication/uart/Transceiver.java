package ir.kia.android.communication.uart;

/**
 * @author akia
 * @since 2016-10
 */
public interface Transceiver {
    /**
     * change transmit line status to high or now
     * @param status 1 for high or 0 for low
     */
    void transmit(int status);

    /**
     * get receive line status
     * @return 1 for high or 0 for low
     */
    int receive();
}
