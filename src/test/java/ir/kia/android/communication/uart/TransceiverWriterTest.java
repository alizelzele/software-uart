package ir.kia.android.communication.uart;

import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

/**
 * @author akia
 * @since 2016-10
 */
public class TransceiverWriterTest {
    int baudRate = 64;
    SerialConfig config = new SerialConfig(baudRate, "8N1", false);


    @Test
    public void testRead() throws InterruptedException {

        TransceiverWriter writer = new TransceiverWriter(new DummyTransReceiver(baudRate, new Consumer<Integer>() {
            private String result = "100000000110000000101000000100100000011110000010001";
            private int index = 0;

            @Override
            public void accept(Integer integer) {
                assertEquals("wrong node at index " + index, Integer.valueOf(Character.getNumericValue(result.charAt(index))), integer);
                index++;
            }
        }), config);
        new Thread(writer).start();
        Thread.sleep(500);
        System.out.println("writing 1");
        writer.write((byte) 1);
        Thread.sleep(1500);
        System.out.println("writing 2 , 4");
        writer.write(new byte[]{(byte) 2, (byte) 4});
        Thread.sleep(3000);
        System.out.println("writing 7,8 and cutting in the middle");
        writer.write(new byte[]{(byte) 7, (byte) 8});
        Thread.sleep(500);
        System.out.println("stopping");
        writer.stop();
        Thread.sleep(3000);
    }
}
