package toolbox.junit.test;

import java.util.Enumeration;

import junit.framework.TestCase;
import junit.runner.TestCollector;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.JarTestCollector;

/**
 * Unit test for JarTestCollector
 */
public class JarTestCollectorTest extends TestCase
{
    /** Logger **/
    private static final Logger logger_ =
        Logger.getLogger(JarTestCollectorTest.class);
            
    /**
     * Entrypoint
     *
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(JarTestCollectorTest.class);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for JarTestCollectorTest
     * 
     * @param arg0  Name
     */
    public JarTestCollectorTest(String arg0)
    {
        super(arg0);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests collectTests()
     */
    public void testCollectTests()
    {
        logger_.info("Running testCollectTests...");
        
        TestCollector tc = new JarTestCollector();
        
        for(Enumeration e = tc.collectTests(); e.hasMoreElements(); )
        {
            String classname = (String)e.nextElement();
            System.out.println("Testclass=" + classname);
        }
        
    }
}