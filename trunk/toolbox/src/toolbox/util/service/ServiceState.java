package toolbox.util.service;

import toolbox.util.AbstractConstant;

/**
 * ServiceState represents the six distincy states of a Service.
 * 
 * @see toolbox.util.service.Service
 */
public class ServiceState extends AbstractConstant
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Unitialized state.
     */
    public static final ServiceState UNINITIALIZED = 
        new ServiceState("uninitialized");
    
    /**
     * Initialized state. A service can only be initialized once.
     */
    public static final ServiceState INITIALIZED = 
        new ServiceState("initialized");
        
    /**
     * Running state.
     */
    public static final ServiceState RUNNING = 
        new ServiceState("running");
    
    /**
     * Suspended state.
     */
    public static final ServiceState SUSPENDED = 
        new ServiceState("suspended");
    
    /**
     * Stopped state.
     */
    public static final ServiceState STOPPED = 
            new ServiceState("stopped");
    
    /**
     * Destroyed is a terminal state.
     */
    public static final ServiceState DESTROYED = 
        new ServiceState("destroyed");
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * String version of the state.
     */
    private String state_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent construction.
     * 
     * @param state Service state.
     */
    private ServiceState(String state)
    {
        state_ = state;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Returns state in string form.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return state_;
    }
}