package toolbox.util.service;

import toolbox.util.AbstractConstant;

/**
 * ServiceState represents the lifecycle state of a Service.
 * 
 * @see toolbox.util.service.Service
 */
public class ServiceState extends AbstractConstant
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    public static final ServiceState UNINITIALIZED = 
        new ServiceState("uninitialized");
    
    public static final ServiceState INITIALIZED = 
        new ServiceState("initialized");
        
    public static final ServiceState RUNNING = 
        new ServiceState("running");
    
    public static final ServiceState SUSPENDED = 
        new ServiceState("suspended");
    
    public static final ServiceState STOPPED = 
            new ServiceState("stopped");
    
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