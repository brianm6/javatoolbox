package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.MathUtil;


/**
 * MathUtil test class
 */
public class MathUtilTest extends TestCase
{
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
     * Starts the test case and runs the entire suite.
     *
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(MathUtilTest.class);
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


    /**
     * Tests isDouble() for scenarios where the result is true
     */
    public void testIsDoublePositive() throws Exception
    {
        /* positive */
        assertTrue("0 is a double", MathUtil.isDouble("0"));
        assertTrue("0.00 is a double", MathUtil.isDouble("0.00"));
        assertTrue("12345 is a double", MathUtil.isDouble("12345"));
        assertTrue("-12345 is a double", MathUtil.isDouble("-12345"));
        assertTrue("123.45 is a double", MathUtil.isDouble("123.45"));
        assertTrue("-123.45 is a double", MathUtil.isDouble("-123.45"));
        assertTrue("number with spaces is a double", 
            MathUtil.isDouble("    87.774747   "));
    }
    
    
    /**
     * Tests isDouble() for scenarios where the result is false
     */
    public void testIsDoubleNegative() throws Exception
    {
        /* negative */
        assertTrue("null is not a double", !MathUtil.isDouble(null));
        assertTrue("empty string is not a double", !MathUtil.isDouble(""));
        assertTrue("alpha is not a double", !MathUtil.isDouble("a"));
                
        assertTrue("blank string is not a double", 
            !MathUtil.isDouble("         "));
        
        assertTrue("alphanumeric is not a double", 
            !MathUtil.isDouble("fun stuff!"));
            
        assertTrue("ascii junk is not a double", 
            !MathUtil.isDouble("al;dfj0&**&&*345jklsjdf;90q354090**"));
    }
}