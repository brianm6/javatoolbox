package toolbox.util.test;

import java.text.DecimalFormat;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.DoubleUtil;

/**
 * Unit test for DoubleUtil
 */
public class DoubleUtilTest extends TestCase
{
    /** Logger */
    private static final Logger logger_ =
        Logger.getLogger(DoubleUtilTest.class);
    
    /** 
     * Format 
     */
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

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for DoubleUtilTest.
     * 
     * @param arg0  name
     */
    public DoubleUtilTest(String arg0)
    {
        super(arg0);
    }

    //--------------------------------------------------------------------------
    // Unit Test
    //--------------------------------------------------------------------------
    
    /**
     * Tests isDouble() for scenarios where the result is true
     * 
     * @throws  Exception on error
     */
    public void testIsDoubleTrue() throws Exception
    {
        logger_.info("Running testIsDoubleTrue...");
        
        // positive
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
     * 
     * @throws  Exception on error
     */
    public void testIsDoubleFalse() throws Exception
    {
        logger_.info("Running testIsDoubleFalse...");
        
        // negative
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
     * 
     * @throws  Exception on error
     */
    public void testMedianEmpty() throws Exception
    {
        logger_.info("Running testMedianEmpty...");
        
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
     * 
     * @throws  Exception on error
     */
    public void testMedianEven() throws Exception
    {
        logger_.info("Running testMedianEven...");
        
        int len = 6;

        double[] d = new double[len];
        for (int i = 0; i < d.length; i++)
            d[i] = i + 1;

        double e = DoubleUtil.median(d);

        assertEquals("values don't match", 3.5, e, 0);
    }


    /**
     * Tests median() for an odd set
     * 
     * @throws  Exception on error
     */
    public void testMedianOdd() throws Exception
    {
        logger_.info("Running testMedianOdd...");
        
        int len = 5;

        double[] d = new double[len];

        for (int i = 0; i < d.length; i++)
            d[i] = i + 1;

        double e = DoubleUtil.median(d);

        assertEquals("values don't match", 3, e, 0);
    }


    /**
     * Tests median for a set of 1
     * 
     * @throws  Exception on error
     */
    public void testMedianOne() throws Exception
    {
        logger_.info("Running testMedianOne...");
        
        double[] d = new double[] { 99 };
        double e = DoubleUtil.median(d);
        assertEquals("values don't match", d[0], e, 0);
    }


    /**
     * Tests round() for rounding a number down
     * 
     * @throws  Exception on error
     */
    public void testRoundDown() throws Exception
    {
        logger_.info("Running testRoundDown...");
        
        double d = 100.123;
        String s = DoubleUtil.round(d, TWO_DIGIT_FORMAT);
        assertEquals("Rounding failed.", "100.12", s);
    }


    /**
     * Tests round() for a big ugly number
     * 
     * @throws  Exception on error
     */
    public void testRoundMax() throws Exception
    {
        logger_.info("Running testRoundMax...");
        
        double d = 100.6585754859606858456484758586585785765785;
        String s = DoubleUtil.round(d, TWO_DIGIT_FORMAT);
        assertEquals("Rounding failed.", "100.66", s);
    }


    /**
     * Tests round() with one decimal number
     * 
     * @throws  Exception on error
     */
    public void testRoundOneDecimal() throws Exception
    {
        logger_.info("Running testRoundOneDecimal...");
        
        double d = 100.10;
        String s = DoubleUtil.round(d, TWO_DIGIT_FORMAT);
        assertEquals("Rounding failed.", "100.1", s);
    }


    /**
     * Tests round() with a two decimal number
     * 
     * @throws  Exception on error
     */
    public void testRoundTwoDecimal() throws Exception
    {
        logger_.info("Running testRoundTwoDecimal...");
        
        double d = 100.12;
        String s = DoubleUtil.round(d, TWO_DIGIT_FORMAT);
        assertEquals("Rounding failed.", "100.12", s);

        d = 100.120000;
        s = DoubleUtil.round(d, TWO_DIGIT_FORMAT);
        assertEquals("Rounding failed.", "100.12", s);
    }


    /**
     * Tests round() for rounding a number up
     * 
     * @throws  Exception on error
     */
    public void testRoundUp() throws Exception
    {
        logger_.info("Running testRoundUp...");
        
        double d = 100.127;
        String s = DoubleUtil.round(d, TWO_DIGIT_FORMAT);
        assertEquals("Rounding failed.", "100.13", s);
    }


    /**
     * Tests round() with a whole number
     * 
     * @throws  Exception on error
     */
    public void testRoundWholeNumber() throws Exception
    {
        logger_.info("Running testRoundWholeNumber...");
        
        double d = 100;
        String s = DoubleUtil.round(d, TWO_DIGIT_FORMAT);
        assertEquals("Rounding failed.", "100", s);
    }
    

    /**
     * Tests round() for a double passed in as a string
     */
    public void testRoundString()
    {
        logger_.info("Running testRoundString...");
        
        double d = 100.123;
        String s = DoubleUtil.round(d+"", TWO_DIGIT_FORMAT);
        assertEquals("Rounding failed.", "100.12", s);
    }

    
    /** 
     * Tests isBetween()
     */
    public void testIsBetween()
    {
        logger_.info("Running testIsBetween...");
        
        double a = 5.5;
        double b = 10.7;
        double c = -34.2;
        
        assertTrue(DoubleUtil.isBetween(a, c, b));
        assertTrue(DoubleUtil.isBetween(a, a, a));
        assertTrue(DoubleUtil.isBetween(b, b, b));
        assertTrue(DoubleUtil.isBetween(c, c, c));
        assertTrue(!DoubleUtil.isBetween(a, b, c));       
        assertTrue(!DoubleUtil.isBetween(b, c, c));
        assertTrue(!DoubleUtil.isBetween(a, b, b));
        assertTrue(!DoubleUtil.isBetween(c, a, b));
    }
 
    
    /**
     * Tests average()
     */
    public void testAverage()
    {
        logger_.info("Running testAverage...");
        
        double[] d = new double[] { 1.0, 1.1, 1.2 };
        assertEquals( (double) 1.1, (double) DoubleUtil.average(d), 
            /* this should be 0.0 */ 0.01);
    }
    
    
    /**
     * Tests difference()
     */
    public void testDifference()
    {
        logger_.info("Running testDifference...");
        
        double[] a = new double[] { 1.1, 4.5, 100.6 };
        double[] b = new double[] { 1.1, 4.5, 100.6 };
        double[] c = DoubleUtil.difference(a, b);
    
        for (int i=0; i<c.length; i++)
            assertEquals(0.0, c[i], 0.0); 
    }
    
    
    /**
     * Tests occurs()
     */
    public void testOccurs()
    {
        logger_.info("Running testOccurs...");
           
        double[] a = new double[] { 2.3, 4.5, 6.7 };  // occurs once
        double[] b = new double[] { 4.5, 4.5, 4.5 };  // occurs thrice
        double[] c = new double[] { 9.9, 9.9, 9.9 };  // occurs none
        
        assertEquals(1, DoubleUtil.occurs(4.5, a));
        assertEquals(3, DoubleUtil.occurs(4.5, b));
        assertEquals(0, DoubleUtil.occurs(4.5, c));
    }
    
    
    /**
     * Tests sum()
     */
    public void testSum()
    {
        logger_.info("Running testSum...");
        
        double[] d = new double[] { 1.0, 1.1, 1.2 };
        assertEquals( (double) 3.3, (double) DoubleUtil.sum(d), 0.0);
    }
    
}