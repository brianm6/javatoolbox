package toolbox.junit;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for JUnitUtil.
 * 
 * @see toolbox.junit.JUnitUtil
 */
public class JUnitUtilTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(JUnitUtilTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(JUnitUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests assertSerializable(String) 
     */
    public void testAssertSerializableString()
    {
        logger_.info("Running testAssertSerializableString...");
        
        String s = "hello world";

        // Verify serializes
        JUnitUtil.assertSerializable(s);
        
        // Verify a.equals(b)
        JUnitUtil.assertSerializable(s, true);
        
        // Verify a.compare(b);
        JUnitUtil.assertSerializable(s, false, false, 
            String.CASE_INSENSITIVE_ORDER);
        
        try
        {
            // Verify a != b
            JUnitUtil.assertSerializable(s, false, true, null);
        }
        catch (AssertionFailedError e)
        {
            // Success
        }
    }

    
    /**
     * Tests assertSerializable(String[]) 
     */
    public void testAssertSerializableStringArray()
    {
        logger_.info("Running testAssertSerializableStringArray...");
        
        String[] sa = new String[] {"one", "two", "three"};
        JUnitUtil.assertSerializable(sa);

        try
        {
            // Verify !a.equals(b)
            JUnitUtil.assertSerializable(sa, true);
        }
        catch (AssertionFailedError e)
        {
            // Success
        }

        try
        {
            // Verify a != b
            JUnitUtil.assertSerializable(sa, false, true, null);
        }
        catch (AssertionFailedError e)
        {
            // Success
        }
    }
}
