package toolbox.util.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import edu.emory.mathcs.util.concurrent.BlockingQueue;
import edu.emory.mathcs.util.concurrent.LinkedBlockingQueue;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.log4j.Logger;

import toolbox.util.DateTimeUtil;
import toolbox.util.RandomUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.io.throughput.MockThroughputListener;
import toolbox.util.io.throughput.ThroughputMonitor;

/**
 * Unit test for {@link toolbox.util.io.MonitoredOutputStream}.
 */
public class MonitoredOutputStreamTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(MonitoredOutputStreamTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Entrypoint.   
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(MonitoredOutputStreamTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
       
    /**
     * Tests the constructors.
     */
    public void testConstructors()
    {
        logger_.info("Running testConstructors...");
        assertNotNull(new MonitoredOutputStream(new NullOutputStream()));
    }
        
    
//    /**
//     * Tests the write(byte) method.
//     * 
//     * @throws Exception on error.
//     */
//    public void testWriteByte() throws Exception
//    {
//        logger_.info("Running testWriteByte...");
//        
//        StringOutputStream sos = new StringOutputStream();
//        MonitoredOutputStream cos = new MonitoredOutputStream(sos);
//
//        assertEquals(0, cos.getCount());        
//        cos.write("x".getBytes()[0]);
//        cos.flush();
//        assertEquals("strings don't match", "x", sos.toString());
//        assertEquals(1, cos.getCount());
//    }
//    
//    
//    /**
//     * Tests the write(byte[]) method.
//     * 
//     * @throws Exception on error.
//     */
//    public void testWriteByteArray() throws Exception
//    {
//        logger_.info("Running testWriteByteArray...");
//
//        StringOutputStream sos = new StringOutputStream();       
//        MonitoredOutputStream cos = new MonitoredOutputStream(sos);
//
//        assertEquals(0, cos.getCount());
//        cos.write("hello".getBytes());
//        cos.flush();
//        assertEquals("strings don't match", "hello", sos.toString());
//        assertEquals("hello".length(), cos.getCount());
//    }
//    
//    
//    /**
//     * Tests the write(byte[], begin, len) method.
//     * 
//     * @throws Exception on error.
//     */
//    public void testWriteByteArraySubset() throws Exception
//    {
//        logger_.info("Running testWriteByteArraySubset...");
//
//        StringOutputStream sos = new StringOutputStream();       
//        MonitoredOutputStream cos = new MonitoredOutputStream(sos);
//
//        assertEquals(0, cos.getCount());
//        cos.write("hello".getBytes(), 2, 2);
//        cos.flush();
//        assertEquals("strings don't match", "ll", sos.toString());
//        assertEquals(2, cos.getCount());
//    }
//    
//    
    /**
     * Tests the event generation and listener notification.
     *
     * @throws Exception on error.
     */
    public void testListener() throws Exception
    {
        logger_.info("Running testListener...");
        
        MonitoredOutputStream mos = 
            new MonitoredOutputStream("MyStream", new NullOutputStream());
        
        assertEquals("MyStream", mos.getName());
        
        OutputStreamListener listener = new OutputStreamListener();
        mos.addOutputStreamListener(listener);
        
        mos.write(1);
        assertEquals(1, mos.getCount());
        
        mos.flush();
        assertNotNull(listener.waitForFlush());
        
        mos.close();
        assertNotNull(listener.waitForClose());
        
        mos.removeOutputStreamListener(listener);
    }

    
    /**
     * Tests the streamThroughput() method on the MonitoredOutputStream.Listener.
     * 
     * @throws Exception on error.
     */
    public void testStreamThroughput() throws Exception
    {
        logger_.info("Running testStreamThroughput...");
        
        MonitoredOutputStream mos = 
            new MonitoredOutputStream(new NullOutputStream());
        
        ThroughputMonitor monitor = mos.getThroughputMonitor();
        
        monitor.addThroughputListener(new MockThroughputListener());
        monitor.setSampleInterval(1000);
        monitor.setMonitoringThroughput(true);
        
        int delay = 0;
        
        logger_.info("");
        logger_.info("[1 byte packet]");
        stuffStream(mos, 1, 2, delay);
        
        logger_.info("");
        logger_.info("[10 byte packet]");
        stuffStream(mos, 10, 2, delay);
        
        logger_.info("");
        logger_.info("[100 byte packet]");
        stuffStream(mos, 100, 2, delay);
        
        logger_.info("");
        logger_.info("[1000 byte packet]");
        stuffStream(mos, 1000, 2, delay);
        
        logger_.info("");
        logger_.info("[10000 byte packet]");
        stuffStream(mos, 10000, 2, delay);
        
        logger_.info("");
        logger_.info("[100000 byte packet]");
        stuffStream(mos, 100000, 2, delay); 
               
        monitor.setMonitoringThroughput(false);
    }

    /**
     * Tests the streamThroughput() method on the MonitoredOutputStream.Listener.
     * 
     * @throws Exception on error.
     */
    public void testCurrentThroughput() throws Exception
    {
        logger_.info("Running testStreamThroughput...");

        MonitoredOutputStream mos = 
            new MonitoredOutputStream(new NullOutputStream());

        BufferedOutputStream bos = new BufferedOutputStream(mos, 1);
       
        ThroughputMonitor monitor = mos.getThroughputMonitor();
        monitor.addThroughputListener(new MockThroughputListener());
        monitor.setSampleInterval(1000);
        monitor.setMonitoringThroughput(true);
        
        int delay = 1;
        
        logger_.info("[100000 byte packet]");
        //for (int i = 0; i< 1000000; i++)
        //    bos.write(i);
        
        //while (true)
        //    bos.write(99);

        //while (true)
        //    mos.write(99);
        
        stuffStream(mos, 1000000, 10, delay); 
               
        monitor.setMonitoringThroughput(false);
    }

    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------

    /**
     * Stuffs a stream with a packet of random data as fast as possible for a 
     * given duration of time.
     * 
     * @param os OutputStream to stuff.
     * @param packetSize Number of bytes to stuff per write.
     * @param duration Number of seconds to stuff the stream.
     * @throws IOException on I/O error.
     */
    protected void stuffStream(OutputStream os, int packetSize, int duration)
        throws IOException
    {
        stuffStream(os, packetSize, duration, 0);
    }

    protected void stuffStream(
        OutputStream os, 
        int packetSize, 
        int duration, 
        int delay)
        throws IOException
    {
        byte[] packet = new byte[packetSize];
        for (int i = 0; i < packet.length; i++)
            packet[i] = (byte) RandomUtil.nextInt(1, 50);
        
        Date now = new Date();
        Date future = new Date(now.getTime());
        DateTimeUtil.add(future, 0, 0, 0, 0, 0, duration);
            
        while (now.before(future))
        {
            os.write(packet);
            os.flush();
            ThreadUtil.sleep(delay);
            now = new Date();
        }
    }
    
    //--------------------------------------------------------------------------
    // OutputStreamListener 
    //--------------------------------------------------------------------------
   
    /** 
     * Listener used to make sure event notification is working correctly.
     */    
    class OutputStreamListener implements 
        MonitoredOutputStream.OutputStreamListener
    {
        private BlockingQueue flushQueue_ = new LinkedBlockingQueue();
        private BlockingQueue closeQueue_ = new LinkedBlockingQueue();
        
        //----------------------------------------------------------------------
        // Public
        //----------------------------------------------------------------------
        
        /**
         * Waits for the stream to be flushed.
         * 
         * @return MonitoredOutputStream.
         * @throws InterruptedException on interruption.
         */
        public OutputStream waitForFlush() throws InterruptedException
        {
            return (OutputStream) flushQueue_.take();
        }

        
        /**
         * Waits for the stream to be closed.
         * 
         * @return MonitoredOutputStream.
         * @throws InterruptedException on interruption.
         */
        public OutputStream waitForClose() throws InterruptedException
        {
            return (OutputStream) closeQueue_.take();
        }
        
        //----------------------------------------------------------------------
        // MonitoredOutputStream.OutputStreamListener Interface
        //----------------------------------------------------------------------
        
        /**
         * @see toolbox.util.io.MonitoredOutputStream.Listener#streamClosed(
         *      MonitoredOutputStream)
         */
        public void streamClosed(MonitoredOutputStream stream)
        {
            try
            {
                closeQueue_.put(stream);
            }
            catch (InterruptedException e)
            {
                logger_.error(e);
            }
        }

        
        /**
         * @see toolbox.util.io.MonitoredOutputStream.Listener#streamFlushed(
         *      MonitoredOutputStream)
         */
        public void streamFlushed(MonitoredOutputStream stream)
        {
            try
            {
                flushQueue_.put(stream);
            }
            catch (InterruptedException e)
            {
                logger_.error(e);
            }
        }
    }
}