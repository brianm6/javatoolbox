package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.DoubleUtil;
import toolbox.util.MathUtil;


/**
 * MathUtil test class
 */
public class MathUtilTest extends TestCase
{
    /**
     * Starts the test case and runs the entire suite.
     *
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(MathUtilTest.class);
    }
    
    
    /**
     * MathUtilTest constructor comment.
     * 
     * @param aName String
     */
    public MathUtilTest(String aName)
    {
        super(aName);
    }

    
    /**
     * Tests isEven()
     */
    public void testIsEven() throws Exception
    {
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
        assertTrue("0 is not odd!", !MathUtil.isOdd(0));
        assertTrue("1 is odd!", MathUtil.isOdd(1));
        assertTrue("-1 is odd!", MathUtil.isOdd(-1));
        assertTrue("2 is not odd!", !MathUtil.isOdd(2));
        assertTrue("999 is odd!", MathUtil.isOdd(999));
        assertTrue("-1000 is not odd!", !MathUtil.isOdd(-1000));
    }
}