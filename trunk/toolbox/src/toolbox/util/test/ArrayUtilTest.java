package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;
import toolbox.util.ArrayUtil;

/**
 * ArrayUtil unit test class
 */
public class ArrayUtilTest extends TestCase
{
    
    /** Logger **/
    private static final Category logger = 
        Category.getInstance(ArrayUtilTest.class);

    /**
     * ArrayUtilTest constructor
     *  
     * @param aName String
     */
    public ArrayUtilTest(String aName)
    {
        super(aName);
    }


    /**
     * Starts the test case and runs the entire suite.
     *
     * @param args An array of unused command-line arguments
     */
    public static void main(String[] args)
    {
        BasicConfigurator.configure();
        TestRunner.run(ArrayUtilTest.class);
    }

    /**
     * Test subset() for subset equal to array
     */
    public void testSubsetDoubleAll() 
    {
        int len = 10;
        double[] d = new double[len];

        for (int i = 0; i < d.length; i++)
            d[i] = i;

        double[] e = ArrayUtil.subset(d, 0, d.length - 1);
        assertEquals("subset should be same size as original", d.length, 
                     e.length);

        for (int i = 0; i < d.length; i++)
            assertEquals("values don't match", d[i], e[i], 0);
    }


    /**
     * Test subset() for empty array
     */
    public void testSubsetDoubleEmpty() 
    {
        double[] d = new double[0];
        double[] e = ArrayUtil.subset(d, 0, 0);
        assertEquals("subset should be empty", 0, e.length);
    }


    /**
     * Test subset() for subset first half of array
     */
    public void testSubsetDoubleFirstHalf() 
    {
        int len = 10;
        double[] d = new double[len];

        for (int i = 0; i < d.length; i++)
            d[i] = i;

        double[] e = ArrayUtil.subset(d, 0, (d.length / 2) - 1);
        assertEquals("subset should be half size of original", d.length / 2, 
                     e.length);

        for (int i = 0; i < e.length; i++)
            assertEquals("values don't match", d[i], e[i], 0);
    }


    /**
     * Test subset() for array of length 1
     */
    public void testSubsetDoubleOne() 
    {
        double[] d = new double[]{99};
        double[] e = ArrayUtil.subset(d, 0, 0);
        assertEquals("subset should have one element", 1, e.length);
        assertEquals("values don't match", d[0], e[0], 0);
    }


    /**
     * Test subset() for subset second half of the array
     */
    public void testSubsetDoubleSecondHalf()
    {
        int len = 10;
        double[] d = new double[len];

        for (int i = 0; i < d.length; i++)
            d[i] = i;

        double[] e = ArrayUtil.subset(d, d.length / 2, d.length - 1);
        assertEquals("subset should be half size of original", d.length / 2, 
                     e.length);

        int ei = 0;

        for (int i = d.length / 2; i < d.length; i++)
        {
            assertEquals("values don't match", d[i], e[ei], 0);
            ei++;
        }
    }

    /**
     * Test subset(Object[]) for all
     */
    public void testSubsetObjectAll() 
    {
        String method = "[subObj] ";
        
        String[] objs = new String[] { "zero", "one", "two", "three" };
        
        String[] subset = (String[]) ArrayUtil.subset(objs, 1, 2);
        
        logger.info(method + ArrayUtil.toString(objs));
        logger.info(method + ArrayUtil.toString(subset));
        
        assertEquals("first index is incorrect", "one", subset[0]);
        assertEquals("second index is incorrect", "two", subset[1]);
    }


    /**
     * Test subset(Object[]) for empty array of objects
     */
    public void testSubsetObjectEmpty() 
    {
        String[] d = new String[0];
        String[] e = (String[]) ArrayUtil.subset(d, 0, 0);
        assertEquals("subset should be empty", 0, e.length);
    }


    /**
     * Test subset(Object[]) for array of length 1
     */
    public void testSubsetObjectOne() 
    {
        String[] d = new String[] { "a", "b", "c" };
        String[] e = (String[])ArrayUtil.subset(d, 0, 0);
        assertEquals("subset should have one element", 1, e.length);
        assertEquals("values don't match", "a", e[0]);
        
        e = (String[])ArrayUtil.subset(d, 1, 1);
        assertEquals("subset should have one element", 1, e.length);
        assertEquals("values don't match", "b", e[0]);
        
        e = (String[])ArrayUtil.subset(d, 2, 2);
        assertEquals("subset should have one element", 1, e.length);
        assertEquals("values don't match", "c", e[0]);
    }

        
    /**
     * Tests toString()
     */
    public void testToString()
    {
        String[] s = new String[]
        {
            "one", "two", "three", "four", "five", "six", "seven", "eight", 
            "nine", "ten"
        };
        
        logger.info("[toStrn] " + ArrayUtil.toString(s));
    }


    /**
     * Tests toString() for one element per line
     */
    public void testToStringOnePerLine()
    {
        String[] s = new String[]
        {
            "one", "two", "three", "four", "five", "six", "seven", "eight", 
            "nine", "ten"
        };
        
        logger.info("[toStrE] " + ArrayUtil.toString(s, true));
    }


    /**
     * Tests toString() for empty array
     */
    public void testToStringEmpty()
    {
        String[] s = new String[0];
        logger.info("[toStr1] " + ArrayUtil.toString(s));
    }
    
    /**
     * Tests indexOf() for an empty array 
     */
    public void testIndexOfEmpty() 
    {
        String strArray[] = new String[0];
        String s = "duke";
        
        int idx = ArrayUtil.indexOf(strArray, s);
        
        assertTrue("Array is empty", idx == -1);
    }

    /**
     * Tests indexOf() for an array of length 1
     */
    public void testIndexOfOne()
    {
        String   s = "duke";
        String[] strArray = new String[] { s };
        
        
        int idx = ArrayUtil.indexOf(strArray, s);
        
        assertEquals("Found at wrong index", 0, idx);
    }

    /**
     * Tests indexOf() for an array of length 1 where obj not found
     */
    public void testIndexOfOneNotFound()
    {
        String   s = "duke";
        String[] strArray = new String[] { "java" };
        
        
        int idx = ArrayUtil.indexOf(strArray, s);
        
        assertEquals("Should not have found a match", -1, idx);
    }

    /**
     * Tests indexOf() for an array of length > 1
     */
    public void testIndexOfMany()
    {
        String   two = "two";
        
        String[] strArray = 
            new String[] { "zero", "one", two, "three", "four" };
        
        int idx = ArrayUtil.indexOf(strArray, two);
        
        assertEquals("Found at wrong index", 2, idx);
    }

    /**
     * Tests indexOf() for an array of length > 1 where obj not found
     */
    public void testIndexOfManyNotFound()
    {
        String   notFound = "notFound";
        
        String[] strArray = 
            new String[] { "zero", "one", "two", "three", "four" };
        
        int idx = ArrayUtil.indexOf(strArray, notFound);
        
        assertEquals("Should not have found a match", -1, idx);
    }
    
}