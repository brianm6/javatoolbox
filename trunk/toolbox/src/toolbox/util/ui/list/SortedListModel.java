package toolbox.util.ui.list;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

/**
 * List model that maintains its elements in sorted order.
 */
public class SortedListModel extends AbstractListModel
{
    // TODO: Write unit test.
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Holds model contents in sorted order.
     */
    private SortedSet model_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SortedListModel.
     */
    public SortedListModel()
    {
        model_ = new TreeSet();
    }

    
    /**
     * Creates a SortedListModel with the given Comparator.
     * 
     * @param comparator Comparator used to compare elements.
     */
    public SortedListModel(Comparator comparator)
    {
        model_ = new TreeSet(comparator);
    }
    
    //--------------------------------------------------------------------------
    // ListModel Interface
    //--------------------------------------------------------------------------

    /**
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize()
    {
        return model_.size();
    }


    /**
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index)
    {
        return model_.toArray()[index];
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Adds an element to this model.
     * 
     * @param element Element to add.
     */
    public void add(Object element)
    {
        if (model_.add(element))
        {
            fireContentsChanged(this, 0, getSize());
        }
    }


    /**
     * Adds an array of elements to this model.
     * 
     * @param elements Array of elements.
     */
    public void addAll(Object elements[])
    {
        Collection c = Arrays.asList(elements);
        model_.addAll(c);
        fireContentsChanged(this, 0, getSize());
    }


    /**
     * Removes all elements from this model. 
     */
    public void clear()
    {
        model_.clear();
        fireContentsChanged(this, 0, getSize());
    }


    /**
     * Returns true if this model contains the given element.
     * 
     * @param element Element to check.
     * @return boolean
     */
    public boolean contains(Object element)
    {
        return model_.contains(element);
    }


    /**
     * Returns the first element in this sorted model.
     * 
     * @return Object
     */
    public Object firstElement()
    {
        return model_.first();
    }


    /**
     * Returns an iterator for this sorted model.
     * 
     * @return Iterator
     */
    public Iterator iterator()
    {
        return model_.iterator();
    }


    /**
     * Returns the last element of this sorted model.
     * 
     * @return Object
     */
    public Object lastElement()
    {
        return model_.last();
    }


    /**
     * Removes an element from this model and return true if the removal was
     * successful or false otherwise.
     * 
     * @param element Element to remove.
     * @return boolean
     */
    public boolean removeElement(Object element)
    {
        boolean removed = model_.remove(element);
        
        if (removed)
        {
            fireContentsChanged(this, 0, getSize());
        }
        return removed;
    }
}