package toolbox.util.collections;

import java.util.ArrayList;
import java.util.List;

/**
 * Elements are inserted at the beginning of the list.
 * Elements are evicted from the tail of the list.
 */
public class EvictingList
{
    /** Backing store */
    private List list_;
    
    /** Max size of the list. */
    private int max_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an EvictingList
     * 
     * @param  max  Maximum size of the list before elements start getting
     *              evicted
     */
    public EvictingList(int max)
    {
        max_ = max;
        list_ = new ArrayList(max_);
    }
    
    //--------------------------------------------------------------------------
    // Public
    //-------------------------------------------------------------------------- 
    
    public void add(Object element)
    {
        if (list_.size() == max_)
            makeRoom();
            
        list_.add(0, element);
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    protected void makeRoom()
    {
        list_.remove(max_ - 1);
    }
    
    //--------------------------------------------------------------------------
    // Delegations
    //--------------------------------------------------------------------------
    
    public int size()
    {
        return list_.size();
    }
    
    public Object get(int i)
    {
        return list_.get(i);
    }
}
