package toolbox.util.test;

import java.text.DecimalFormat;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import toolbox.util.DoubleUtil;

/**
 * Unit test for DoubleUtil
 */
public class DoubleUtilTest extends TestCase
{
    public static final DecimalFormat TWO_DIGIT_FORMAT = 
        new DecimalFormat("#########.##");
        
    /**
     * Entry point
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(DoubleUtilTest.class);
    }


    /**
     * Constructor for DoubleUtilTest.
     * 
     * @param arg0  name
     */
    public DoubleUtilTest(String arg0)
    {
        super(arg0);
    }


    /**
     * Tests isDouble() for scenarios where the result is true
     */
    public void testIsDoubleTrue() throws Exception
    {
        /* positive */
        assertTrue("0 is a double", DoubleUtil.isDouble("0"));
        assertTrue("0.00 is a double", DoubleUtil.isDouble("0.00"));
        assertTrue("12345 is a double", DoubleUtil.isDouble("12345"));
        assertTrue("-12345 is a double", DoubleUtil.isDouble("-12345"));
        assertTrue("123.45 is a double", DoubleUtil.isDouble("123.45"));
        assertTrue("-123.45 is a double", DoubleUtil.isDouble("-123.45"));
        assertTrue("number with spaces is a double",
            DoubleUtil.isDouble("    87.774747   "));
    }


    /**
     * Tests isDouble() for scenarios where the result is false
     */
    public void testIsDoubleFalse() throws Exception
    {
        /* negative */
        assertTrue("null is not a double", !DoubleUtil.isDouble(null));
        assertTrue("empty string is not a double", !DoubleUtil.isDouble(""));
        assertTrue("alpha is not a double", !DoubleUtil.isDouble("a"));

        assertTrue("blank string is not a double", 
            !DoubleUtil.isDouble("         "));

        assertTrue("alphanumeric is not a double", 
            !DoubleUtil.isDouble("fun stuff!"));

        assertTrue("ascii junk is not a double",
            !DoubleUtil.isDouble("al;dfj0&**&&*345jklsjdf;90q354090**"));
    }


    /**
     * Test median() for an empty set
     */
    public void testMedianEmpty() throws Exception
    {
        double[] d = new double[0];

        try
        {
            double e = DoubleUtil.median(d);
            fail("media should have failed for an empty set");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(true);
        }
    }


    /**
     * Tests median() for even set
     */
    public void testMedianEven() throws Exception
    {
        int len = 6;

        double[] d = new double[len];
        for (int i = 0; i < d.length; i++)
            d[i] = i + 1;

        double e = DoubleUtil.median(d);

        assertEquals("values don't match", 3.5, e, 0);
    }


    /**
     * Tests median() for an odd set
     */
    public void testMedianOdd() throws Exception
    {
        int len = 5;

        double[] d = new double[len];

        for (int i = 0; i < d.length; i++)
            d[i] = i + 1;

        double e = DoubleUtil.median(d);

        assertEquals("values don't match", 3, e, 0);
    }


    /**
     * Tests median for a set of 1
     */
    public void testMedianOne() throws Exception
    {
        double[] d = new double[] { 99 };
        double e = DoubleUtil.median(d);
        assertEquals("values don't match", d[0], e, 0);
    }


    /**
     * Tests round() for rounding a number down
     */
    public void testRoundDown() throws Exception
    {
        double d = 100.123;
        String s = DoubleUtil.round(d, TWO_DIGIT_FORMAT);
        assertEquals("Rounding failed.", "100.12", s);
    }


    /**
     * Tests round() for a big ugly number
     */
    public void testRoundMax() throws Exception
    {
        double d = 100.6585754859606858456484758586585785765785;
        String s = DoubleUtil.round(d, TWO_DIGIT_FORMAT);
        assertEquals("Rounding failed.", "100.66", s);
    }


    /**
     * Tests round() with one decimal number
     */
    public void testRoundOneDecimal() throws Exception
    {
        double d = 100.10;
        String s = DoubleUtil.round(d, TWO_DIGIT_FORMAT);
        assertEquals("Rounding failed.", "100.1", s);
    }


    /**
     * Tests round() with a two decimal number
     */
    public void testRoundTwoDecimal() throws Exception
    {
        double d = 100.12;
        String s = DoubleUtil.round(d, TWO_DIGIT_FORMAT);
        assertEquals("Rounding failed.", "100.12", s);

        d = 100.120000;
        s = DoubleUtil.round(d, TWO_DIGIT_FORMAT);
        assertEquals("Rounding failed.", "100.12", s);
    }


    /**
     * Tests round() for rounding a number up
     */
    public void testRoundUp() throws Exception
    {
        double d = 100.127;
        String s = DoubleUtil.round(d, TWO_DIGIT_FORMAT);
        assertEquals("Rounding failed.", "100.13", s);
    }


    /**
     * Tests round() with a whole number
     */
    public void testRoundWholeNumber() throws Exception
    {
        double d = 100;
        String s = DoubleUtil.round(d, TWO_DIGIT_FORMAT);
        assertEquals("Rounding failed.", "100", s);
    }
}