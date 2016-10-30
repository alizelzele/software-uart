package ir.kia.android.communication.uart;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author akia
 * @since 2016-10
 */
public class TransceiverReader implements Runnable {
    private long startTime;
    private final Transceiver transceiver;
    private final SerialConfig serialConfig;
    private final Consumer<Integer> dataConsumer;
    private boolean alive;

    public TransceiverReader(Transceiver transceiver, SerialConfig serialConfig, Consumer<Integer> dataConsumer) {
        this.transceiver = transceiver;
        this.serialConfig = serialConfig;
        this.dataConsumer = dataConsumer;
        startTime = System.nanoTime();
    }

    @Override
    public void run() {
        alive = true;
        int dataCount = serialConfig.getDataCount();
        // is start bit a high value or a low one
        int startBit = serialConfig.isInverted() ? 1 : 0;
        long pulseDuration = TimeUnit.SECONDS.toNanos(1) / serialConfig.getBaudRate();
        while (alive) {
            if (transceiver.receive() == startBit) {
                // best precision to middle of pulse
                startTime = System.nanoTime() + (pulseDuration / 4);
                int result = startBit;
                for (int i = 1; i < dataCount; i++) {
                    sleep(pulseDuration);
                    int read = transceiver.receive();
                    result = (result << 1) | read;
                }
                int finalResult = result;
                // calling data consumer on another thread to free Reader
                new Thread(() -> dataConsumer.accept(finalResult));
            } else {
                // check twice in a pulse duration to be sure not missing start bit
                sleep(pulseDuration / 2);
            }
        }
    }

    /**
     * sleep based on start time to ignore calculation time
     *
     * @param duration ideal sleep duration
     */
    private void sleep(long duration) {
        final long timePassedFromDuration = (System.nanoTime() - startTime) % duration;
        try {
            TimeUnit.NANOSECONDS.sleep(duration - timePassedFromDuration);
        } catch (InterruptedException e) {
            alive = false;
        }
    }

    public void stop() {
        alive = false;
    }
}
