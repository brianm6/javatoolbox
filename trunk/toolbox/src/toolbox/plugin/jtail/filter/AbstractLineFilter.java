package toolbox.jtail.filter;

/**
 * AbstractLineFilter
 */
public abstract class AbstractLineFilter implements ILineFilter
{
    private boolean enabled_;
    
    /**
     * Constructor for AbstractLineFilter.
     */
    public AbstractLineFilter()
    {
        super();
    }

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
