package toolbox.util.io;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import toolbox.util.RandomUtil;
import toolbox.util.io.throughput.MockThroughputListener;
import toolbox.util.io.throughput.ThroughputMonitor;

/**
 * Unit test for {@link toolbox.util.io.MonitoredInputStream}.
 */
public class MonitoredInputStreamTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(MonitoredInputStreamTest.class);

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
        TestRunner.run(MonitoredInputStreamTest.class);
    }
    
    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the constructors.
     */
    public void testConstructors()
    {
        logger_.info("Running testConstructors...");
        assertNotNull(new MonitoredInputStream(new StringInputStream()));
        assertNotNull(new MonitoredInputStream("name", new StringInputStream()));
    }
    
    
    /**
     * Tests the currentThroughput() on the ThroughtputMonitor.
     * 
     * @throws Exception on error.
     */
    public void testCurrentThroughput() throws Exception
    {
        logger_.info("Running testCurrentThroughput...");
        
        String data = StringUtils.repeat("*", 200000);

        Bandwidth bandwidth = 
            new Bandwidth(33000, 33000, Bandwidth.TYPE_RECEIVED);
        
        ThrottledInputStream tis = 
            new ThrottledInputStream(new StringInputStream(data), false);
        
        tis.setThrottle(bandwidth);
        
        Logger.getLogger(Bandwidth.class).setLevel(Level.ERROR);
        
        MonitoredInputStream mis = new MonitoredInputStream(tis);
       
        ThroughputMonitor monitor = mis.getThroughputMonitor();
        monitor.addThroughputListener(new MockThroughputListener());
        monitor.setSampleInterval(1000);
        monitor.setMonitoringThroughput(true);
        
        
        while (mis.read() != -1);
        
        logger_.info("[100000 byte packet]");
        //for (int i = 0; i< 1000000; i++)
        //    bos.write(i);
        
        //while (true)
        //    bos.write(99);

        //while (true)
        //    mos.write(99);
        
        //stuffStream(mis, 1000000, 10, delay); 
               
        monitor.setMonitoringThroughput(false);
    }
    
    
    /**
     * Tests the read() method and event notification for bytesRead() and
     * streamClosed().
     * 
     * @throws Exception on error.
     */
    public void testRead() throws Exception
    {
        logger_.info("Running testRead...");
       
        class MyListener implements MonitoredInputStream.InputStreamListener
        {
            private BlockingQueue c_ = new LinkedBlockingQueue();
            
            /**
             * @see toolbox.util.io.MonitoredInputStream.Listener#streamClosed(
             *      MonitoredOutputStream)
             */
            public void streamClosed(MonitoredInputStream stream)
            {
                try
                {
                    c_.put("close");
                }
                catch (InterruptedException e)
                {
                    logger_.error(e);
                }
            }
            
            
            /**
             * Waits for notification that the stream has been closed.
             * 
             * @throws InterruptedException on interruption.
             */
            void waitForClose() throws InterruptedException
            {
                c_.take();
            }
        };
        
        MyListener listener = new MyListener();
        String str = RandomUtil.nextString(500);
        
        MonitoredInputStream eis = 
            new MonitoredInputStream(new StringInputStream(str));
        
        eis.addInputStreamListener(listener);
        
        for (int i = 0; i < str.length(); i++)
        {
            int c = eis.read();
            assertTrue(c >= 0);
            assertEquals(str.charAt(i), (char) c);
            assertEquals(i + 1, eis.getTransferredMonitor().getBytesTransferred());
        }
       
        eis.close();
        listener.waitForClose();
        eis.removeInputStreamListener(listener);
    }

    
    /**
     * Tests the read(byte[], off, len) method and event notification for bytes
     * read and stream closed.
     * 
     * @throws Exception on error.
     */
    public void testRead2() throws Exception
    {
        logger_.info("Running testRead2...");
        
        MonitoredInputStream.InputStreamListener listener = 
            new MonitoredInputStream.InputStreamListener()
        {
            private BlockingQueue c_ = new LinkedBlockingQueue();
            
            /**
             * @see toolbox.util.io.MonitoredInputStream.Listener#streamClosed(
             *      MonitoredOutputStream)
             */
            public void streamClosed(MonitoredInputStream stream)
            {
                try
                {
                    c_.put("close");
                }
                catch (InterruptedException e)
                {
                    logger_.error(e);
                }
            }
            
            
            /**
             * Waits for notification that the stream has been closed.
             * 
             * @throws InterruptedException on interruption.
             */
            void waitForClose() throws InterruptedException
            {
                c_.take();
            }
            
        };
        
        String str = RandomUtil.nextString(1000);
        
        MonitoredInputStream eis = 
            new MonitoredInputStream(new StringInputStream(str));
        
        eis.addInputStreamListener(listener);
        
        int cnt = 0;
        
        while (cnt < str.length())
        {
            byte[] b = new byte[RandomUtil.nextInt(1, 30)]; 
            int n = eis.read(b, 0, b.length);
            
            //logger_.info("Read " + n + " chars..");
            
            for (int i = 0; i < n; i++)
                assertEquals(str.charAt(cnt++), b[i]); 
        }
        
        assertEquals(str.length(), 
            eis.getTransferredMonitor().getBytesTransferred());
        
        eis.close();
        eis.removeInputStreamListener(listener);
    }
}