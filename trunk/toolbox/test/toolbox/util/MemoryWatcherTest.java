package toolbox.util;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.util.MemoryWatcher}.
 */
public class MemoryWatcherTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(MemoryWatcherTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(MemoryWatcherTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testMemoryWatcher() throws Exception
    {
        logger_.info("Running testMemoryWatcher...");
        
        MemoryWatcher mw = new MemoryWatcher();
        mw.initialize(MapUtils.EMPTY_MAP);
        mw.start();
        ThreadUtil.sleep(3000);
        assertTrue(mw.isRunning());
        logger_.info("Min = " + mw.getMin());
        logger_.info("Max = " + mw.getMax());
        mw.stop();
        mw.destroy();
    }
    
    
    public void testInvalidUsage() throws Exception
    {
        logger_.info("Running testInvalidUsage...");
        
        MemoryWatcher mw = new MemoryWatcher();
        mw.initialize(MapUtils.EMPTY_MAP);
        mw.start();
        
        try
        {
            mw.start();
        }
        catch (IllegalStateException se)
        {
            logger_.debug(se);
        }
    }
}
