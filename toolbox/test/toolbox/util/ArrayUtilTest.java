package toolbox.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.util.ArrayUtil}.
 */
public class ArrayUtilTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(ArrayUtilTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(ArrayUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests: subset(double[])
    //--------------------------------------------------------------------------
    
    /**
     * Test subset() for subset equal to array.
     */
    public void testSubsetByteAll() 
    {
        logger_.info("Running testSubsetByteAll...");
        
        int len = 10;
        byte[] d = new byte[len];

        for (int i = 0; i < d.length; i++)
            d[i] = (byte) RandomUtils.nextInt(255);

        byte[] e = ArrayUtil.subset(d, 0, d.length - 1);
        assertEquals("subset should be same size as original", d.length, 
                e.length);

        for (int i = 0; i < d.length; i++)
            assertEquals("values don't match", d[i], e[i], 0);
    }

    
    /**
     * Test subset() for empty array.
     */
    public void testSubsetByteEmpty() 
    {
        logger_.info("Running testSubsetByteEmpty...");
        
        byte[] d = new byte[0];
        byte[] e = ArrayUtil.subset(d, 0, 0);
        assertEquals("subset should be empty", 0, e.length);
    }

    
    /**
     * Test subset() for subset first half of array.
     */
    public void testSubsetByteFirstHalf() 
    {
        logger_.info("Running testSubsetByteFirstHalf...");
        int len = 10;
        byte[] d = new byte[len];

        for (int i = 0; i < d.length; i++)
            d[i] = (byte) RandomUtils.nextInt(255);

        byte[] e = ArrayUtil.subset(d, 0, (d.length / 2) - 1);
        assertEquals("subset should be half size of original", d.length / 2, 
                e.length);

        for (int i = 0; i < e.length; i++)
            assertEquals("values don't match", d[i], e[i], 0);
    }

    
    /**
     * Test subset() for array of length 1.
     */
    public void testSubsetByteOne() 
    {
        logger_.info("Running testSubsetByteOne...");
        
        byte[] d = new byte[]{99};
        byte[] e = ArrayUtil.subset(d, 0, 0);
        assertEquals("subset should have one element", 1, e.length);
        assertEquals("values don't match", d[0], e[0], 0);
    }

    
    /**
     * Test subset() for subset second half of the array.
     */
    public void testSubsetByteSecondHalf()
    {
        logger_.info("Running testSubsetByteSecondHalf...");
        
        int len = 10;
        byte[] d = new byte[len];

        for (int i = 0; i < d.length; i++)
            d[i] = (byte) RandomUtils.nextInt(255);

        byte[] e = ArrayUtil.subset(d, d.length / 2, d.length - 1);
        assertEquals("subset should be half size of original", d.length / 2, 
                e.length);

        int ei = 0;

        for (int i = d.length / 2; i < d.length; i++)
        {
            assertEquals("values don't match", d[i], e[ei], 0);
            ei++;
        }
    }

    //--------------------------------------------------------------------------
    // Unit Tests: subset(double[])
    //--------------------------------------------------------------------------
    
    /**
     * Test subset() for subset equal to array.
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
     * Test subset() for empty array.
     */
    public void testSubsetDoubleEmpty() 
    {
        logger_.info("Running testSubsetDoubleEmpty...");
        
        double[] d = new double[0];
        double[] e = ArrayUtil.subset(d, 0, 0);
        assertEquals("subset should be empty", 0, e.length);
    }

    
    /**
     * Test subset() for subset first half of array.
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
     * Test subset() for array of length 1.
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
     * Test subset() for subset second half of the array.
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

    //--------------------------------------------------------------------------
    // Unit Tests: subset(Object[])
    //--------------------------------------------------------------------------
    
    /**
     * Test subset(Object[]) for all.
     */
    public void testSubsetObjectAll() 
    {
        logger_.info("Running testSubsetObjectAll...");
        
        String[] objs = new String[] {"zero", "one", "two", "three"};
        String[] subset = (String[]) ArrayUtil.subset(objs, 1, 2);
        
        logger_.debug(ArrayUtil.toString(objs));
        logger_.debug(ArrayUtil.toString(subset));
        
        assertEquals("first index is incorrect", "one", subset[0]);
        assertEquals("second index is incorrect", "two", subset[1]);
    }

    
    /**
     * Test subset(Object[]) for empty array of objects.
     */
    public void testSubsetObjectEmpty() 
    {
        logger_.info("Running testSubsetObjectEmpty...");
        
        String[] d = new String[0];
        String[] e = (String[]) ArrayUtil.subset(d, 0, 0);
        assertEquals("subset should be empty", 0, e.length);
    }

    
    /**
     * Test subset(Object[]) for array of length 1.
     */
    public void testSubsetObjectOne() 
    {
        logger_.info("Running testSubsetObjectOne...");
        
        String[] d = new String[] {"a", "b", "c"};
        String[] e = (String[]) ArrayUtil.subset(d, 0, 0);
        assertEquals("subset should have one element", 1, e.length);
        assertEquals("values don't match", "a", e[0]);
        
        e = (String[]) ArrayUtil.subset(d, 1, 1);
        assertEquals("subset should have one element", 1, e.length);
        assertEquals("values don't match", "b", e[0]);
        
        e = (String[]) ArrayUtil.subset(d, 2, 2);
        assertEquals("subset should have one element", 1, e.length);
        assertEquals("values don't match", "c", e[0]);
    }

    
    /**
     * Tests subset(Object,int,int) for boundary conditions.
     */
    public void testSubsetOutOfBounds()
    {
        logger_.info("Running testSubetOutOfBounds...");
        
        String[] a = new String[] {"zero", "one", "two"};
        
        // Subset bounds which are invalid
        int[][] bounds = new int[][] 
        { 
            {-1, -1 },
            {-1,  0  },
            {-1,  1  },
            {-1,  2  },
            {-1,  3  },
            {0,  3  },
            {1,  3  },
            {2,  3  }, 
            {3,  3  }     
        };
        
        // Run each set of bounds through and make sure it fails.
        for (int i = 0; i < bounds.length; i++)
        {
            try
            {
                int lower = bounds[i][0];
                int upper = bounds[i][1];
                ArrayUtil.subset(a, lower, upper);
                
                fail("Bounds (" + lower + ", " + upper + 
                    ") should have failed");
            }
            catch (IllegalArgumentException iae)
            {
                // Passed
                logger_.debug("Error message: " + iae.getMessage());
            }
        }
    }

    //--------------------------------------------------------------------------
    // Unit Tests: toString()
    //--------------------------------------------------------------------------
        
    public void testToString()
    {
        String[] s = new String[]
        {
            "one", "two", "three", "four", "five", "six", "seven", "eight", 
            "nine", "ten"
        };
        
        String expected = 
            "[10]{one, two, three, four, five, six, seven, eight, nine, ten}";
            
        String result   = ArrayUtil.toString(s, false);
        logger_.debug(result);
        assertEquals("strings don't match", expected, result);

    }
    
    public void testToStringEmpty()
    {
        logger_.info("Running testToStringEmpty...");
        
        String[] s = new String[0];
        logger_.debug(ArrayUtil.toString(s));
    }
    
    public void testToString_ManyElements_OneLine() 
    {
        String[] s = new String[] { "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten" };
        String expected = "[10]{one, two, three, four, five, six, seven, eight, nine, ten}";
        String result = ArrayUtil.toString(s, false);
        System.out.println(result);
        assertEquals("strings don't match", expected, result);
    }

    public void testToString_ZeroElements() 
    {
        String[] s = new String[0];
        assertEquals("[0]{}", ArrayUtil.toString(s, false));
    }

    public void testToString_OneElement() 
    {
        String[] s = new String[] { "blah" };
        assertEquals("[1]{blah}", ArrayUtil.toString(s, false));
    }

    public void testToString_OneElementOnePerLine() 
    {
        String[] s = new String[] { "hello" };
        assertEquals("[1]{hello}", ArrayUtil.toString(s, true));
        assertEquals("[1]{hello}", ArrayUtil.toString(s, false));
    }

    public void testToString_ManyElementsOnePerLine() 
    {
        String[] s = new String[] { "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten" };

        String expected = "[10]{\n" +
            "one, \n" + 
            "two, \n" + 
            "three, \n" + 
            "four, \n" + 
            "five, \n" + 
            "six, \n" + 
            "seven, \n" + 
            "eight, \n" + 
            "nine, \n" + 
            "ten}";
        
        String result = ArrayUtil.toString(s, true);
        assertEquals(expected, result);
    }    
    
    //--------------------------------------------------------------------------
    // Unit Tests: indexOf()
    //--------------------------------------------------------------------------
    
    /**
     * Tests indexOf() for an empty array.
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
     * Tests indexOf() for an array of length 1.
     */
    public void testIndexOfOne()
    {
        logger_.info("Running testIndexOfOne...");
        
        String   s = "duke";
        String[] strArray = new String[] {s};
        
        
        int idx = ArrayUtil.indexOf(strArray, s);
        
        assertEquals("Found at wrong index", 0, idx);
    }

    
    /**
     * Tests indexOf() for an array of length 1 where obj not found.
     */
    public void testIndexOfOneNotFound()
    {
        logger_.info("Running testIndexOfOneNotFound...");
        
        String   s = "duke";
        String[] strArray = new String[] {"java"};
        
        
        int idx = ArrayUtil.indexOf(strArray, s);
        
        assertEquals("Should not have found a match", -1, idx);
    }

    
    /**
     * Tests indexOf() for an array of length > 1.
     */
    public void testIndexOfMany()
    {
        logger_.info("Running testIndexOfMany...");
        
        String   two = "two";
        
        String[] strArray = 
            new String[] {"zero", "one", two, "three", "four"};
        
        int idx = ArrayUtil.indexOf(strArray, two);
        
        assertEquals("Found at wrong index", 2, idx);
    }

    
    /**
     * Tests indexOf() for an array of length > 1 where obj not found.
     */
    public void testIndexOfManyNotFound()
    {
        logger_.info("Running testIndexOfManyNotFound...");
        
        String   notFound = "notFound";
        
        String[] strArray = 
            new String[] {"zero", "one", "two", "three", "four"};
        
        int idx = ArrayUtil.indexOf(strArray, notFound);
        
        assertEquals("Should not have found a match", -1, idx);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests: contains()
    //--------------------------------------------------------------------------
    
    /**
     * Tests contains() for an empty array.
     */
    public void testContainsEmpty()
    {
        logger_.info("Running testContainsEmpty...");
        
        assertTrue("Should not be found in an empty array",
            !ArrayUtil.contains(new String[0], "blah"));
    }
    
    
    /**
     * Tests contains() for object not found in an array of size one.
     */
    public void testContainsOneNotFound()
    {
        logger_.info("Running testContainsOneNotFound...");
        
        assertTrue("Should not be found in an array of size one", 
            !ArrayUtil.contains(new String[] {"this"}, "dont match"));
    }
    
    
    /**
     * Tests contains() for object not found in an array of size > one.
     */
    public void testContainsManyNotFound()
    {
        logger_.info("Running testContainsManyNotFound...");
        
        assertTrue("Should not be found in an array with size > one", 
            !ArrayUtil.contains(new String[] {"one", "two", "three" }, "zero"));
    }    
    
    
    /**
     * Tests contains() for object found in an array of size one.
     */
    public void testContainsOne()
    {
        logger_.info("Running testContainsOne...");
        
        assertTrue("Should have found in an array of size one", 
            ArrayUtil.contains(new String[] {"this"}, "this"));
    }
    
    
    /**
     * Tests contains() for object found in an array of size > one.
     */
    public void testContainsMany()
    {
        logger_.info("Running testContainsMany...");
        
        assertTrue("Should have found in an array with size > one", 
            ArrayUtil.contains(new String[] {"one", "two", "three" }, "two"));
    }    

    //--------------------------------------------------------------------------
    // Unit Tests: concat()
    //--------------------------------------------------------------------------

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
     * Tests concat() for two empty arrays.
     */
    public void testConcatBothEmpty()
    {
        logger_.info("Running testConcatBothEmpty...");
        
        String[] head = new String[0];
        String[] tail = new String[0];
        
        String[] concatted = (String[]) ArrayUtil.concat(head, tail);
        
        assertEquals("concatted array should be empty", 0, concatted.length);
        assertEquals("concatted array class type should string", String.class,
            concatted.getClass().getComponentType());
    }
    
    
    /**
     * Tests concat() for two arrays, one of which is empty.
     */
    public void testConcatEmptyOne()
    {
        logger_.info("Running testConcatEmptyOne...");
        
        String[] head = new String[0];
        String[] tail = new String[] {"one"};
        
        String[] concatted = (String[]) ArrayUtil.concat(head, tail);
        
        assertEquals("concatted array length incorrect", 1, concatted.length);
        assertEquals("concatted array class type should string", String.class,
            concatted.getClass().getComponentType());
        assertEquals("concatted array element incorrect", "one", concatted[0]);
    }

    
    /**
     * Tests concat() for an empty head and a tail containing many elements.
     */
    public void testConcatEmptyMany()
    {
        logger_.info("Running testConcatEmptyMany...");
        
        String[] head = new String[0];
        String[] tail = new String[] {"one", "two", "three", "four"};
        
        String[] concatted = (String[]) ArrayUtil.concat(head, tail);
        
        assertEquals("concatted array len incorrect", tail.length, 
            concatted.length);
            
        assertEquals("concatted array class type should string", String.class,
            concatted.getClass().getComponentType());
        
        for (int i = 0; i < tail.length; i++)
            assertEquals("concatted array contents incorrect", tail[i], 
                concatted[i]);
    }

    
    /**
     * Tests concat() for an a head containing one element and an empty tail.
     */
    public void testConcatOneEmpty()
    {
        logger_.info("Running testConcatOneEmpty...");
        
        String[] tail = new String[0];
        String[] head = new String[] {"one"};
        
        String[] concatted = (String[]) ArrayUtil.concat(head, tail);
        
        assertEquals("concatted array length incorrect", 1, concatted.length);
        assertEquals("concatted array class type should string", String.class,
            concatted.getClass().getComponentType());
        assertEquals("concatted array element incorrect", "one", concatted[0]);
    }

    
    /**
     * Tests concat() for an head containing many elements and an empty tail.
     */
    public void testConcatManyEmpty()
    {
        logger_.info("Running testConcatManyEmpty...");
        
        String[] tail = new String[0];
        String[] head = new String[] {"one", "two", "three", "four"};
        
        String[] concatted = (String[]) ArrayUtil.concat(head, tail);
        
        assertEquals("concatted array len incorrect", head.length, 
            concatted.length);
            
        assertEquals("concatted array class type should string", String.class,
            concatted.getClass().getComponentType());
        
        for (int i = 0; i < head.length; i++)
            assertEquals("concatted array contents incorrect", head[i], 
                concatted[i]);
    }
 
    
    /**
     * Tests concat() for a head and tail each containing one element. 
     */
    public void testConcatBothOne()
    {
        logger_.info("Running testConcatBothOne...");
        
        String[] head = new String[] {"one"};
        String[] tail = new String[] {"two"};
        
        String[] concatted = (String[]) ArrayUtil.concat(head, tail);
        
        assertEquals("concatted array length incorrect", 2, concatted.length);
        assertEquals("concatted array class type should string", String.class,
            concatted.getClass().getComponentType());
        assertEquals("concatted array element incorrect", "one", concatted[0]);
        assertEquals("concatted array element incorrect", "two", concatted[1]);
    }

    
    /**
     * Tests concat() for an head containing many elements and tail containing
     * one element.
     */
    public void testConcatManyOne()
    {
        logger_.info("Running testConcatManyOne...");
        
        String[] head = new String[] {"one", "two", "three", "four"};
        String[] tail = new String[] {"five"};
        
        String[] concatted = (String[]) ArrayUtil.concat(head, tail);
        
        assertEquals("concatted array len incorrect", head.length + tail.length,
            concatted.length);
            
        assertEquals("concatted array class type should string", String.class,
            concatted.getClass().getComponentType());
        
        int i;
        for (i = 0; i < head.length; i++)
            assertEquals("concatted array contents incorrect", head[i], 
                concatted[i]);
                
        assertEquals("concatted array contents incorrect", tail[0], 
            concatted[i]);
    }

    
    /**
     * Tests concat() for an tail containing many elements and head containing
     * one element.
     */
    public void testConcatOneMany()
    {
        logger_.info("Running testConcatOneMany...");
        
        String[] tail = new String[] {"one", "two", "three", "four"};
        String[] head = new String[] {"five"};
        
        String[] concatted = (String[]) ArrayUtil.concat(head, tail);
        
        assertEquals("concatted array len incorrect", head.length + tail.length,
            concatted.length);
            
        assertEquals("concatted array class type should string", String.class,
            concatted.getClass().getComponentType());
        
        assertEquals("concatted array contents incorrect", head[0], 
            concatted[0]);
            
        for (int i = 0; i < tail.length; i++)
            assertEquals("concatted array contents incorrect", tail[i], 
                concatted[i + head.length]);

    }

    
    /**
     * Tests concat() for both head and tail containing many elements.
     */
    public void testConcatBothMany()
    {
        logger_.info("Running testConcatBothMany...");
        
        String[] head = new String[] {"one", "two", "three", "four"};
        String[] tail = new String[] {"five", "six", "seven", "eight"};
        
        String[] concatted = (String[]) ArrayUtil.concat(head, tail);
        
        assertEquals("concatted array len incorrect", head.length + tail.length,
            concatted.length);
            
        assertEquals("concatted array class type should string", String.class,
            concatted.getClass().getComponentType());
        
        for (int i = 0; i < head.length; i++)
            assertEquals(
                "concatted array contents incorrect",
                head[i],
                concatted[i]);

        for (int i = 0; i < tail.length; i++)
            assertEquals(
                "concatted array contents incorrect",
                tail[i],
                concatted[i + head.length]);
    }   

    //--------------------------------------------------------------------------
    // Unit Tests: insert() / insertAt()
    //--------------------------------------------------------------------------

    /**
     * Tests insert() for inserting an object into an empty array.
     */
    public void testInsertToEmptyArray()
    {
        logger_.info("Running testInsertToEmptyArray...");
        
        String[] arr = new String[0];
        String obj = "foo";

        String[] result = (String[]) ArrayUtil.insert(arr, obj);
            
        assertEquals(1, result.length);
        assertEquals(obj, result[0]);    
    }
    
    
    /**
     * Tests insert() for adding an object to a non-empty array.
     */
    public void testInsertToArray()
    {
        logger_.info("Running testInsertToArray...");
        
        String[] arr = new String[] {"one", "two", "three"};
        String zero = "zero";        
        String[] expected = new String[] {"zero", "one", "two", "three"};


        String[] result = (String[]) ArrayUtil.insert(arr, zero);
            
        assertEquals(arr.length + 1, result.length);
        assertTrue(ArrayUtil.equals(expected, result));    
    }
    
    
    /**
     * Tests insertAt() for inserting an object into an empty array.
     */
    public void testInsertAtToEmptyArray()
    {
        logger_.info("Running testInsertAtToEmptyArray...");
        
        String[] arr = new String[0];
        String obj = "foo";

        String[] result = (String[]) ArrayUtil.insertAt(arr, obj, 0);
            
        assertEquals(1, result.length);
        assertEquals(obj, result[0]);    
    }
    
    
    /**
     * Tests insertAt() for adding an object to the front of a non-empty array.
     */
    public void testInsertAtFront()
    {
        logger_.info("Running testInsertAtFront...");
        
        String[] arr = new String[] {"one", "two", "three"};
        String zero = "zero";        
        String[] expected = new String[] {"zero", "one", "two", "three"};

        String[] result = (String[]) ArrayUtil.insertAt(arr, zero, 0);
            
        assertEquals(arr.length + 1, result.length);
        assertTrue(ArrayUtil.equals(expected, result));    
    }
    
    
    /**
     * Tests insertAt() for adding an object to the end of a non-empty array.
     */
    public void testInsertAtBack()
    {
        logger_.info("Running testInsertAtBack...");
        
        String[] arr = new String[] {"one", "two", "three"};
        String four = "four";        
        String[] expected = new String[] {"one", "two", "three", "four"};

        String[] result = (String[]) ArrayUtil.insertAt(arr, four, 3);

        assertEquals(arr.length + 1, result.length);
        assertTrue(ArrayUtil.equals(expected, result));    
    }

    
    /**
     * Tests insertAt() for adding an object to the middle of a non-empty array.
     */
    public void testInsertAtMiddle()
    {
        logger_.info("Running testInsertAtMiddle...");
        
        String[] arr = new String[] {"one", "three"};
        String two = "two";        
        String[] expected = new String[] {"one", "two", "three"};

        String[] result = (String[]) ArrayUtil.insertAt(arr, two, 1);
            
        assertEquals(arr.length + 1, result.length);
        assertTrue(ArrayUtil.equals(expected, result));    
    }

    //--------------------------------------------------------------------------
    // Unit Tests: remove()
    //--------------------------------------------------------------------------

    /**
     * Tests remove() for removing from an empty array.
     */
    public void testRemoveEmpty()
    {
        logger_.info("Running testRemoveEmpty...");
        
        String[] strArray = new String[0];
        String   s = "duke";
    
        Object[] result = ArrayUtil.remove(strArray, s);
    
        assertEquals("Array should be empty empty", 0, result.length);
        assertTrue(result.getClass().getComponentType() == String.class);
    }

    
    /**
     * Tests remove() for removing the last element from an array.
     */
    public void testRemoveOne()
    {
        logger_.info("Running testRemoveOne...");
        
        String   s = "duke";
        String[] strArray = new String[] {s};
        
        String[] result = (String[]) ArrayUtil.remove(strArray, s);
        
        assertEquals(0, result.length);
        assertTrue(result.getClass().getComponentType() == String.class);
    }
    
    
    /**
     * Tests remove() for removing the first element in an array of many.
     */
    public void testRemoveHead()
    {
        logger_.info("Running testRemoveHead...");
        
        String s = "head";
        String[] strArray = new String[] {s, "one", "two", "three", "tail"};
        
        String[] result = (String[]) ArrayUtil.remove(strArray, s);
        
        assertEquals(strArray.length - 1, result.length);
        assertTrue(result.getClass().getComponentType() == String.class);
        
        for (int i = 0; i < strArray.length - 1; i++)
            assertEquals(strArray[i + 1], result[i]);
    }
    
    
    /**
     * Tests remove() for removing the last element in an array of many.
     */
    public void testRemoveTail()
    {
        logger_.info("Running testRemoveTail...");
        
        String tail = "tail";
        String[] strArray = new String[] {"head", "one", "two", "three", tail};
        
        String[] result = (String[]) ArrayUtil.remove(strArray, tail);
        
        assertEquals(strArray.length - 1, result.length);
        assertTrue(result.getClass().getComponentType() == String.class);
        
        for (int i = 0; i < strArray.length - 1; i++)
            assertEquals(strArray[i], result[i]);
    }

    
    /**
     * Tests remove() for removing the middle element.
     */
    public void testRemoveMiddle()
    {
        logger_.info("Running testRemoveHead...");
        
        String middle = "middle";
        String[] strArray = new String[] {"head", middle, "tail"};
        
        String[] result = (String[]) ArrayUtil.remove(strArray, middle);
        
        assertEquals(strArray.length - 1, result.length);
        assertTrue(result.getClass().getComponentType() == String.class);
        assertEquals("head", result[0]);
        assertEquals("tail", result[1]);
    }

    
    /**
     * Tests remove() for removing a buncha random elements.
     */
    public void testRemoveRandom()
    {
        logger_.info("Running testRemoveHead...");

        List keys = new ArrayList();
        
        for (int i = 0; i < 100; i++)
            keys.add("key" + i);
                
        String[] data = (String[]) keys.toArray(new String[0]);
        
        while (!keys.isEmpty())
        {
            int i = RandomUtil.nextInt(0, keys.size() - 1);
            data = (String[]) ArrayUtil.remove(data, keys.get(i));
            keys.remove(i);            
        }
        
        assertEquals(0, data.length);
    }

    //--------------------------------------------------------------------------
    // Remaining Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests init(double)
     */
    public void testInitDouble()
    {
        logger_.info("Running testInitDouble...");
                
        double[] d = new double[10];
        ArrayUtil.init(d, 99.9);
            
        for (int i = 0; i < d.length; assertEquals(99.9d, d[i++], 0.0));
    }
    
    
    /**
     * Tests init(int)
     */
    public void testInitInt()
    {
        logger_.info("Running testInitInt...");
                
        int[] d = new int[10];
        ArrayUtil.init(d, 99);
        
        for (int i = 0; i < d.length; assertEquals(99, d[i++]));
    }
    
    
    /**
     * Tests toString(int[])
     */
    public void testToStringIntArray()
    {
        logger_.info("Running testToStringIntArray...");
        
        int[] i = new int[] {1, 2, 3, 4, 5};
        logger_.debug(ArrayUtil.toString(i));        
    }
    

    /**
     * Tests toString(double[])
     */
    public void testToStringDoubleArray()
    {
        logger_.info("Running testToStringDoubleArray...");
        
        double[] d = new double[] {1.1, 2.2, 3.3, 4.4, 5.5};
        logger_.debug(ArrayUtil.toString(d));        
    }

    
    /**
     * Tests add() for adding an object to an empty array.
     */
    public void testAddToEmptyArray()
    {
        logger_.info("Running testAddToEmptyArray...");
        
        String[] arr = new String[0];
        String obj = "foo";

        String[] result = (String[]) ArrayUtil.add(arr, obj);
            
        assertEquals(1, result.length);
        assertEquals(obj, result[0]);    
    }
    
    
    /**
     * Tests add() for adding an object to a non-empty array.
     */
    public void testAddToArray()
    {
        logger_.debug("Running testAddToArray...");
        
        String[] arr = new String[] {"one", "two", "three"};
        String four = "four";        
        String[] expected = new String[] {"one", "two", "three", four};

        String[] result = (String[]) ArrayUtil.add(arr, four);
            
        assertEquals(arr.length + 1, result.length);
        assertTrue(ArrayUtil.equals(expected, result));    
    }
    
    
    /**
     * Tests equals() for by array reference and by array's contents reference
     * and value.
     */
    public void testEquals()
    {
        logger_.info("Running testEquals...");
        
        String[] a = new String[] {"a", "b", "c"};
        String[] b = new String[] {"1", "2"};
        String[] c = new String[] {"alpha", "beta", "gamma"};
        
        Object x1 = new Integer(1);
        Object x2 = new Boolean(false);
        Object x3 = new Character('x');
        
        Object[] d1 = new Object[] {x1, x2, x3};
        Object[] d2 = new Object[] {x1, x2, x3};
        
        Object y1 = new Integer(1);
        Object y2 = new Boolean(false);
        Object y3 = new Character('x');
        
        Object[] e1 = new Object[] {x1, x2, x3};
        Object[] e2 = new Object[] {y1, y2, y3};
                 
        // Equal by reference: a == a
        assertTrue(ArrayUtil.equals(a, a));
        
        // Not equal by length: a.length != b.length
        assertFalse(ArrayUtil.equals(a, b));
        
        // Not equals by content: a[i] != c[i] or !a[i].equals(c[i])
        assertFalse(ArrayUtil.equals(a, c));
        
        // Equal by contents' reference: d1[i] == d2[i]
        assertTrue(ArrayUtil.equals(d1, d2));
        
        // Equal by contents' values: e1[i].equals(e2[i])
        assertTrue(ArrayUtil.equals(e1, e2));        
    }
    
    
    /**
     * Tests the isNullOrEmpty() method.
     */
    public void testIsNullOrEmpty()
    {
        logger_.info("Running testIsNullOrEmpty...");
        
        String[] nullArray  = null;
        String[] emptyArray = new String[0];
        String[] oneArray   = new String[] {"zero"};
        
        assertTrue("Should have returned true for null array", 
            ArrayUtil.isNullOrEmpty(nullArray));
            
        assertTrue("Should have returned true for empty array",
            ArrayUtil.isNullOrEmpty(emptyArray));
            
        assertTrue("Should have returned false for non-empty array", 
            !ArrayUtil.isNullOrEmpty(oneArray));            
    }
    
    
    /**
     * Tests invoke().
     */
    public void testInvoke()
    {
        logger_.info("Running testInvoke...");

        StringBuffer[] buffers = new StringBuffer[10];

        for (int i = 0; i < buffers.length; i++)
            buffers[i] = new StringBuffer("");

        ArrayUtil.invoke(buffers, "append", new Object[] {"x"});

        for (int i = 0; i < buffers.length; i++)
            assertEquals("x", buffers[i].toString());
    }

    
    /**
     * Tests invoke() failure.
     */
    public void testInvokeFailure()
    {
        logger_.info("Running testInvokeFailure...");
        
        String[] buffers = new String[] {"a", "b"};
        ArrayUtil.invoke(
            buffers, 
            "\n" + Figlet.getBanner("OK"), 
            new Object[] {"x"});
    }
    
    
    /**
     * Tests toList() for an empty array.
     */
    public void testToListZero()
    {
        logger_.info("Running testToListZero...");
        
        List result = ArrayUtil.toList(new String[0]);
        assertNotNull("Result should not be null for an empty array", result);
        assertTrue("Result should be empty", result.isEmpty());
    }

    
    /**
     * Tests toList() for an array with one element.
     */
    public void testToListOne()
    {
        logger_.info("Running testToListOne...");
        
        List result = ArrayUtil.toList(new String[] {"one"});
        assertNotNull("Result should not be null", result);
        assertEquals("Result should have one element", 1, result.size());
        assertEquals("Contents not the same", "one", result.get(0));
    }
    
    
    /**
     * Tests toList() for an array with many elements.
     */
    public void testToListMany()
    {
        logger_.info("Running testToListMany...");
        
        int max = 100;
        String[] expected = new String[max];
        for (int i = 0; i < max; i++)
            expected[i] = i + "";
        
        List result = ArrayUtil.toList(expected);
        assertNotNull("Result should not be null", result);
        assertEquals("Sizes don't match", expected.length, result.size());
        for (int i = 0; i < max; i++)
            assertEquals("Contents not the same", expected[i], result.get(i));
    }
}