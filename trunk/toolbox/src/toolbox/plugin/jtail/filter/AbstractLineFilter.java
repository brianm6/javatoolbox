package toolbox.jtail.filter;

/**
 * AbstractLineFilter contains only behavior to managed enabled state
 */
public abstract class AbstractLineFilter implements ILineFilter
{
    private boolean enabled_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
        
    /**
     * Constructor for AbstractLineFilter.
     */
    public AbstractLineFilter()
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * @return True if the filter is enabled, false otherwise
     */
    public boolean isEnabled()
    {
        return enabled_;
    }

    /**
     * Enables/disables
     */
    public void setEnabled(boolean b)
    {
        enabled_ = b;
    }
}
