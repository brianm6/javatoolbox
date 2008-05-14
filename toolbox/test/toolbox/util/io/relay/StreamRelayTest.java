package toolbox.util.io.relay;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

public class StreamRelayTest extends TestCase {

    public void testRelaySingleByte() throws Exception {
        byte[] buf = new byte[1];
        buf[0] = 0;
        ByteArrayInputStream is = new ByteArrayInputStream(buf);
        ByteArrayOutputStream os = new ByteArrayOutputStream(1);
        StreamRelay relay = new StreamRelay(is, os, new AlwaysFlushPolicy());
        relay.run();
        assertEquals(0, os.toByteArray()[0]);
    }
    
    public void testRelayManyBytes() throws Exception {
        byte[] buf = new byte[999999];
        for (int i = 0; i < buf.length; buf[i++] = (byte) (buf.length % 32));
        
        ByteArrayInputStream is = new ByteArrayInputStream(buf);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        StreamRelay relay = new StreamRelay(is, os, new AlwaysFlushPolicy());
        relay.run();
        byte[] actual = os.toByteArray();

        assertEquals(buf.length, actual.length);
        for (int i = 0; i < buf.length; i++) {
            assertEquals(buf[i], actual[i] );
        }
    }
    
}
