package toolbox.util.ui;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 * Abstract filter class for list models
 */
public abstract class AbstractListModelFilter extends AbstractListModel
{
    /**
     * Storage of reference to model being filtered
     */
    private ListModel delegate_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a list model filter with the given delegate
     * 
     * @param  delegate  Reference to the model being filtered
     */
    public AbstractListModelFilter(ListModel delegate)
    {
        delegate_ = delegate;
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------

    /**
     * @return Delegate list model
     */    
    public ListModel getDelegate()
    {
        return delegate_;
    }

    //--------------------------------------------------------------------------
    //  Overridden from javax.swing.AbstractListModel
    //--------------------------------------------------------------------------

    /**
     * @return  Size of the filtered model
     */    
    public int getSize()
    {
        // delegate to filter target
        return delegate_.getSize();
    }

    /**
     * Retrieves element at a given index
     * 
     * @param   index  Index to retrieve
     * @return  List element at given index in filtered model
     */
    public Object getElementAt(int index)
    {
        // delegate to filter target
        return delegate_.getElementAt(index);
    }

    //--------------------------------------------------------------------------
    // Listener support 
    //--------------------------------------------------------------------------
    
    /**
     * Adds a data listener
     * 
     * @param  listener   Listener to add
     */
    public void addListDataListener(ListDataListener listener)
    {
        // delegate to filter target
        delegate_.addListDataListener(listener);
    }

    /**
     * Removes a data listener
     * 
     * @param  listener  Listener to remove
     */
    public void removeListDataListener(ListDataListener listener)
    {
        // delegate to filter target
        delegate_.removeListDataListener(listener);
    }
}