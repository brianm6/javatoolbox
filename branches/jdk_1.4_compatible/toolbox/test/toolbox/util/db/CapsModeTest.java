package toolbox.util.db;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.AssertUtil;

/**
 * Unit test for {@link toolbox.util.db.CapsMode}.
 */
public class CapsModeTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(CapsModeTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(CapsModeTest.class);
    }
    
    //--------------------------------------------------------------------------
    // UnitTests
    //--------------------------------------------------------------------------
    
    public void testSerializable()
    {
        logger_.info("Running testSerializable...");
        AssertUtil.assertSerializable(CapsMode.LOWERCASE, true, true, null);
    }
}
