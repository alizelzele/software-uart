package ir.kia.android.communication.uart;

import java.util.function.Consumer;

/**
 * @author akia
 * @since 2016-10
 */
class TransceiverReader extends TransceiverCommon {
    private final Consumer<Integer> dataConsumer;

    TransceiverReader(Transceiver transceiver, SerialConfig serialConfig, Consumer<Integer> dataConsumer) {
        super(transceiver, serialConfig);
        this.dataConsumer = dataConsumer;
    }

    @Override
    public void run() {
        // is start bit a high value or a low one
        int startBit = serialConfig.isInverted() ? 1 : 0;
        while (alive) {
            if (transceiver.receive() == startBit) {
                // best precision to middle of pulse
                startTime = System.nanoTime() + (pulseDuration / 4);
                int result = startBit;
                for (int i = 1; i < dataCount; i++) {
                    waitOnePulse();
                    int read = transceiver.receive();
                    result = (result << 1) | read;
                }
                int finalResult = result;
                // calling data consumer on another thread to free Reader
                new Thread(() -> dataConsumer.accept(finalResult)).start();
            } else {
                // check twice in a pulse duration to be sure not missing start bit
                sleep(pulseDuration / 2);
            }
        }
    }
}
