package ir.kia.communication.uart;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author akia
 * @since 2016-10
 */
public class TransceiverCommonTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void sleep() throws Exception {
        Random random = new Random();
        TransceiverCommon common = new TransceiverCommon(new DummyTransReceiver(), SerialConfig.DEFAULT) {
            @Override
            public void run() {
            }
        };
        common.sleep(100000000);
        long sum = 0;
        for (int i = 0; i < 10; i++) {
            final int delay = random.nextInt(50);
            long start = System.nanoTime();
            Thread.sleep(delay);
            common.sleep(100000000);
            long end = System.nanoTime();
            sum += end - start;

        }
        long average = sum / 10;
        logger.info("" + average);
        assertTrue("sleeped less", average > 99800000);
        assertTrue("sleeped more", average < 100200000);
    }

}
