package toolbox.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for Collections
 */
public final class CollectionUtil
{
    /**
     * Constructor for CollectionUtil.
     */
    private CollectionUtil()
    {
    }

    
    /**
     * Assembles the union of a pair of sets
     * 
     * @param   set1  First set
     * @param   set2  Second set
     * @return  Collection containing the Union of set1 and set2
     */    
    public static final Collection union(Collection set1, Collection set2)
    {
        Collection union = new HashSet(set1);
        union.addAll(set2);
        return union;
    }

    
    /**
     * Assembles the intersection of a pair of sets
     * 
     * @param   set1  First set
     * @param   set2  Second set
     * @return  Collection containing the intersection of set1 and set2
     */    
    public static final Collection intersection(Collection set1, Collection set2)
    {
        Collection intersection = new HashSet(set1);
        intersection.retainAll(set2);
        return intersection;
    }


    /**
     * Assembles the set difference of a pair of sets
     * 
     * @param   set1  First set
     * @param   set2  Second set
     * @return  Collection containing the difference of set1 and set2
     */    
    public static final Collection difference(Collection set1, Collection set2)
    {
        Collection difference = new HashSet(set1);
        difference.removeAll(set2);
        return difference;
    }

    
    /**
     * Determines if set2 is a subset of set1
     * 
     * @param   set1  First set
     * @param   set2  Second set
     * @return  True if set2 is a subset of set1, false otherwise
     */    
    public static final boolean isSubset(Collection set1, Collection set2)
    {
        return set1.containsAll(set2);
    }
}
