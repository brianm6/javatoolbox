package toolbox.util.test;

import java.io.PrintWriter;
import java.io.Writer;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ThreadUtil;

/**
 * Unit test for ThreadUtil
 */
public class ThreadUtilTest extends TestCase
{
    /** Logger */
    private static final Logger logger_ = 
        Logger.getLogger(ThreadUtilTest.class);

    /**
     * Entrypoint
     * 
     * @param  args  Args
     */
    public static void main(String[] args)
    {
        TestRunner.run(ThreadUtilTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Arg constructor
     * 
     * @param  name  Name
     */
    public ThreadUtilTest(String name)
    {
        super(name);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests sleep()
     */
    public void testSleep() 
    {
        logger_.info("Running testSleep...");
        
        // Not much to test
        ThreadUtil.sleep(1000);
    }

    
    /**
     * Tests join(Thread)
     */
    public void testJoinThread()
    {
        logger_.info("Running testJoinThread...");
        
        Thread t = new Thread(new DelayedRunner(2000));
        t.start();
        ThreadUtil.join(t);
    }


    /**
     * Tests join(Thread, millis)
     */
    public void testJoinThreadTimed()
    {
        logger_.info("Running testJoinThreadTimed...");
        
        Thread t = new Thread(new DelayedRunner(4000));
        t.start();
        ThreadUtil.join(t, 1000);
        assertTrue("Thread should still be alive", t.isAlive());
    }


    /**
     * Tests join()
     */
    public void testJoin()
    {
        logger_.info("Running testJoin...");
        
        ThreadUtil.join(1000);
    }


    /**
     * Tests run() on a method with no args
     * 
     * @throws Exception on error
     */
    public void testRunSimple() throws Exception
    {
        logger_.info("Running testRunSimple...");
        
        Tester target = new Tester();
        ThreadUtil.run(target, "pingSimple", null).join();
        assertTrue("ping was not executed", target.pingSimpleCalled_);
    }


    /**
     * Tests run() on a method with simple args and arrays
     * 
     * @throws Exception on error
     */
    public void testRunArgs() throws Exception
    {
        logger_.info("Running testRunArgs...");
        
        Tester target = new Tester();
        
        // Call method with args
        Object[] params = new Object[] 
        {
            "string1",
            new String[] {"element1", "element2"}
        };
       
        ThreadUtil.run(target, "pingArgs", params).join();
        assertTrue("pingArgs was not executed", target.pingArgsCalled_);
    }

 
    /**
     * Tests run() on a method with complex arg types
     * 
     * @throws Exception on error
     */
    public void testRunComplex() throws Exception
    {
        logger_.info("Running testRunComplex...");
        
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
            
        assertTrue("pingComplex was not executed", target.pingComplexCalled_);
    } 


    /**
     * Tests run() on a method with primitive args
     * 
     * @throws Exception on error
     */
    public void testRunPrimitive() throws Exception
    {
        logger_.info("Running testRunPrimitive...");
        
        Tester target = new Tester();
               
        ThreadUtil.run(target, "pingPrimitive", 
            new Object[] 
            { 
                new Integer(10), 
                //new Character('y'),
                new Boolean(true),
                new Long(99L),
                new Float(100.3f)
            }).join();
            
        assertTrue("pingPrimitive was not executed", 
            target.pingPrimitiveCalled_);
    } 
 
 
    /**
     * Tests from an inner class
     * 
     * @throws  Exception on error
     */
    public void testInner() throws Exception
    {
        logger_.info("Running testInner...");
        
        Tester target = new Tester();
        target.testFromInnerClass();
    }  

    //--------------------------------------------------------------------------
    //  Inner Classes
    //--------------------------------------------------------------------------
     
    /**
     * Test class for testRun()
     */   
    protected class Tester
    {
        private boolean pingSimpleCalled_;
        private boolean pingArgsCalled_;
        private boolean pingComplexCalled_;
        private boolean pingPrimitiveCalled_;
        private boolean pingInnerCalled_;
               
        /**
         * Default constructor
         */
        public Tester()
        {
            pingSimpleCalled_    = false;
            pingArgsCalled_      = false;
            pingComplexCalled_   = false;
            pingPrimitiveCalled_ = false;
            pingInnerCalled_     = false;
        }
        
        /**
         * Simplest method - no args 
         */
        public void pingSimple()
        {
            pingSimpleCalled_ = true;
            logger_.info("Called ping()");
        }
        
        /**
         * Method with args of complex type
         */
        public void pingArgs(String str, String[] strArray)
        {
            pingArgsCalled_ = true;
            logger_.info("Called pingArgs(" + str + ", " + 
                ArrayUtil.toString(strArray, false) + ")");
        }
 
        /**
         * Test method with complex args
         */       
        public void pingComplex(Writer pw,  Integer i, Integer i2, String s)
        {
            pingComplexCalled_ = true;
            logger_.info("Called write delayed!");
        }
        
        /**
         * Test method method with primitive args
         */
        public void pingPrimitive(int a, /*char c,*/ boolean b, long l, float f)
        {
            pingPrimitiveCalled_ = true;
            logger_.info("Called pingPrimitive!");
        }
        
        /**
         * Simplest method - no args 
         */
        public void pingInner()
        {
            pingInnerCalled_ = true;
            logger_.info("Called pingInner()");
        }
        
        /**
         * Test calling a method from in innerclass on itself
         */
        public void testFromInnerClass() throws Exception
        {
            ThreadUtil.run(Tester.this, "pingInner", null).join();
            assertTrue("pingInner() not called", pingInnerCalled_);
        }
        
        /**
         * Returns the pingArgsCalled.
         * 
         * @return boolean
         */
        public boolean isPingArgsCalled()
        {
            return pingArgsCalled_;
        }

        /**
         * Returns the pingComplexCalled.
         * 
         * @return boolean
         */
        public boolean isPingComplexCalled()
        {
            return pingComplexCalled_;
        }

        /**
         * Returns the pingInnerCalled.
         * 
         * @return boolean
         */
        public boolean isPingInnerCalled()
        {
            return pingInnerCalled_;
        }

        /**
         * Returns the pingPrimitiveCalled.
         * 
         * @return boolean
         */
        public boolean isPingPrimitiveCalled()
        {
            return pingPrimitiveCalled_;
        }

        /**
         * Returns the pingSimpleCalled.
         * 
         * @return boolean
         */
        public boolean isPingSimpleCalled()
        {
            return pingSimpleCalled_;
        }

        /**
         * Sets the pingArgsCalled.
         * 
         * @param pingArgsCalled The pingArgsCalled to set
         */
        public void setPingArgsCalled(boolean pingArgsCalled)
        {
            this.pingArgsCalled_ = pingArgsCalled;
        }

        /**
         * Sets the pingComplexCalled.
         * 
         * @param pingComplexCalled The pingComplexCalled to set
         */
        public void setPingComplexCalled(boolean pingComplexCalled)
        {
            this.pingComplexCalled_ = pingComplexCalled;
        }

        /**
         * Sets the pingInnerCalled.
         * 
         * @param pingInnerCalled The pingInnerCalled to set
         */
        public void setPingInnerCalled(boolean pingInnerCalled)
        {
            this.pingInnerCalled_ = pingInnerCalled;
        }

        /**
         * Sets the pingPrimitiveCalled.
         * 
         * @param pingPrimitiveCalled The pingPrimitiveCalled to set
         */
        public void setPingPrimitiveCalled(boolean pingPrimitiveCalled)
        {
            this.pingPrimitiveCalled_ = pingPrimitiveCalled;
        }

        /**
         * Sets the pingSimpleCalled.
         * 
         * @param pingSimpleCalled The pingSimpleCalled to set
         */
        public void setPingSimpleCalled(boolean pingSimpleCalled)
        {
            this.pingSimpleCalled_ = pingSimpleCalled;
        }
    }
    
    class DelayedRunner implements Runnable
    {
        private int delay_;
        
        public DelayedRunner(int delay)
        {
            delay_ = delay;           
        }
     
        /**
         * Just goes to sleep for delay_ milliseconds 
         */   
        public void run()
        {
            ThreadUtil.sleep(delay_);
        }
    }
}