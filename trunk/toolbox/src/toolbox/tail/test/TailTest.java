package toolbox.tail.test;

import java.io.OutputStreamWriter;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.apache.log4j.Logger;
import toolbox.tail.ITailListener;
import toolbox.tail.Tail;
import toolbox.util.ThreadUtil;
import toolbox.util.io.StringOutputStream;

/**
 * Unit test for Tail
 */
public class TailTest extends TestCase
{
    
    /** Logger **/
    public static final Logger logger_ =
        Logger.getLogger(TailTest.class);

    /**
     * Creates test
     * 
     * @param  name Name
     */
    public TailTest(String name)
    {
        super(name);
    }
    
    
    /**
     * Entrypoint
     * 
     * @param  args  Args
     */
    public static void main(String[] args)
    {
        TestRunner.run(TailTest.class);
    }
    
    /**
     * Tests tail using a reader
     * 
     * @throws  Exception on error
     */
    public void testTailReader() throws Exception
    {
       
        PipedWriter writer = new PipedWriter();
        PipedReader reader = new PipedReader(writer);
        
        ThreadUtil.run(this, "writeDelayed", 
            new Object[] { writer, new Integer(5),
            new Integer(1000), "testing tail reader"} );
        
        Tail tail = new Tail();
        tail.addOutputStream(new StringOutputStream());
        tail.addWriter(new OutputStreamWriter(new StringOutputStream()));
        tail.addTailListener(new TestTailListener());
        logger_.info(tail.toString());
        tail.setTailReader(reader);
        tail.start();
        ThreadUtil.sleep(2000);
        tail.pause();
        ThreadUtil.sleep(2000);
        tail.unpause();
        ThreadUtil.sleep(2000);
    }
    
    
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

/**
 * Test tail listener 
 */
class TestTailListener implements ITailListener
{
    /** Logger **/
    private static final Logger logger_ = TailTest.logger_;
        //Logger.getLogger(TestTailListener.class);
        
    /**
     * Next line is available
     * 
     * @param  line  LineScanner
     */    
    public void nextLine(String line)
    {
        logger_.debug(line);
    }

    /**
     * Tail was started
     */
    public void tailStarted()
    {
        logger_.info("tail started");    
    }
    
    
    /** 
     * Tail was stopped
     */
    public void tailStopped()
    {
        logger_.info("tail stopped");
    }
  
  
    /**
     * Tail was ended
     */
    public void tailEnded()
    {
        logger_.info("tail ended");              
    }
    
    
    /** 
     * Tail was paused
     */
    public void tailPaused()
    {
        logger_.info("tail paused");
    }
    
    
    /**
     * Tail was unpaused
     */
    public void tailUnpaused()
    {
        logger_.info("tail unpaused");
    }
}