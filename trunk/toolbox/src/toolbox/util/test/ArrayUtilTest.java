package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;

/**
 * ArrayUtil unit test class
 */
public class ArrayUtilTest extends TestCase
{
    /** Logger **/
    private static final Logger logger_ = 
        Logger.getLogger(ArrayUtilTest.class);

    /**
     * Starts the test case and runs the entire suite.
     *
     * @param args An array of unused command-line arguments
     */
    public static void main(String[] args)
    {
        TestRunner.run(ArrayUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * ArrayUtilTest constructor
     *  
     * @param aName String
     */
    public ArrayUtilTest(String aName)
    {
        super(aName);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Test subset() for subset equal to array
     */
    public void testSubsetDoubleAll() 
    {
        logger_.info("Running testSubsetDoubleAll...");
        
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
        logger_.info("Running testSubsetDoubleEmpty...");
        
        double[] d = new double[0];
        double[] e = ArrayUtil.subset(d, 0, 0);
        assertEquals("subset should be empty", 0, e.length);
    }


    /**
     * Test subset() for subset first half of array
     */
    public void testSubsetDoubleFirstHalf() 
    {
        logger_.info("Running testSubsetDoubleFirstHalf...");
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
        logger_.info("Running testSubsetDoubleOne...");
        
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
        logger_.info("Running testSubsetDoubleSecondHalf...");
        
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
        logger_.info("Running testSubsetObjectAll...");
        
        String method = "[subObj] ";
        
        String[] objs = new String[] { "zero", "one", "two", "three" };
        
        String[] subset = (String[]) ArrayUtil.subset(objs, 1, 2);
        
        logger_.info(method + ArrayUtil.toString(objs));
        logger_.info(method + ArrayUtil.toString(subset));
        
        assertEquals("first index is incorrect", "one", subset[0]);
        assertEquals("second index is incorrect", "two", subset[1]);
    }


    /**
     * Test subset(Object[]) for empty array of objects
     */
    public void testSubsetObjectEmpty() 
    {
        logger_.info("Running testSubsetObjectEmpty...");
        
        String[] d = new String[0];
        String[] e = (String[]) ArrayUtil.subset(d, 0, 0);
        assertEquals("subset should be empty", 0, e.length);
    }


    /**
     * Test subset(Object[]) for array of length 1
     */
    public void testSubsetObjectOne() 
    {
        logger_.info("Running testSubsetObjectOne...");
        
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
        logger_.info("Running testToString...");
        
        String[] s = new String[]
        {
            "one", "two", "three", "four", "five", "six", "seven", "eight", 
            "nine", "ten"
        };
        
        String expected = "[10]{one, two, three, four, five, six, seven, eight, nine, ten}";
        String result   = ArrayUtil.toString(s, false);
        logger_.info("[toStrn] " + result);
        assertEquals("strings don't match", expected, result);

    }


    /**
     * Tests toString() for empty array
     */
    public void testToStringEmpty()
    {
        logger_.info("Running testToStringEmpty...");
        
        String[] s = new String[0];
        logger_.info("[sempty] " + ArrayUtil.toString(s));
    }
    

    /**
     * Tests toString() for single element array with one per line = true
     */
    public void testToStringOneElementOnePerLine()
    {
        logger_.info("Running testToStringOneElementOnePerLine...");
        
        String[] s = new String[] { "hello"};
        logger_.info("[oneelm] " + ArrayUtil.toString(s, true));
        logger_.info("[oneelm] " + ArrayUtil.toString(s, false));        
    }


    /**
     * Tests toString() for one element per line
     */
    public void testToStringOnePerLine()
    {
        logger_.info("Running testToStringOnePerLine...");
        
        String[] s = new String[]
        {
            "one", "two", "three", "four", "five", "six", "seven", "eight", 
            "nine", "ten"
        };
        
        String result   = ArrayUtil.toString(s, true);
        logger_.info("[oneper]\n " + result);
    }

    
    /**
     * Tests indexOf() for an empty array 
     */
    public void testIndexOfEmpty() 
    {
        logger_.info("Running testIndexOfEmpty...");
        
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
        logger_.info("Running testIndexOfOne...");
        
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
        logger_.info("Running testIndexOfOneNotFound...");
        
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
        logger_.info("Running testIndexOfMany...");
        
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
        logger_.info("Running testIndexOfManyNotFound...");
        
        String   notFound = "notFound";
        
        String[] strArray = 
            new String[] { "zero", "one", "two", "three", "four" };
        
        int idx = ArrayUtil.indexOf(strArray, notFound);
        
        assertEquals("Should not have found a match", -1, idx);
    }
    
    
    /**
     * Tests contains() for an empty array
     */
    public void testContainsEmpty()
    {
        logger_.info("Running testContainsEmpty...");
        
        assertTrue("Should not be found in an empty array",
            !ArrayUtil.contains(new String[0], "blah"));
    }
 
    
    /**
     * Tests contains() for object not found in an array of size one
     */
    public void testContainsOneNotFound()
    {
        logger_.info("Running testContainsOneNotFound...");
        
        assertTrue("Should not be found in an array of size one", 
            !ArrayUtil.contains(new String[] {"this"}, "dont match"));
    }
    
    
    /**
     * Tests contains() for object not found in an array of size > one
     */
    public void testContainsManyNotFound()
    {
        logger_.info("Running testContainsManyNotFound...");
        
        assertTrue("Should not be found in an array with size > one", 
            !ArrayUtil.contains(new String[] {"one", "two", "three" }, "zero"));
    }    
    
    
    /**
     * Tests contains() for object found in an array of size one
     */
    public void testContainsOne()
    {
        logger_.info("Running testContainsOne...");
        
        assertTrue("Should have found in an array of size one", 
            ArrayUtil.contains(new String[] {"this"}, "this"));
    }
    
    
    /**
     * Tests contains() for object found in an array of size > one
     */
    public void testContainsMany()
    {
        logger_.info("Running testContainsMany...");
        
        assertTrue("Should have found in an array with size > one", 
            ArrayUtil.contains(new String[] {"one", "two", "three" }, "two"));
    }    
    
    
    /**
     * Tests the isNullOrEmpty() method
     */
    public void testIsNullOrEmpty()
    {
        logger_.info("Running testIsNullOrEmpty...");
        
        String[] nullArray  = null;
        String[] emptyArray = new String[0];
        String[] oneArray   = new String[] { "zero" };
        
        assertTrue("Should have returned true for null array", 
            ArrayUtil.isNullOrEmpty(nullArray));
            
        assertTrue("Should have returned true for empty array",
            ArrayUtil.isNullOrEmpty(emptyArray));
            
        assertTrue("Should have returned false for non-empty array", 
            !ArrayUtil.isNullOrEmpty(oneArray));            
    }
    

    /*
     * concat(head, tail) test cases:
     * 
     * head and tail are arrays of String[]
     * 
     * empty = array is empty
     * one   = array contains one element
     * many  = array contains > 1 elements
     * 
     * head     tail
     * ==============
     * empty    empty
     * empty    one
     * empty    many
     * one      empty
     * many     empty
     * one      one
     * one      many
     * many     one
     * many     many
     */

    
    /**
     * Tests concat() for two empty arrays
     */
    public void testConcatBothEmpty()
    {
        logger_.info("Running testConcatBothEmpty...");
        
        String[] head = new String[0];
        String[] tail = new String[0];
        
        String[] concatted = (String[])ArrayUtil.concat(head, tail);
        
        assertEquals("concatted array should be empty", 0, concatted.length);
        assertEquals("concatted array class type should string", String.class,
            concatted.getClass().getComponentType());
    }
    
    
    /**
     * Tests merge() for an empty head and a tail containing one element 
     */
    public void testConcatEmptyOne()
    {
        logger_.info("Running testConcatEmptyOne...");
        
        String[] head = new String[0];
        String[] tail = new String[] { "one" };
        
        String[] concatted = (String[])ArrayUtil.concat(head, tail);
        
        assertEquals("concatted array length incorrect", 1, concatted.length);
        assertEquals("concatted array class type should string", String.class,
            concatted.getClass().getComponentType());
        assertEquals("concatted array element incorrect", "one", concatted[0]);
    }


    /**
     * Tests concat() for an empty head and a tail containing many elements
     */
    public void testConcatEmptyMany()
    {
        logger_.info("Running testConcatEmptyMany...");
        
        String[] head = new String[0];
        String[] tail = new String[] { "one", "two", "three", "four" };
        
        String[] concatted = (String[])ArrayUtil.concat(head, tail);
        
        assertEquals("concatted array len incorrect", tail.length, 
            concatted.length);
            
        assertEquals("concatted array class type should string", String.class,
            concatted.getClass().getComponentType());
        
        for (int i=0; i<tail.length; i++)
            assertEquals("concatted array contents incorrect", tail[i], 
                concatted[i]);
    }


    /**
     * Tests concat() for an a head containing one element and an empty tail
     */
    public void testConcatOneEmpty()
    {
        logger_.info("Running testConcatOneEmpty...");
        
        String[] tail = new String[0];
        String[] head = new String[] { "one" };
        
        String[] concatted = (String[])ArrayUtil.concat(head, tail);
        
        assertEquals("concatted array length incorrect", 1, concatted.length);
        assertEquals("concatted array class type should string", String.class,
            concatted.getClass().getComponentType());
        assertEquals("concatted array element incorrect", "one", concatted[0]);
    }


    /**
     * Tests merge() for an head containing many elements and an empty tail
     */
    public void testConcatManyEmpty()
    {
        logger_.info("Running testConcatManyEmpty...");
        
        String[] tail = new String[0];
        String[] head = new String[] { "one", "two", "three", "four" };
        
        String[] concatted = (String[])ArrayUtil.concat(head, tail);
        
        assertEquals("concatted array len incorrect", head.length, 
            concatted.length);
            
        assertEquals("concatted array class type should string", String.class,
            concatted.getClass().getComponentType());
        
        for (int i=0; i<head.length; i++)
            assertEquals("concatted array contents incorrect", head[i], 
                concatted[i]);
    }
 
 
    /**
     * Tests concat() for a head and tail each containing one element 
     */
    public void testConcatBothOne()
    {
        logger_.info("Running testConcatBothOne...");
        
        String[] head = new String[] { "one" };
        String[] tail = new String[] { "two" };
        
        String[] concatted = (String[])ArrayUtil.concat(head, tail);
        
        assertEquals("concatted array length incorrect", 2, concatted.length);
        assertEquals("concatted array class type should string", String.class,
            concatted.getClass().getComponentType());
        assertEquals("concatted array element incorrect", "one", concatted[0]);
        assertEquals("concatted array element incorrect", "two", concatted[1]);
    }


    /**
     * Tests concat() for an head containing many elements and tail containing
     * one element
     */
    public void testConcatManyOne()
    {
        logger_.info("Running testConcatManyOne...");
        
        String[] head = new String[] { "one", "two", "three", "four" };
        String[] tail = new String[] { "five" };
        
        String[] concatted = (String[])ArrayUtil.concat(head, tail);
        
        assertEquals("concatted array len incorrect", head.length + tail.length, 
            concatted.length);
            
        assertEquals("concatted array class type should string", String.class,
            concatted.getClass().getComponentType());
        
        int i;
        for (i=0; i<head.length; i++)
            assertEquals("concatted array contents incorrect", head[i], 
                concatted[i]);
                
        assertEquals("concatted array contents incorrect", tail[0], 
            concatted[i]);
    }


    /**
     * Tests concat() for an tail containing many elements and head containing
     * one element
     */
    public void testConcatOneMany()
    {
        logger_.info("Running testConcatOneMany...");
        
        String[] tail = new String[] { "one", "two", "three", "four" };
        String[] head = new String[] { "five" };
        
        String[] concatted = (String[])ArrayUtil.concat(head, tail);
        
        assertEquals("concatted array len incorrect", head.length + tail.length, 
            concatted.length);
            
        assertEquals("concatted array class type should string", String.class,
            concatted.getClass().getComponentType());
        
        assertEquals("concatted array contents incorrect", head[0], 
            concatted[0]);
            
        for (int i=0; i<tail.length; i++)
            assertEquals("concatted array contents incorrect", tail[i], 
                concatted[i+head.length]);

    }


    /**
     * Tests concat() for both head and tail containing many elements
     */
    public void testConcatBothMany()
    {
        logger_.info("Running testConcatBothMany...");
        
        String[] head = new String[] { "one", "two", "three", "four" };
        String[] tail = new String[] { "five", "six", "seven", "eight" };
        
        String[] concatted = (String[])ArrayUtil.concat(head, tail);
        
        assertEquals("concatted array len incorrect", head.length + tail.length, 
            concatted.length);
            
        assertEquals("concatted array class type should string", String.class,
            concatted.getClass().getComponentType());
        
        for (int i=0; i<head.length; i++)
            assertEquals("concatted array contents incorrect", head[i], 
                concatted[i]);

        for (int i=0; i<tail.length; i++)
            assertEquals("concatted array contents incorrect", tail[i], 
                concatted[i+head.length]);
    }    
}