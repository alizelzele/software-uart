package ir.kia.communication.uart;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * @author akia
 * @since 2016-10
 */
public class TransceiverWriter extends TransceiverCommon {
    private final PipedInputStream inputStream;
    private final PipedOutputStream outputStream;

    TransceiverWriter(Transceiver transceiver, SerialConfig serialConfig) {
        super(transceiver, serialConfig);
        inputStream = new PipedInputStream(1024);
        try {
            outputStream = new PipedOutputStream(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("this will not happen", e);
        }
    }

    public void write(byte b) {
        try {
            outputStream.write(b);
        } catch (IOException ignored) {
        }
    }

    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
        } catch (IOException ignored) {
        }
    }

    @Override
    public void run() {
        // putting line to initial state
        transceiver.transmit(serialConfig.isInverted() ? 0 : 1);
        int read;
        while (alive) {
            try {
                read = inputStream.read();
            } catch (IOException e) {
                throw new RuntimeException("this will not happen");
            }
            if (read != -1) {
                int toSend = generateValueToSend(read);
                startTime = System.nanoTime();
                for (int currentBit = dataCount - 1; currentBit >= 0; currentBit--) {
                    int state = ((toSend & (1 << currentBit)) > 0) ? 1 : 0;
                    transceiver.transmit(state);
                    waitOnePulse();
                }
            } else {
                waitOnePulse();
            }
        }
    }

    private int generateValueToSend(int read) {
        int toSend = ~(1 << serialConfig.getBits()) & read;
        if (serialConfig.hasParityBit()) {
            int sum = 0;
            for (int i = 0; i < serialConfig.getBits(); i++) {
                int mask = 1 << i;
                if ((mask & read) > 0) {
                    sum++;
                }
            }
            toSend = (toSend << 1) | (sum & 1);
        }
        for (int i = 0; i < serialConfig.getStopBits(); i++) {
            toSend = (toSend << 1) | 1;
        }
        if (serialConfig.isInverted()) {
            toSend = ~toSend;
        }
        return toSend;
    }

    @Override
    public void stop() {
        super.stop();
        try {
            outputStream.close();
            while (inputStream.available() > 1) {
                waitOnePulse();
            }
            inputStream.close();
        } catch (IOException ignored) {
        }
    }
}
