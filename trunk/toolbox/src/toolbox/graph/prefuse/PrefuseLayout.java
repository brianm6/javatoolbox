package toolbox.graph.prefuse;

import edu.uci.ics.jung.visualization.Layout;

/**
 * Prefuse implementation of a {@link toolbox.graph.Layout}.
 */
public class PrefuseLayout implements toolbox.graph.Layout
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Prefuse layout delegate.
     */
    private Layout delegate_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a PrefuseLayout.
     */
    public PrefuseLayout(Layout layout)
    {
        delegate_ = layout;
    }
    
    //--------------------------------------------------------------------------
    // Delegator Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.graph.Delegator#getDelegate()
     */
    public Object getDelegate()
    {
        return delegate_;
    }
}