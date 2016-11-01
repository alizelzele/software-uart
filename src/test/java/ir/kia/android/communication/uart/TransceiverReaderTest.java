package ir.kia.android.communication.uart;

import org.junit.Test;

import java.util.Arrays;
import java.util.function.Consumer;

import static org.junit.Assert.fail;

/**
 * @author akia
 * @since 2016-10
 */
public class TransceiverReaderTest {
    private int baudRate = 512;

    @Test
    public void testRead() throws InterruptedException {
        SerialConfig config = new SerialConfig(baudRate, "8N1", false);
        TransceiverReader reader = new TransceiverReader(new DummyTransReceiver(baudRate), config, new Consumer<Integer>() {
            private boolean firstResult = true;
            private int[] results = new int[]{1, 15, 47, 51, 255};

            @Override
            public void accept(Integer data) {
                //show data for debugging
                if (baudRate < 256) {
                    System.out.println(data);
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
        System.out.println("stop readeing");
        reader.stop();
        Thread.sleep(1000);
    }

}
