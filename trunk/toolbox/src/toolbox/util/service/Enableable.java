package toolbox.util.service;

/**
 * An Enableable object supports an enabled and disabled state. 
 */
public interface Enableable extends ServiceNature
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Javabean property that represents the enabled state.
     */
    static final String PROP_ENABLED = "enabled";
    
    //--------------------------------------------------------------------------
    // Interface
    //--------------------------------------------------------------------------
    
    /**
     * Sets the enabled state of this object.
     * 
     * @param enabled True to enable, or false otherwise.
     */
    void setEnabled(boolean enabled);
    
    
    /**
     * Returns true if this object is in an enabled state, false otherwise.
     * 
     * @return boolean
     */
    boolean isEnabled();
}