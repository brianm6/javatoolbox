package toolbox.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import toolbox.util.RandomUtil;

/**
 * Simple grab bag from which you can pick random elements.
 */
public class GrabBag
{
    /** delegate **/
    private List delegate_ = new ArrayList();


    /**
     * Default constructor
     */
    public GrabBag()
    {
    }

    
    /**
     * Creates a GrabBag with the given collection
     * 
     * @param  c  Collection
     */
    public GrabBag(Collection c)
    {
        delegate_.addAll(c);
    }


    /**
     * Adds an element to the grab bag
     *
     * @param  obj  Object to add to the grab bag
     */
    public void add(Object obj)
    {
        delegate_.add(obj);
    }


    /**
     * Picks a random element from the bag
     * 
     * @return Random element. Null if the bag is empty.
     */
    public Object get()
    {
        int size = size();
        
        if (size == 0)
            return null;
        else
            return delegate_.get(RandomUtil.nextInt(size-1));
    }


    /**
     * @return The number of elements in the bag
     */
    public int size()
    {
        return delegate_.size();
    }
}