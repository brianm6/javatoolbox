package toolbox.jtail.filter;

/**
 * Base abstract implementation of an ILineFilter that adds behavior to keep
 * track of whether the filter is enabled or not.
 */
public abstract class AbstractLineFilter implements ILineFilter
{
    /** 
     * Tracks enabled state of the filter. 
     */
    private boolean enabled_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates an AbstractLineFilter that is not enabled by default.
     */
    public AbstractLineFilter()
    {
        this(false);    
    }
        
        
    /**
     * Creates an AbstractLineFilter with the given state.
     * 
     * @param enabled Set to true to enable the filter or false to disable the 
     *         filter.
     */
    public AbstractLineFilter(boolean enabled)
    {
        enabled_ = enabled;    
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns true if the filter is enabled, false otherwise.
     * 
     * @return boolean
     */
    public boolean isEnabled()
    {
        return enabled_;
    }


    /**
     * Enables/disables.
     * 
     * @param b Set enabled flag
     */
    public void setEnabled(boolean b)
    {
        enabled_ = b;
    }
}