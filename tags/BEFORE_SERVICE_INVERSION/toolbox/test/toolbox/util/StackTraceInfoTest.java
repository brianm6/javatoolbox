package toolbox.util;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit Test for StrackTraceInfo.
 */
public class StackTraceInfoTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(StackTraceInfoTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(StackTraceInfoTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testStackTraceInfo()
    {
        logger_.info("Running testStackTraceInfo...");
        
        try
        {
            throw new Exception("alert");
        }
        catch (Exception e) 
        {
            logger_.debug(StringUtil.banner(ExceptionUtil.getStackTrace(e)));
            StackTraceInfo info = new StackTraceInfo(e, getClass().getName());
            logger_.debug("\n" + BeanUtil.toString(info));
        }
    }
}
