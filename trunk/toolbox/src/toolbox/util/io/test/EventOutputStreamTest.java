package toolbox.util.io.test;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Date;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.log4j.Logger;

import toolbox.util.DateTimeUtil;
import toolbox.util.RandomUtil;
import toolbox.util.concurrent.BlockingQueue;
import toolbox.util.io.EventOutputStream;
import toolbox.util.io.StringOutputStream;

/**
 * Unit test for EventOutputStream
 */
public class EventOutputStreamTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(EventOutputStreamTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Entrypoint   
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(EventOutputStreamTest.class);
    }

    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
       
    /**
     * Tests the constructors
     */
    public void testConstructors()
    {
        logger_.info("Running testConstructors...");
        
        EventOutputStream cos = 
            new EventOutputStream(new NullOutputStream());
        
        assertNotNull(cos);
        assertEquals(0, cos.getCount());
    }
        
    /**
     * Tests the write(byte) method
     * 
     * @throws Exception on error
     */
    public void testWriteByte() throws Exception
    {
        logger_.info("Running testWriteByte...");
        
        StringOutputStream sos = new StringOutputStream();
        EventOutputStream cos = new EventOutputStream(sos);

        assertEquals(0, cos.getCount());        
        cos.write("x".getBytes()[0]);
        cos.flush();
        assertEquals("strings don't match", "x", sos.toString());
        assertEquals(1, cos.getCount());
    }
    
    /**
     * Tests the write(byte[]) method
     * 
     * @throws Exception on error
     */
    public void testWriteByteArray() throws Exception
    {
        logger_.info("Running testWriteByteArray...");

        StringOutputStream sos = new StringOutputStream();       
        EventOutputStream cos = new EventOutputStream(sos);

        assertEquals(0, cos.getCount());
        cos.write("hello".getBytes());
        cos.flush();
        assertEquals("strings don't match", "hello", sos.toString());
        assertEquals("hello".length(), cos.getCount());
    }
    
    /**
     * Tests the write(byte[], begin, len) method
     * 
     * @throws Exception on error
     */
    public void testWriteByteArraySubset() throws Exception
    {
        logger_.info("Running testWriteByteArraySubset...");

        StringOutputStream sos = new StringOutputStream();       
        EventOutputStream cos = new EventOutputStream(sos);

        assertEquals(0, cos.getCount());
        cos.write("hello".getBytes(), 2, 2);
        cos.flush();
        assertEquals("strings don't match", "ll", sos.toString());
        assertEquals(2, cos.getCount());
    }
    
    /**
     * Tests the event generation and listener notification
     *
     * @throws Exception on error
     */
    public void testListener() throws Exception
    {
        logger_.info("Running testListener...");
        
        EventOutputStream eos = new EventOutputStream(new NullOutputStream());
        OutputStreamListener listener = new OutputStreamListener();
        eos.addListener(listener);
        
        eos.write(1);
        assertEquals(1, listener.waitForWrite());
        assertEquals(1, eos.getCount());
        
        eos.flush();
        assertNotNull(listener.waitForFlush());
        
        eos.close();
        assertNotNull(listener.waitForClose());
    }

    /**
     * Tests the streamThroughput() method on the EventOutputStream.Listener
     * 
     * @throws Exception on error
     */
    public void testStreamThroughput() throws Exception
    {
        logger_.info("Running testStreamThroughput...");
        
        EventOutputStream eos = new EventOutputStream(new NullOutputStream());
        eos.addListener(new ThroughputListener());
        eos.startThroughputMonitor();
        
        logger_.info("");
        logger_.info("[1 byte packet]");
        stuffStream(eos, 1, 2);
        
        logger_.info("");
        logger_.info("[10 byte packet]");
        stuffStream(eos, 10, 2);
        
        logger_.info("");
        logger_.info("[100 byte packet]");
        stuffStream(eos, 100, 2);
        
        logger_.info("");
        logger_.info("[1000 byte packet]");
        stuffStream(eos, 1000, 2);
        
        logger_.info("");
        logger_.info("[10000 byte packet]");
        stuffStream(eos, 10000, 2);
        
        logger_.info("");
        logger_.info("[100000 byte packet]");
        stuffStream(eos, 100000, 2); 
               
        eos.stopThroughputMonitor();
    }

    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------

    /**
     * Stuffs a stream with a packet of random data as fast as possible for a 
     * given duration of time.
     * 
     * @param   os          OutputStream to stuff
     * @param   packetSize  Number of bytes to stuff per write
     * @param   duration    Number of seconds to stuff the stream
     * @throws  IOException on I/O error
     */
    protected void stuffStream(OutputStream os, int packetSize, int duration)
        throws IOException
    {
        byte[] packet = new byte[packetSize];
        for (int i=0; i<packet.length; i++)
            packet[i] = (byte) RandomUtil.nextInt(1,50);
        
        Date now = new Date();
        Date future = new Date(now.getTime());
        DateTimeUtil.add(future, 0, 0, 0, 0, 0, duration);
            
        while (now.before(future))
        {
            os.write(packet);
            os.flush();
            now = new Date();
        }
    }

    //--------------------------------------------------------------------------
    // Inner classes 
    //--------------------------------------------------------------------------
   
    /** 
     * Listener used to make sure event notification is working correctly
     */    
    class OutputStreamListener implements EventOutputStream.Listener
    {
        private BlockingQueue writeQueue_ = new BlockingQueue();
        private BlockingQueue flushQueue_ = new BlockingQueue();
        private BlockingQueue closeQueue_ = new BlockingQueue();
        
        //----------------------------------------------------------------------
        // Public
        //----------------------------------------------------------------------
        
        public int waitForWrite() throws InterruptedException
        {
            return ((Integer) writeQueue_.pull()).intValue();
        }

        public EventOutputStream waitForFlush() throws InterruptedException
        {
            return (EventOutputStream) flushQueue_.pull();
        }

        public EventOutputStream waitForClose() throws InterruptedException
        {
            return (EventOutputStream) closeQueue_.pull();
        }
        
        //----------------------------------------------------------------------
        // EvenoutOutputStream.Listener Interface
        //----------------------------------------------------------------------
        
        public void byteWritten(EventOutputStream stream, int b)
        {
            writeQueue_.push(new Integer(b));
        }
        
        public void streamClosed(EventOutputStream stream)
        {
            closeQueue_.push(stream);
        }

        public void streamFlushed(EventOutputStream stream)
        {
            flushQueue_.push(stream);
        }
        
        public void streamThroughput(EventOutputStream stream, 
            float bytesPerPeriod)
        {
        }
    }
    
    /** 
     * Listener used to make sure event notification is working correctly
     */    
    class ThroughputListener implements EventOutputStream.Listener
    {
        //----------------------------------------------------------------------
        // EvenoutOutputStream.Listener Interface
        //----------------------------------------------------------------------
        
        public void byteWritten(EventOutputStream stream, int b)
        {
        }
        
        public void streamClosed(EventOutputStream stream)
        {
        }

        public void streamFlushed(EventOutputStream stream)
        {
        }
        
        public void streamThroughput(EventOutputStream stream, 
            float bytesPerPeriod)
        {
            String thruput = DecimalFormat.getInstance().format(bytesPerPeriod);
            
            logger_.info("Transferred " + thruput + " bytes/second");    
        }
    }
}