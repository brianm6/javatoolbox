package toolbox.graph.jung;

import edu.uci.ics.jung.visualization.Layout;

/**
 * Jung implementation of a {@link toolbox.graph.Layout}.
 */
public class JungLayout implements toolbox.graph.Layout
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Jung layout delegate.
     */
    private Layout delegate_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JungLayout.
     */
    public JungLayout(Layout layout)
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