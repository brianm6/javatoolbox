package toolbox.util.invoker.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ElapsedTime;
import toolbox.util.invoker.BlockingInvoker;
import toolbox.util.invoker.Invoker;

/**
 * Unit test for BlockingInvoker
 */
public class BlockingInvokerTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(BlockingInvokerTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(BlockingInvokerTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests that a call to invoke(Runnable) method blocks for >= the amount of 
     * time that it takes to execute the method.
     * 
     * @throws Exception on error
     */
    public void testInvokeRunnable() throws Exception
    {
        logger_.info("Running testInvokeRunnable...");

        int delay = 3000;
        Invoker invoker = new BlockingInvoker();
        TimedRunner invokable = new TimedRunner(delay);
        ElapsedTime time = new ElapsedTime();
        invoker.invoke(invokable);
        time.setEndTime();

        assertTrue("Method was not invoked", invokable.wasInvoked());

        assertTrue(
            "Method did not execute in expected amount of time",
            time.getTotalMillis() >= delay);
    }

    /**
     * Tests that a call to invoke(Object, Method, Object[]) blocks for >= the 
     * amount of time that it takes to execute the method.
     * 
     * @throws Exception on error
     */
    public void testInvokeReflectively() throws Exception
    {
        logger_.info("Running testInvokeReflectively...");

        int delay = 3000;
        Invoker invoker = new BlockingInvoker();
        TimedRunner invokable = new TimedRunner(delay);
        ElapsedTime time = new ElapsedTime();
        invoker.invoke(invokable, "run", null);
        time.setEndTime();

        assertTrue("Method was not invoked", invokable.wasInvoked());

        assertTrue(
            "Method did not execute in expected amount of time",
            time.getTotalMillis() >= delay);
    }

    /**
     * Stress tests invoke()
     * 
     * @throws Exception on error
     */
    public void testInvokeStressTest() throws Exception
    {
        logger_.info("Running testInvokeStressTest...");

        int delay = 1;
        int numIterations = 1000;

        Invoker invoker = new BlockingInvoker();
        TimedRunner[] invokables = new TimedRunner[numIterations];

        // Stress    
        for (int i = 0; i < numIterations; i++)
        {
            invokables[i] = new TimedRunner(delay);
            invoker.invoke(invokables[i]);
        }

        // Verify
        for (int i = 0; i < numIterations; i++)
        {
            assertTrue("Method was not invoked", invokables[i].wasInvoked());
        }
    }
}
