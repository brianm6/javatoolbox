package toolbox.util.test;

import java.io.PrintWriter;

import junit.framework.TestCase;
import junit.textui.TestRunner;
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
     * 
     * @param  name  Name
     */
    public ThreadUtilTest(String name)
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
        //BasicConfigurator.configure();
        TestRunner.run(ThreadUtilTest.class);
    }

    /**
     * Tests sleep()
     */
    public void testSleep() 
    {
        /* not much to test */
        ThreadUtil.sleep(1000);
    }

    /**
     * Tests run() on a method with no args
     * 
     * @throws Exception on error
     */
    public void testRunSimple() throws Exception
    {
        Tester target = new Tester();
        ThreadUtil.run(target, "pingSimple", new Object[0]).join();
        assertTrue("ping was not executed", target.pingSimpleCalled);
    }

    /**
     * Tests run() on a method with simple args and arrays
     * 
     * @throws Exception on error
     */
    public void testRunArgs() throws Exception
    {
        Tester target = new Tester();
        
        /* call method with args */
        Object[] params = new Object[] 
        {
            "string1",
            new String[] {"element1", "element2"}
        };
       
        ThreadUtil.run(target, "pingArgs", params).join();
        assertTrue("pingArgs was not executed", target.pingArgsCalled);
    }

 
    /**
     * Tests run() on a method with complex arg types
     * 
     * @throws Exception on error
     */
    public void testRunComplex() throws Exception
    {
        Tester target = new Tester();
        
        PrintWriter writer = new PrintWriter(System.out);
        
        ThreadUtil.run(target, "pingComplex", 
            new Object[] 
            { 
                writer, 
                new Integer(10), 
                new Integer(1000), 
                "hello from testRunComplex()"
            }).join();
            
        assertTrue("pingComplex was not executed", target.pingComplexCalled);
    } 

    /**
     * Tests run() on a method with primitive args
     * 
     * @throws Exception on error
     */
    public void testRunPrimitive() throws Exception
    {
        Tester target = new Tester();
               
        ThreadUtil.run(target, "pingPrimitive", 
            new Object[] 
            { 
                new Integer(10), 
                new Character('y'),
                new Boolean(true),
                new Long(99L),
                new Float(100.3f)
            }).join();
            
        assertTrue("pingPrimitive was not executed", 
            target.pingPrimitiveCalled);
    } 
 
    /**
     * Test class for testRun()
     */   
    public class Tester
    {
        public boolean pingSimpleCalled;
        public boolean pingArgsCalled;
        public boolean pingComplexCalled;
        public boolean pingPrimitiveCalled;
               
        /**
         * Default constructor
         */
        public Tester()
        {
            pingSimpleCalled    = false;
            pingArgsCalled      = false;
            pingComplexCalled   = false;
            pingPrimitiveCalled = false;
        }
        
        /**
         * Simplest method - no args 
         */
        public void pingSimple()
        {
            pingSimpleCalled = true;
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
 
        /**
         * Test method with complex args
         */       
        public void pingComplex(Writer pw,  Integer i, Integer i2, String s)
        {
            pingComplexCalled = true;
            logger_.info("Called write delayed!");
        }
        
        /**
         * Test method method with primitive args
         */
        public void pingPrimitive(int a, char c, boolean b, long l, float f)
        {
            pingPrimitiveCalled = true;
            logger_.info("Called pingPrimitive!");
        }
    }
}