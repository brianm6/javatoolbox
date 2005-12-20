package toolbox.junit;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.junit.AssertUtil}.
 */
public class AssertUtilTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(AssertUtilTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(AssertUtilTest.class);
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
        AssertUtil.assertSerializable(s);
        
        // Verify a.equals(b)
        AssertUtil.assertSerializable(s, true);
        
        // Verify a.compare(b);
        AssertUtil.assertSerializable(s, false, false, 
            String.CASE_INSENSITIVE_ORDER);
        
        try
        {
            // Verify a != b
            AssertUtil.assertSerializable(s, false, true, null);
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
        AssertUtil.assertSerializable(sa);

        try
        {
            // Verify !a.equals(b)
            AssertUtil.assertSerializable(sa, true);
        }
        catch (AssertionFailedError e)
        {
            // Success
        }

        try
        {
            // Verify a != b
            AssertUtil.assertSerializable(sa, false, true, null);
        }
        catch (AssertionFailedError e)
        {
            // Success
        }
    }
}
