/**
 * Copyright 2002, Southwest Airlines
 * All Rights Reserved
 */
package toolbox.util.collections;

import java.util.Comparator;

/**
 * ReverseComparator is useful for sorting in the reverse direction
 */
public class ReverseComparator implements Comparator
{
    /** Comparator whose comparisons are going to be reversed **/
    protected Comparator comparator_;

    /**
     * Construct a comparator that generates reverse of another comparator
     * 
     * @param   comparator  Comparator to be reversed
     */
    public ReverseComparator(Comparator comparator)
    {
        comparator_ = comparator;
    }

    /**
     * Compares two objects
     * 
     * @param   a   Object performing the compare
     * @param   b   Object being compared
     * @return  Reverse of passed in comparator
     */
    public int compare(Object a, Object b)
    {
        return -comparator_.compare(a, b);
    }
}