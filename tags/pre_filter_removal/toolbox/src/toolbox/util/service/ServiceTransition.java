package toolbox.util.service;

import toolbox.util.statemachine.impl.DefaultTransition;

/**
 * A ServiceTransitions represents an activity that causes a change of state
 * in a Service.
 * 
 * @see toolbox.util.service.Service
 * @see toolbox.util.service.ServiceState
 */
public class ServiceTransition extends DefaultTransition
{
    //--------------------------------------------------------------------------
    // Transition Constants
    //--------------------------------------------------------------------------
    
    /**
     * Transition to initialize a service.
     */
    public static final ServiceTransition INITIALIZE = 
        new ServiceTransition("initialize");
        
    /**
     * Transition to start a service.
     */
    public static final ServiceTransition START = 
        new ServiceTransition("start");
    
    /**
     * Transition to suspend a service.
     */
    public static final ServiceTransition SUSPEND = 
        new ServiceTransition("suspend");
    
    /**
     * Transition to resume a service.
     */
    public static final ServiceTransition RESUME = 
        new ServiceTransition("resume");
    
    /**
     * Transition to stop a service.
     */
    public static final ServiceTransition STOP = 
            new ServiceTransition("stop");
    
    /**
     * Transition to destroy a service.
     */
    public static final ServiceTransition DESTROY = 
        new ServiceTransition("destroy");
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent construction.
     * 
     * @param name Transition name.
     */
    private ServiceTransition(String name)
    {
        super(name);
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Returns this transitions name.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getName();
    }
}