package ir.kia.communication.uart;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Consumer;

import static org.junit.Assert.fail;

/**
 * @author akia
 * @since 2016-10
 */
public class TransceiverReaderTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private int baudRate = 512;

    @Test
    public void testRead() throws InterruptedException {
        SerialConfig config = new SerialConfig(baudRate, "8N1", false);
        TransceiverReader reader = new TransceiverReader(new DummyTransReceiver(baudRate), config, new Consumer<Byte>() {
            private boolean firstResult = true;
            private int[] results = new int[]{-1, 1, 15, 47, 51};

            @Override
            public void accept(Byte data) {
                //show data for debugging
                if (baudRate < 256) {
                    logger.info("" + data);
                }
                if (firstResult) {
                    //ignore first result since it might not be readed fully.
                    firstResult = false;
                    return;
                }
                if (Arrays.binarySearch(results, data) < 0) {
                    fail("data not acceptable: " + data);
                }
            }
        });
        new Thread(reader).start();

        Thread.sleep(10000);
        logger.info("stop readeing");
        reader.stop();
        Thread.sleep(1000);
    }

}
