package ir.kia.android.communication.uart;

import java.util.function.Consumer;

/**
 * @author akia
 * @since 2016-10
 */
public class UART {
    private final Transceiver transceiver;
    private final TransceiverReader transceiverReader;
    private SerialConfig serialConfig = SerialConfig.DEFAULT;


    public UART(Transceiver transceiver, Consumer<Byte> consumer) {
        this.transceiver = transceiver;
        transceiverReader = new TransceiverReader(transceiver, serialConfig, this::parseReceived);
        new Thread(transceiverReader).start();
    }

    private synchronized void parseReceived(Integer received) {

        if (serialConfig.isInverted()) {
            received = ~received;
        }
        //TODO validate
        System.out.println(received);
    }
}
