package ir.kia.android.communication.uart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * @author akia
 * @since 2016-10
 */
class TransceiverReader extends TransceiverCommon {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Consumer<Byte> dataConsumer;

    TransceiverReader(Transceiver transceiver, SerialConfig serialConfig, Consumer<Byte> dataConsumer) {
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
                new Thread(() -> parseAndSendResult(finalResult)).start();
            } else {
                // check twice in a pulse duration to be sure not missing start bit
                sleep(pulseDuration / 2);
            }
        }
    }

    private synchronized void parseAndSendResult(int finalResult) {
        if (serialConfig.isInverted()) {
            finalResult = ~finalResult;
        }
        for (int i = 0; i < serialConfig.getStopBits(); i++) {
            if (((1 << i) & finalResult) == 0) {
                logger.warn("wrong data received: " + finalResult);
                return;
            }
        }
        finalResult = finalResult >> serialConfig.getStopBits();
        if (serialConfig.hasParityBit()) {
            int sum = 0;
            for (int i = 0; i <= serialConfig.getBits(); i++) {
                int mask = 1 << i;
                if ((mask & finalResult) > 0) {
                    sum++;
                }
            }
            if ((sum & 1) > 1) {
                logger.warn("wrong data received: " + finalResult);
                return;
            }
            finalResult = finalResult >> 1;
        }
        finalResult = finalResult & (~(~0 << serialConfig.getBits()));
        dataConsumer.accept((byte)finalResult);
    }
}
