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

    /**
     * Friendly name of the layout.
     */
    private String name_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JungLayout.
     */
    public JungLayout(String name, Layout layout)
    {
        setName(name);
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
    
    //--------------------------------------------------------------------------
    // Layout Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.graph.Layout#getName()
     */
    public String getName()
    {
        return name_;
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the layout name.
     * 
     * @param name Layout name.
     */
    public void setName(String name)
    {
        name_ = name;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getName();
    }
}