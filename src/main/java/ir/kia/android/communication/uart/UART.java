package ir.kia.android.communication.uart;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author akia
 * @since 2016-10
 */
public class UART {
    private final TransceiverReader reader;
    private final TransceiverWriter writer;
    private final List<Byte> buffer;
    public final InputStream in;

    public UART(final Transceiver transceiver) {
        this(transceiver, () -> {
        });
    }

    public UART(final Transceiver transceiver, final Runnable dataArrivedTrigger) {
        this(transceiver, dataArrivedTrigger, SerialConfig.DEFAULT);
    }

    public UART(final Transceiver transceiver, final Runnable dataArrivedTrigger, SerialConfig serialConfig) {
        buffer = new ArrayList<>();
        in = initializeInputStream();
        reader = new TransceiverReader(transceiver, serialConfig, aByte -> {
            buffer.add(aByte);
            dataArrivedTrigger.run();
        });
        new Thread(reader).start();
        writer = new TransceiverWriter(transceiver, serialConfig);
        new Thread(writer).start();
    }

    private InputStream initializeInputStream() {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                if (buffer.isEmpty()) {
                    return -1;
                }
                final Byte aByte = buffer.get(0);
                buffer.remove(0);
                return aByte;
            }

            @Override
            public int available() throws IOException {
                return buffer.size();
            }
        };
    }

    public void write(byte b) {
        writer.write(b);
    }
}
