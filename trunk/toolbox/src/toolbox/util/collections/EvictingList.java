package toolbox.util.collections;

import java.util.ArrayList;
import java.util.List;

/**
 * Elements are inserted at the beginning of the list.
 * Elements are evicted from the tail of the list.
 */
public class EvictingList
{
    /** 
     * Backing store. 
     */
    private List list_;
    
    /** 
     * Max size of the list. 
     */
    private int max_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an EvictingList.
     * 
     * @param max Maximum size of the list before elements start getting bumped
     *        off. Must be > 0.
     */
    public EvictingList(int max)
    {
        if (max <= 0)
            throw new IllegalArgumentException("List size must be > 0");
            
        max_ = max;
        list_ = new ArrayList(max_);
    }
        
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Adds to the list.
     * 
     * @param element Element to add
     */    
    public void add(Object element)
    {
        if (list_.size() == max_)
            list_.remove(max_ - 1);
            
        list_.add(0, element);
    }

    //--------------------------------------------------------------------------
    // Delegations
    //--------------------------------------------------------------------------
    
    /**
     * Returns the size of the list.
     * 
     * @return int
     */
    public int size()
    {
        return list_.size();
    }
    
    
    /**
     * Gets the element at the given index.
     * 
     * @param i Index
     * @return Object
     */
    public Object get(int i)
    {
        return list_.get(i);
    }
}
