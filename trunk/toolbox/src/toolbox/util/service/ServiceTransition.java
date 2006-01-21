package toolbox.util.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import toolbox.util.statemachine.impl.DefaultTransition;

/**
 * A ServiceTransitions represents an activity that causes a change of state
 * in a Service.
 * 
 * @see toolbox.util.service.Service
 * @see toolbox.util.service.ServiceState
 */
public class ServiceTransition extends DefaultTransition {
    
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
    private ServiceTransition(String name) {
        super(name);
    }
    
    
    /**
     * Returns an iterator for all known service transitions.
     *  
     * @return Iterator
     */
    public static final Iterator iterator() {
        List transitions = new ArrayList();
        transitions.add(INITIALIZE);
        transitions.add(START);
        transitions.add(SUSPEND);
        transitions.add(RESUME);
        transitions.add(STOP);
        transitions.add(DESTROY);
        return transitions.iterator();
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Returns this transitions name.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getName();
    }
}