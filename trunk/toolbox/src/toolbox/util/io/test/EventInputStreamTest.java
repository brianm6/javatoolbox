package toolbox.util.io.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.RandomUtil;
import toolbox.util.concurrent.BlockingQueue;
import toolbox.util.io.EventInputStream;
import toolbox.util.io.StringInputStream;

/**
 * Unit test for EventInputStream.
 */
public class EventInputStreamTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(EventInputStreamTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Entrypoint.
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(EventInputStreamTest.class);
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
        assertNotNull(new EventInputStream(new StringInputStream()));
        assertNotNull(new EventInputStream("name", new StringInputStream()));
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
       
        class MyListener implements EventInputStream.Listener
        {
            private BlockingQueue q_ = new BlockingQueue();
            private BlockingQueue c_ = new BlockingQueue();
            
            /**
             * @see toolbox.util.io.EventInputStream.Listener#streamClosed(
             *      toolbox.util.io.EventInputStream)
             */
            public void streamClosed(EventInputStream stream)
            {
                c_.push("close");
            }

            
            /**
             * @see toolbox.util.io.EventInputStream.Listener#bytesRead(
             *      toolbox.util.io.EventInputStream, byte[])
             */
            public void bytesRead(EventInputStream stream, byte[] bytes)
            {
                q_.push(bytes);
            }
            
            
            /**
             * Waits for notification of a byte being read from the inputstream.
             * @return byte that was read.
             * @throws InterruptedException on interruption.
             */
            byte waitForByte() throws InterruptedException
            {
                byte[] b = (byte[]) q_.pull();
                assertEquals(b.length, 1);
                return b[0];
            }
            
            
            /**
             * Waits for notification that the stream has been closed.
             * 
             * @throws InterruptedException on interruption.
             */
            void waitForClose() throws InterruptedException
            {
                c_.pull();
            }
        };
        
        MyListener listener = new MyListener();
        String str = RandomUtil.nextString(500);
        EventInputStream eis = new EventInputStream(new StringInputStream(str));
        eis.addListener(listener);
        
        for (int i = 0; i < str.length(); i++)
        {
            int c = eis.read();
            assertTrue(c >= 0);
            assertEquals(str.charAt(i), (char) c);
            assertEquals(i+1, eis.getCount());
        }
       
        eis.close();
        listener.waitForClose();
        eis.removeListener(listener);
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
        
        EventInputStream.Listener listener = new EventInputStream.Listener()
        {
            private BlockingQueue q_ = new BlockingQueue();
            private BlockingQueue c_ = new BlockingQueue();
            
            /**
             * @see toolbox.util.io.EventInputStream.Listener#streamClosed(
             *      toolbox.util.io.EventInputStream)
             */
            public void streamClosed(EventInputStream stream)
            {
                c_.push("close");
            }
            
            
            /**
             * @see toolbox.util.io.EventInputStream.Listener#bytesRead(
             *      toolbox.util.io.EventInputStream, byte[])
             */
            public void bytesRead(EventInputStream stream, byte[] bytes)
            {
                q_.push(bytes);
            }
            
            
            /**
             * Waits for notification of a byte being read from the inputstream.
             * @return byte that was read.
             * @throws InterruptedException on interruption.
             */
            byte[] waitForBytes() throws InterruptedException
            {
                return (byte[]) q_.pull();
            }
            
            
            /**
             * Waits for notification that the stream has been closed.
             * 
             * @throws InterruptedException on interruption.
             */
            void waitForClose() throws InterruptedException
            {
                c_.pull();
            }
            
        };
        
        String str = RandomUtil.nextString(1000);
        EventInputStream eis = new EventInputStream(new StringInputStream(str));
        eis.addListener(listener);
        
        int cnt = 0;
        
        while (cnt < str.length())
        {
            byte[] b = new byte[RandomUtil.nextInt(1, 30)]; 
            int n = eis.read(b, 0, b.length);
            
            //logger_.info("Read " + n + " chars..");
            
            for (int i = 0; i<n; i++)
                assertEquals(str.charAt(cnt++), b[i]); 
        }
        
        assertEquals(str.length(), eis.getCount());
        eis.close();
        eis.removeListener(listener);
    }
}