package toolbox.tail.test;

import java.io.OutputStreamWriter;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;
import toolbox.tail.ITailListener;
import toolbox.tail.Tail;
import toolbox.util.ThreadUtil;

/**
 * @author analogue
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class TailTest extends TestCase
{
    private static final Category logger_ =
        Category.getInstance(TailTest.class);

    public TailTest(String name)
    {
        super(name);
    }
    
    public static void main(String[] args)
    {
        BasicConfigurator.configure();
        TestRunner.run(TailTest.class);
    }
    
    public void testTailReader() throws Exception
    {
       
        PipedWriter writer = new PipedWriter();
        PipedReader reader = new PipedReader(writer);
        
        ThreadUtil.run(this, "writeDelayed", new Object[] { writer, new Integer(5),
            new Integer(1000), "We are coming for you!"} );
        
        Tail tail = new Tail();
        tail.addOutputStream(System.out);
        tail.addWriter(new OutputStreamWriter(System.out));
        tail.addTailListener(new TestTailListener());
        logger_.info(tail.toString());
        tail.tail(reader);
        ThreadUtil.sleep(2000);
        tail.pause();
        ThreadUtil.sleep(10000);
        tail.unpause();
        ThreadUtil.sleep(4000);
    }
                    
    public void writeDelayed(PipedWriter writer, Integer iterations, Integer delay, String value)
    {
        PrintWriter pw = new PrintWriter(writer);
        
        for(int i=0; i<iterations.intValue(); i++)
        {
            pw.println(i + " " + value);
            pw.flush();
            ThreadUtil.sleep(delay.intValue());
        }
        
        pw.close();
    }
}

class TestTailListener implements ITailListener
{
    private static final Category logger_ =
        Category.getInstance(TestTailListener.class);
        
        
    public void nextLine(String line)
    {
        System.out.print(line);
    }

    public void tailStarted()
    {
        logger_.info("tail started");    
    }
    
    public void tailStopped()
    {
        logger_.info("tail stopped");
    }
  
    public void tailEnded()
    {
        logger_.info("tail ended");              
    }
    
    public void tailPaused()
    {
        logger_.info("tail paused");
    }
    
    public void tailUnpaused()
    {
        logger_.info("tail unpaused");
    }
}