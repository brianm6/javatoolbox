package toolbox.util.service;

/**
 * A Cancelable entity. 
 */
public interface Cancelable
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Javabean property that represents the canceled state.
     */
    static final String PROP_CANCELED = "canceled";
    
    //--------------------------------------------------------------------------
    // Interface
    //--------------------------------------------------------------------------
    
    /**
     * Requests that the implementor of this interface cancel the current task.
     */
    void cancel();
    
    
    /**
     * Returns true if the implementor of this inteface has already been 
     * canceled, false otherwise.
     * 
     * @return boolean
     */
    boolean isCanceled();
}