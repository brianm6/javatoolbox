package toolbox.util.ui;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 * Abstract filter class for list models
 */
public abstract class AbstractListModelFilter extends AbstractListModel
{
    /** Storage of reference to model being filtered */
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
     * Returns the delegate list model
     * 
     * @return ListModel
     */    
    public ListModel getDelegate()
    {
        return delegate_;
    }

    public int getSize()
    {
        // delegate to filter target
        return delegate_.getSize();
    }

    public Object getElementAt(int index)
    {
        // delegate to filter target
        return delegate_.getElementAt(index);
    }

    //--------------------------------------------------------------------------
    // Listener support 
    //--------------------------------------------------------------------------
    
    public void addListDataListener(ListDataListener listener)
    {
        // delegate to filter target
        delegate_.addListDataListener(listener);
    }
    
    public void removeListDataListener(ListDataListener listener)
    {
        // delegate to filter target
        delegate_.removeListDataListener(listener);
    }
}