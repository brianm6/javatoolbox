package toolbox.graph.prefuse;

import edu.berkeley.guir.prefuse.action.assignment.Layout;

/**
 * Prefuse implementation of a {@link toolbox.graph.Layout}.
 */
public class PrefuseLayout implements toolbox.graph.Layout
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Name of this layout.
     */
    private String name_;
    
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
    public PrefuseLayout(String name, Layout layout)
    {
        setName(name);
        delegate_ = layout;
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
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the name of this layout.
     * 
     * @param name The name to set.
     */
    public void setName(String name)
    {
        name_ = name;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Returns the name of this layout.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getName();
    }
}