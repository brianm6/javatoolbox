package toolbox.util;

import java.lang.reflect.Array;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.log4j.Logger;

/**
 * Array utility class.
 */
public final class ArrayUtil
{
    private static final Logger logger_ = Logger.getLogger(ArrayUtil.class);
    
    // Clover private constructor workaround
    static { new ArrayUtil(); }
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Private constructor.
     */
    private ArrayUtil()
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Initializes an array of double with a given value.
     * 
     * @param d Array of doubles.
     * @param value Initialization value.
     * @return Initialized array of doubles.
     */
    public static double[] init(double[] d, double value)
    {
        for (int i = 0; i < d.length; d[i++] = value);
        return d;
    }

    
    /**
     * Initializes an array of ints with a given value.
     * 
     * @param d Array of ints.
     * @param value Initialization value.
     * @return Initialized array of ints.
     */
    public static int[] init(int[] d, int value)
    {
        for (int i = 0; i < d.length; d[i++] = value);
        return d;
    }

    
    /**
     * Returns subset of a given double array.
     *
     * @param array The array to get subset of.
     * @param startIndex The starting index (inclusive).
     * @param endIndex The ending index (inclusive).
     * @return Subset of the array.
     */
    public static double[] subset(double[] array, int startIndex, int endIndex)
    {
        int len = array.length;

        if (len == 0)
            return new double[0];

        // Do bounds checking
        Assert.isTrue(startIndex <= endIndex, 
                      "Start index " + startIndex + 
                      " must be <= end index of " + 
                      endIndex);
                      
        Assert.isTrue(endIndex <= len, 
                      "End index " + endIndex + 
                      " must be <= array length of " + len);

        // Copy array
        int subLen = (endIndex - startIndex) + 1;
        double[] sub = new double[subLen];
        int s = 0;

        for (int i = startIndex; i <= endIndex;)
            sub[s++] = array[i++];

        return sub;
    }

    
    /**
     * Returns the subset of an array of objects.
     * 
     * @param array Array to extract subset from.
     * @param startIndex Starting index of the subset (zero based).
     * @param endIndex Ending index (inclusive).
     * @return Subset of the array
     * @throws IllegalArgumentException if indices are out of bounds.
     */
    public static Object[] subset(Object[] array, int startIndex, int endIndex)
    {
        int   len   = array.length;
        Class clazz = array.getClass().getComponentType();                

        if (len == 0)
        {
            return (Object[]) Array.newInstance(clazz, 0);
        }
        else if (startIndex >= 0        &&
                 startIndex <= len - 1  &&
                 startIndex <= endIndex &&
                 endIndex < len)
        { 
            int subLen = endIndex - startIndex + 1;
            Object[] subset = (Object[])Array.newInstance(clazz, subLen);
            System.arraycopy(array, startIndex, subset, 0, subLen);
            return subset;
        }
        else
        {
            throw new IllegalArgumentException(
                "Subset [" + startIndex + ", " + endIndex + "] " +
                "is not valid for the range [0," + (len-1) + "]");
        }
    }

    
    /**
     * Converts an array of doubles to a string. Good for debug output.
     * 
     * @param array Array of doubles.
     * @return String representing contents of array.
     */
    public static String toString(double[] array)
    {
        Double[] wrapper = new Double[array.length];

        for (int i = 0; i < array.length; i++)
            wrapper[i] = new Double(array[i]);

        return toString(wrapper);
    }

    
    /**
     * Converts an array of ints to a string. Good for debug output.
     * 
     * @param array Array of ints.
     * @return String representing contents of array.
     */
    public static String toString(int[] array)
    {
        Integer[] wrapper = new Integer[array.length];

        for (int i = 0; i < array.length; i++)
            wrapper[i] = new Integer(array[i]);

        return toString(wrapper);
    }

    
    /**
     * Converts an array of objects into a comma delimited single line 
     * string of each elements toString().
     *
     * @param array Array of objects to stringify.
     * @return String of comma delimited array elements toString().
     */
    public static String toString(Object[] array)
    {
        return toString(array, false);
    }

    
    /**
     * Converts an object array into a comma delimited string of 
     * each elements toString().
     *
     * @param array Array of objects to stringify.
     * @param onePerLine If true, the entire contents are represented on a 
     *        single line. If false, the string will contain one element per 
     *        line.
     * @return String representation of array of objects.
     */
    public static String toString(Object[] array, boolean onePerLine)
    {
        StringBuffer sb = new StringBuffer("[" + array.length + "]{");

        switch (array.length)
        {
            case 0 : break;
            
            case 1 : sb.append(array[0].toString()); break;
            
            default:
             
                for (int i = 0; i < array.length - 1; i++)
                {
                    if (i != 0)
                        sb.append(", ");
    
                    if (onePerLine)
                        sb.append("\n");
    
                    sb.append(array[i].toString());
                }
    
                sb.append(", ");
    
                if (onePerLine)
                    sb.append("\n");
    
                sb.append(array[array.length - 1].toString());
                break;
        }

        sb.append("}");

        return sb.toString();
    }
    
    
    /**
     * Determines if an object exists in a given array of objects. Uses equals() 
     * for comparison.
     * 
     * @param array Array of objects to search.
     * @param obj Object to search for.
     * @return -1 if the object is not found, otherwise the index of the first 
     *         matching object.
     */
    public static int indexOf(Object[] array, Object obj)
    {
        if (array.length == 0)
            return -1;
        
        int idx = 0;
        
        while (idx < array.length)
        {
            if (obj.equals(array[idx]))
                return idx;
            else
                idx++;
        }
                
        return -1;
    }
    
    
    /**
     * Determines if an array of objects contains an object.
     * 
     * @param array Array of objects to search.
     * @param obj Object to search for.
     * @return True if the object is found in the array, false otherwise.
     */
    public static boolean contains(Object[] array, Object obj)
    {
        return !(indexOf(array, obj) == -1);
    }

    
    /**
     * Determines if an array is null or empty.
     * 
     * @param array Array to check for null or empty.
     * @return True if an array is null or has a size of length zero, false 
     *         otherwise.
     */
    public static boolean isNullOrEmpty(Object[] array)
    {
        return (array == null || array.length == 0);
    }

    
    /**
     * Concats two arrays (one right after the other) with homogenous content.
     * Arrays must contain elements of the same type!
     * 
     * @param head Array at the head of the resulting array.
     * @param tail Array at the tail of the resulting array.
     * @return Concatenated array.
     */
    public static Object[] concat(Object[] head, Object[] tail)
    {
        int      len    = head.length + tail.length;
        Class    clazz  = head.getClass().getComponentType();
        Object[] result = (Object[]) Array.newInstance(clazz, len);
        
        System.arraycopy(head, 0, result, 0, head.length);
        System.arraycopy(tail, 0, result, head.length, tail.length);
        
        return result;
    } 
    
    
    /**
     * Adds an element to the end of an existing array and returns the new 
     * array.
     * 
     * @param array An array to add the element to.
     * @param element Element to append.
     * @return New array with element.
     */
    public static Object add(Object[] array, Object element)
    {
        int length = array.length;
        
        // Create a new array of length + 1
        Object[] newArray = (Object[])
            Array.newInstance(array.getClass().getComponentType(), length + 1);
        
        // Copy everything over    
        System.arraycopy(array, 0, newArray, 0, length);
        
        // Set the last index of the array to the new element
        newArray[length] = element;
        
        return newArray;
    }
    
    
    /**
     * Inserts an element to the beginning of an array. The component type of 
     * the array must be the same as that type of the element.
     * 
     * @param array An array.
     * @param element The element to insert.
     * @return New array with element.
     */
    public static Object[] insert(Object[] array, Object element)
    {
        return insertAt(array, element, 0);
    }
    
    
    /**
     * Inserts an element into the given position of an array. The component 
     * type of the array must be the same as that type of the element.
     * 
     * @param array An array.
     * @param element The element to insert.
     * @param index The index to insert the element before.
     * @return New array with element.
     */
    public static Object[] insertAt(Object[] array, Object element, int index)
    {
        int length = Array.getLength(array);
        
        Object[] newarray = (Object[])
            Array.newInstance(array.getClass().getComponentType(), length + 1);
    
        if (index > 0)
            System.arraycopy(array, 0, newarray, 0, index);
    
        Array.set(newarray, index, element);
        System.arraycopy(array, index, newarray, index + 1, length - index);
        return newarray;
    }
    
    
    /**
     * Determines if two given arrays are equal in length and content.
     * 
     * @param array1 First array. 
     * @param array2 Second array.
     * @return True if the two arrays are equal by reference or equality and
     *         each of the indices values are also equal by reference or 
     *         equality, false otherwise.
     */
    public static boolean equals(Object[] array1, Object[] array2)
    {
        if (array1 == array2)
            return true;

        if (array1.length != array2.length)
            return false;

        for (int i = 0; i < array1.length; i++)
        {
            if (array1[i] != array2[i])
                if (!array1[i].equals(array2[i]))
                    return false;
        }

        return true;
    }

    
    /**
     * Removes the first occurence of an object from an array.
     * 
     * @param array Array to remove object from.
     * @param element Object to remove.
     * @return New array with the object removed, or the original array if
     *         the object was not a memver of the array.
     */    
    public static Object[] remove(Object[] array, Object element)
    {
        Object[] result = null;
        int index = indexOf(array, element);
        
        if (index >= 0)
        {
            int length = array.length;
            
            Object[] newArray = 
                (Object[]) Array.newInstance(
                    array.getClass().getComponentType(), length - 1);
        
            System.arraycopy(array, 0, newArray, 0, index);
            System.arraycopy(
                array, 
                Math.min(index+1, array.length-1), 
                newArray, 
                Math.min(index, array.length-1), 
                Math.min(length - index - 1 , array.length-1));
            
            result = newArray;            
        }
        else
        {
            result = array;
        }
            
        return result;
    }
    
    
    /**
     * Invokes a method on each element of an array.
     * 
     * @param array Array of objects. 
     * @param method Method to invoke on each object.
     * @param params Parameters to pass to each method invocation.
     */    
    public static void invoke(Object[] array, String method, Object[] params)
    {
        for (int i=0; i<array.length; i++)
        {
            try
            {
                MethodUtils.invokeMethod(array[i], method, params);
            }
            catch (Exception e)
            {
                logger_.error("invoke", e);
            }
        }
    }
}