package ir.kia.android.communication.uart;

import java.util.concurrent.TimeUnit;

/**
 * common process for both reader and writer
 *
 * @author akia
 * @since 2016-10
 */
abstract class TransceiverCommon implements Runnable {
    final Transceiver transceiver;
    final SerialConfig serialConfig;
    final long pulseDuration;
    final int dataCount;

    long startTime;
    boolean alive;

    TransceiverCommon(Transceiver transceiver, SerialConfig serialConfig) {
        this.transceiver = transceiver;
        this.serialConfig = serialConfig;
        pulseDuration = TimeUnit.SECONDS.toNanos(1) / serialConfig.getBaudRate();
        dataCount = serialConfig.getDataCount();
        startTime = System.nanoTime();
        alive = true;
    }

    /**
     * sleep based on start time to ignore calculation time
     *
     * @see #sleep(long)
     */
    void waitOnePulse() {
        sleep(pulseDuration);
    }

    /**
     * sleep for defined duration based on start time to ignore calculation time
     *
     * @param duration ideal sleep duration
     */
    void sleep(long duration) {
        final long enterTime = System.nanoTime();
        final long timePassedFromDuration = (enterTime - startTime) % duration;
        final long timeout = duration - timePassedFromDuration;
        while (true) {
            if (System.nanoTime() - enterTime > timeout) {
                break;
            }
        }
    }

    /**
     * stop execution loop
     */
    public void stop() {
        alive = false;
    }
}
