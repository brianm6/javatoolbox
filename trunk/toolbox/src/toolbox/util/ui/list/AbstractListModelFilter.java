package toolbox.util.ui.list;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 * Abstract filter class for list models
 */
public abstract class AbstractListModelFilter extends AbstractListModel
{
    /** 
     * Reference to the list model being filtered 
     */
    private ListModel delegate_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a list model filter with the given delegate
     * 
     * @param delegate Reference to the model being filtered
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

    //--------------------------------------------------------------------------
    // ListModel Interface
    //--------------------------------------------------------------------------

    /**
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize()
    {
        return delegate_.getSize();
    }

    /**
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index)
    {
        return delegate_.getElementAt(index);
    }

    /**
     * @see javax.swing.ListModel#addListDataListener(
     *      javax.swing.event.ListDataListener)
     */
    public void addListDataListener(ListDataListener listener)
    {
        delegate_.addListDataListener(listener);
    }
    
    /**
     * @see javax.swing.ListModel#removeListDataListener(
     *      javax.swing.event.ListDataListener)
     */
    public void removeListDataListener(ListDataListener listener)
    {
        delegate_.removeListDataListener(listener);
    }
}