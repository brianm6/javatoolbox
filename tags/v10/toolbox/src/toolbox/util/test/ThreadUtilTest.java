package toolbox.util.test;

import java.io.PrintWriter;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;
import toolbox.util.ArrayUtil;
import toolbox.util.ThreadUtil;
import java.io.Writer;

/**
 * Unit test for ThreadUtil
 */
public class ThreadUtilTest extends TestCase
{
    /** Logger **/
    private static final Category logger_ = 
        Category.getInstance(ThreadUtilTest.class);
    
    /**
     * Arg constructor
     */
    public ThreadUtilTest(String name)
    {
        super(name);
    }

    /**
     * Entrypoint
     */
    public static void main(String[] args)
    {
        BasicConfigurator.configure();
        TestRunner.run(ThreadUtilTest.class);
    }

    /**
     * Tests sleep()
     */
    public void testSleep() throws Exception
    {
        /* not much to test */
        ThreadUtil.sleep(1000);
    }

    /**
     * Tests run()
     */
    public void testRunInThread() throws Exception
    {
        Tester target = new Tester();
        
        /* call simple method with no args */
        ThreadUtil.run(target, "ping", new Object[0]);
        
        /* call method with args */
        Object[] params = new Object[] 
        {
            "string1",
            new String[] {"element1", "element2"}
        };
       
        Thread t = ThreadUtil.run(target, "pingArgs", params);
        t.join();
        
        assertTrue("ping was not executed", target.pingCalled);
        assertTrue("pingArgs was not executed", target.pingArgsCalled);
    }
 
    public void testRun() throws Exception
    {
        Tester target = new Tester();
        
        PrintWriter writer = new PrintWriter(System.out);
        Writer w2 = writer;
        ThreadUtil.run(target, "writeDelayed", new Object[] 
            { w2, new Integer(10), new Integer(1000), 
                "We are coming for you!"} );        
    } 
 
 
    /**
     * Test class for testRun()
     */   
    public class Tester
    {
        boolean pingCalled = false;
        boolean pingArgsCalled = false;
        
        /**
         * Simplest method - no args 
         */
        public void ping()
        {
            pingCalled = true;
            logger_.info("Called ping()");
        }
        
        /**
         * Method with args of complex type
         */
        public void pingArgs(String str, String[] strArray)
        {
            pingArgsCalled = true;
            logger_.info("Called pingArgs(" + str + ", " + 
                ArrayUtil.toString(strArray, false) + ")");
        }
        
        public void writeDelayed(Writer pw,  Integer i, Integer i2, String s)
        {
            logger_.info("Called write delayed!");
        }
    }
}