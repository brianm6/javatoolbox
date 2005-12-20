package toolbox.util.service;

import toolbox.util.statemachine.impl.DefaultState;

/**
 * ServiceState represents the various lifecycle states of a Service.
 *  
 * @see toolbox.util.service.Service
 */
public class ServiceState extends DefaultState
{
    //--------------------------------------------------------------------------
    // ServiceState Constants
    //--------------------------------------------------------------------------
    
    /**
     * Unitialized state.
     */
    public static final ServiceState UNINITIALIZED = 
        new ServiceState("uninitialized");
    
    /**
     * Initialized state.
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
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent construction.
     * 
     * @param state Service state.
     */
    private ServiceState(String state)
    {
        super(state);
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Returns the states name.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getName();
    }
}