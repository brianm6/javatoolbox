package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.MathUtil;

/**
 * MathUtil test class
 */
public class MathUtilTest extends TestCase
{
    /** Logger **/
    private static final Logger logger_ = Logger.getLogger(MathUtilTest.class);
    
    /**
     * Starts the test case and runs the entire suite.
     *
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(MathUtilTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * MathUtilTest constructor comment.
     * 
     * @param aName String
     */
    public MathUtilTest(String aName)
    {
        super(aName);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
        
    /**
     * Tests isEven()
     */
    public void testIsEven() throws Exception
    {
        logger_.info("Running testIsEven...");
        
        assertTrue("0 is even!", MathUtil.isEven(0));
        assertTrue("1 is not even!", !MathUtil.isEven(1));
        assertTrue("-1 is not even!", !MathUtil.isEven(-1));
        assertTrue("2 is even!", MathUtil.isEven(2));
        assertTrue("999 is not even!", !MathUtil.isEven(999));
        assertTrue("-1000 is even!", MathUtil.isEven(-1000));
    }

    
    /**
     * Tests isOdd()
     */
    public void testIsOdd() throws Exception
    {
        logger_.info("Running testIsOdd...");
        
        assertTrue("0 is not odd!", !MathUtil.isOdd(0));
        assertTrue("1 is odd!", MathUtil.isOdd(1));
        assertTrue("-1 is odd!", MathUtil.isOdd(-1));
        assertTrue("2 is not odd!", !MathUtil.isOdd(2));
        assertTrue("999 is odd!", MathUtil.isOdd(999));
        assertTrue("-1000 is not odd!", !MathUtil.isOdd(-1000));
    }
}