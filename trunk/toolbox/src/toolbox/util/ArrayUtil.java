package toolbox.util;

import java.lang.reflect.Array;

/**
 * Array utility class
 */
public class ArrayUtil
{

    /**
     * Prevent construction
     */
    private ArrayUtil()
    {
    }


    /**
     * Initializes an array of double with a given value
     * 
     * @param   d      Array of doubles
     * @param   value  Initialization value
     * @return  Initialized array of doubles
     */
    public static double[] init(double[] d, double value)
    {
        for (int i = 0; i < d.length; d[i++] = value);
        return d;
    }


    /**
     * Initializes an array of ints with a given value
     * 
     * @param    d      Array of ints
     * @param    value  Initialization value
     * @return   Initialized array of ints
     */
    public static int[] init(int[] d, int value)
    {
        for (int i = 0; i < d.length; d[i++] = value);
        return d;
    }


    /**
     * Returns subset of a given double array
     *
     * @param    array       The array to get subset of
     * @param    startIndex  The starting index (inclusive)
     * @param    endIndex    The ending index (inclusive)
     * @return   Subset of array
     */
    public static double[] subset(double[] array, int startIndex, int endIndex)
    {
        int len = array.length;

        if (len == 0)
            return new double[0];

        /* do bounds checking */
        Assert.isTrue(startIndex <= endIndex, 
                      "Start index " + startIndex + 
                      " must be <= end index of " + 
                      endIndex);
                      
        Assert.isTrue(endIndex <= len, 
                      "End index " + endIndex + 
                      " must be <= array length of " + len);

        /* copy array */
        int subLen = (endIndex - startIndex) + 1;
        double[] sub = new double[subLen];
        int s = 0;

        for (int i = startIndex; i <= endIndex;)
            sub[s++] = array[i++];

        return sub;
    }

    /**
     * Returns subset of a given array of objects
     * 
     * @param    array       The array to get subset of
     * @param    startIndex  The starting index (inclusive)
     * @param    endIndex    The ending index (inclusive)
     * @return   Subset of array
     */
    public static Object[] subset(Object[] array, int startIndex, int endIndex)
    {
        int      len       = array.length;
        Class    classType = array.getClass().getComponentType();
        Object[] subArray  = null;
                

        if (len == 0)
            return (Object[])Array.newInstance(classType, 0);
        else
        {        
            /* do bounds checking */
            Assert.isTrue(startIndex <= endIndex, 
                          "Start index " + startIndex + 
                          " must be <= end index of " + 
                          endIndex);
                          
            Assert.isTrue(endIndex < len, 
                          "End index " + endIndex + 
                          " must be < array length of " + len);
    
            /* copy array */
            int subLen = (endIndex - startIndex) + 1;
            subArray = (Object[])Array.newInstance(classType, subLen);
            int s = 0;
    
            for (int i = startIndex; i <= endIndex;)
                subArray[s++] = array[i++];
        }

        return subArray;
    }



    /**
     * Converts an array of doubles to a string. Good for debug output.
     * 
     * @param    array  Array of doubles
     * @return   String representing contents of array
     */
    public static String toString(double[] array)
    {
        Double[] wrapper = new Double[array.length];

        for (int i = 0; i < array.length; i++)
            wrapper[i] = new Double(array[i]);

        return toString(wrapper);
    }


    /**
     * Converts an array of ints to a string. Good for debug output
     * 
     * @param     array   Array of ints
     * @return    String representing contents of array
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
     * string of each elements toString()
     *
     * @param    array        Array of objects to stringify
     * @return   String of comma delimited array elements toString()
     */
    public static String toString(Object[] array)
    {
        return toString(array, false);
    }


    /**
     * Converts an object array into a comma delimited string of 
     * each elements toString()
     *
     * @param     array        Array of objects to stringify
     * @param     onePerLine   If true, the entire contents are represented 
     *                         on a single line. If false, the string will
     *                         contain one element per line.
     * @return    String representation of array of objects
     */
    public static String toString(Object[] array, boolean onePerLine)
    {
        StringBuffer sb = new StringBuffer("[" + 
                                           array.length + 
                                           "]{");


        switch (array.length)
        {
            case 0  : break;
                      
            case 1  : sb.append(array[0].toString());
                      break;
                      
            default: 
                        for (int i = 0; i < array.length - 1; i++)
                        {
                            if (i != 0)
                                sb.append(", ");
            
                            if (onePerLine)
                                sb.append("\n");
            
                            sb.append(array[i].toString());
                        }
            
                        if (array.length > 1)
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
     * Determines if an object exists in a given array of objects.
     * Uses equals() for comparison.
     * 
     * @param    array   Array of objects to search
     * @param    obj     Object to search for
     * @return   -1 if the object is not found, otherwise the index
     *           of the first matching object
     */
    public static int indexOf(Object[] array, Object obj)
    {
        if (array.length == 0)
            return -1;
            
        boolean found = false;
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
     * Determines if an array of objects contains an object
     * 
     * @param   array   Array of objects to search
     * @param   obj     Object to search for
     * @return  True if the object is found in the array, false otherwise
     */
    public static boolean contains(Object[] array, Object obj)
    {
        if (indexOf(array, obj) == -1)
            return false;
        else
            return true;    
    }

    /**
     * Determines if an array is null or empty
     * 
     * @return True if an array is null or has a size of length zero,
     *         false otherwise
     */
    public static boolean isNullOrEmpty(Object[] array)
    {
        return (array == null || array.length == 0);
    }
    

    /**
     * Concats two arrays (one right after the other) with homogenous content.
     * Arrays must contain elements of the same type!
     * 
     * @param   head  Array at front
     * @param   tail  Array at back
     * @result  Concatenated array
     */
    public static Object[] concat(Object[] head, Object[] tail)
    {
        int      len    = head.length + tail.length;
        Class    clazz  = head.getClass().getComponentType();
        Object[] result = (Object[])Array.newInstance(clazz, len);
        
        System.arraycopy(head, 0, result, 0, head.length);
        System.arraycopy(tail, 0, result, head.length, tail.length);
        
        return result;
    } 
    
    
    /**
     * Adds an element to an existing array and returns the new array.
     * 
     * @param   array       An array to add the element to
     * @param   element     Element to append
     */
    public static Object addElement(Object[] array, Object element)
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
}