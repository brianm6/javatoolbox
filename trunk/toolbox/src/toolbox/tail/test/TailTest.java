package toolbox.tail.test;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.tail.ITailListener;
import toolbox.tail.Tail;
import toolbox.util.ThreadUtil;
import toolbox.util.concurrent.BlockingQueue;
import toolbox.util.io.StringOutputStream;

/**
 * Unit test for Tail
 */
public class TailTest extends TestCase
{
    /** Logger */
    public static final Logger logger_ =
        Logger.getLogger(TailTest.class);

    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(TailTest.class);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates test
     * 
     * @param  name Name
     */
    public TailTest(String name)
    {
        super(name);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests tail using a reader
     * 
     * @throws  Exception on error
     */
    public void testTailReader() throws Exception
    {
       logger_.info("Running testTailReader...");
       
        PipedWriter writer = new PipedWriter();
        PipedReader reader = new PipedReader(writer);

        ThreadUtil.run(
            this,
            "writeDelayed",
            new Object[] {
                writer,
                new Integer(5),
                new Integer(500),
                "line " });

        Tail tail = new Tail();
        
        // Create sinks for tail and attach them to the tail
        StringOutputStream sos = new StringOutputStream();
        StringWriter sw = new StringWriter();
        tail.addOutputStream(sos);
        tail.addWriter(sw);

        // Create a listener so we can test event
        TestTailListener listener = new TestTailListener();        
        tail.addTailListener(listener);
        
        logger_.info(tail.toString());
        
        // Attach the input reader to the tail
        tail.setTailReader(reader);
        
        // Lifecycle of tail
        tail.start();
        listener.waitForStart();
        ThreadUtil.sleep(1000);
        
        tail.pause();
        listener.waitForPause();
        ThreadUtil.sleep(1000);
        
        tail.unpause();
        listener.waitForUnpause();
        ThreadUtil.sleep(1000);
        
        tail.stop();
        listener.waitForStop();
        
        // Dump contents of the sinks
        logger_.info("OutputStream sink:\n" + sos.getBuffer());
        logger_.info("OutputWriter sink:\n" + sw.toString());
        
        tail.removeOutputStream(sos);
        tail.removeWriter(sw);
        tail.removeTailListener(listener);
    }

    /**
     * Tests tailing of a stream
     * 
     * @throws  Exception on error
     */
    public void testTailStream() throws Exception
    {
//       logger_.info("Running testTailStream...");
//       
//        PipedOutputStream pos = new PipedOutputStream();
//        PipedInputStream  pis = new PipedInputStream(pos);
//
//        ThreadUtil.run(
//            this,
//            "writeDelayed",
//            new Object[] {
//                pos,
//                new Integer(5),
//                new Integer(500),
//                "line " });
//
//        Tail tail = new Tail();
//        
//        // Create sinks for tail and attach them to the tail
//        StringOutputStream sos = new StringOutputStream();
//        StringWriter sw = new StringWriter();
//        tail.addOutputStream(sos);
//        tail.addWriter(sw);
//
//        // Create a listener so we can test event
//        TestTailListener listener = new TestTailListener();        
//        tail.addTailListener(listener);
//        
//        logger_.info(tail.toString());
//        
//        // Attach the input reader to the tail
//        tail.setTailReader(pis);
//        
//        // Lifecycle of tail
//        tail.start();
//        listener.waitForStart();
//        ThreadUtil.sleep(1000);
//        
//        tail.pause();
//        listener.waitForPause();
//        ThreadUtil.sleep(1000);
//        
//        tail.unpause();
//        listener.waitForUnpause();
//        ThreadUtil.sleep(1000);
//        
//        tail.stop();
//        listener.waitForStop();
//        
//        // Dump contents of the sinks
//        logger_.info("OutputStream sink:\n" + sos.getBuffer());
//        logger_.info("OutputWriter sink:\n" + sw.toString());
//        
//        tail.removeOutputStream(sos);
//        tail.removeWriter(sw);
//        tail.removeTailListener(listener);
    }

    
    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------
    
    /**
     * Writes output to a writer in a delayed fashion
     * 
     * @param  writer     Writer to send output to
     * @param  iterations Number of times to iterate
     * @param  delay      Delay between writes in seconds
     * @param  value      String to send to writer
     */                
    public void writeDelayed(PipedWriter writer, int iterations, 
        int delay, String value)
    {
        logger_.info("Running writeDelayed...");
        
        PrintWriter pw = new PrintWriter(writer);
        
        for(int i=0; i<iterations; i++)
        {
            pw.println(i + " " + value);
            pw.flush();
            ThreadUtil.sleep(delay);
        }
        
        pw.close();
    }
}

//--------------------------------------------------------------------------
// Helper Classes
//--------------------------------------------------------------------------

/**
 * Test tail listener 
 */
class TestTailListener implements ITailListener
{
    /** Logger */
    private static final Logger logger_ = TailTest.logger_;
        
    private BlockingQueue startEvents_ = new BlockingQueue();
    private BlockingQueue stopEvents_  = new BlockingQueue();
    private BlockingQueue nextLineEvents_ = new BlockingQueue();
    private BlockingQueue endedEvents_ = new BlockingQueue();
    private BlockingQueue pauseEvents_ = new BlockingQueue();
    private BlockingQueue unpauseEvents_ = new BlockingQueue();
        
    /**
     * Next line is available
     * 
     * @param  line  LineScanner
     */    
    public void nextLine(String line)
    {
        logger_.debug(line);
        
        try
        {
            nextLineEvents_.push("nextLine");    
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
        
    }

    /**
     * Tail was started
     */
    public void tailStarted()
    {
        logger_.info("tail started");
        
        try
        {
            startEvents_.push("start");    
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
    }
    
    
    /** 
     * Tail was stopped
     */
    public void tailStopped()
    {
        logger_.info("tail stopped");
        
        try
        {
            stopEvents_.push("stop");    
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
    }
  
  
    /**
     * Tail was ended
     */
    public void tailEnded()
    {
        logger_.info("tail ended");
        
        try
        {
            endedEvents_.push("ended");    
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
    }
    
    
    /** 
     * Tail was paused
     */
    public void tailPaused()
    {
        logger_.info("tail paused");
        
        try
        {
            pauseEvents_.push("pause");    
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
    }
    
    
    /**
     * Tail was unpaused
     */
    public void tailUnpaused()
    {
        logger_.info("tail unpaused");
        
        try
        {
            unpauseEvents_.push("unpause");    
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
    }
    
    public void waitForStart() throws InterruptedException
    {
        startEvents_.pull();
    }
    
    public void waitForStop() throws InterruptedException
    {
        stopEvents_.pull();
    }
    
    public void waitForPause() throws InterruptedException
    {
        pauseEvents_.pull();
    }
    
    public void waitForUnpause() throws InterruptedException
    {
        unpauseEvents_.pull();
    }
}