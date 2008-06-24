package toolbox.plugin.jtail.filter;

/**
 * Abstract base class implementation of an ILineFilter that adds behavior to 
 * keep track of the enabled state of the filter.
 */
public abstract class AbstractLineFilter implements ILineFilter
{
    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    /** 
     * Tracks enabled state of the filter. 
     */
    private boolean enabled_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates an AbstractLineFilter that is <b>disabled</b> by default.
     */
    public AbstractLineFilter()
    {
        this(false);    
    }
        
        
    /**
     * Creates an AbstractLineFilter with the given enabled state.
     * 
     * @param enabled Set to true to enable the filter or false to disable the 
     *        filter.
     */
    public AbstractLineFilter(boolean enabled)
    {
        setEnabled(enabled);    
    }

    //--------------------------------------------------------------------------
    // Enableable Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.Enableable#isEnabled()
     */
    public boolean isEnabled()
    {
        return enabled_;
    }


    /*
     * @see toolbox.util.service.Enableable#setEnabled(boolean)
     */
    public void setEnabled(boolean b)
    {
        enabled_ = b;
    }
}