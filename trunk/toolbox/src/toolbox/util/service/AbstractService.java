package toolbox.util.service;

import java.util.Map;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.statemachine.StateMachine;

/**
 * Abstract base class for Service implementors.
 */
public abstract class AbstractService 
    implements Startable, Initializable, Suspendable, Destroyable
{
    private static final Logger logger_ = 
        Logger.getLogger(AbstractService.class);
    
    /**
     * Strict state transitions are turned off by default.
     */
    private static final boolean DEFAULT_STRICT = false;
    
    //--------------------------------------------------------------------------
    // Static Initializers
    //--------------------------------------------------------------------------
    
    static 
    {
//        // Populate strict service state transitions
//        TRANSITIONS_STRICT = new HashMap(8);
//
//        TRANSITIONS_STRICT.put(
//            new MultiKey(
//                ServiceState.UNINITIALIZED, 
//                ServiceActivity.INITIALIZE), 
//            ServiceState.INITIALIZED);
//
//        TRANSITIONS_STRICT.put(
//            new MultiKey(
//                ServiceState.INITIALIZED, 
//                ServiceActivity.START), 
//            ServiceState.RUNNING);
//        
//        TRANSITIONS_STRICT.put(
//            new MultiKey(
//                ServiceState.RUNNING, 
//                ServiceActivity.STOP), 
//            ServiceState.STOPPED); 
//        
//        TRANSITIONS_STRICT.put(
//            new MultiKey(
//                ServiceState.RUNNING, 
//                ServiceActivity.SUSPEND), 
//            ServiceState.SUSPENDED); 
//        
//        TRANSITIONS_STRICT.put(
//             new MultiKey(
//                ServiceState.SUSPENDED, 
//                ServiceActivity.RESUME), 
//             ServiceState.RUNNING);
//
//        TRANSITIONS_STRICT.put(
//                new MultiKey(
//                   ServiceState.SUSPENDED, 
//                   ServiceActivity.START), 
//                ServiceState.RUNNING);
//        
//        TRANSITIONS_STRICT.put(
//            new MultiKey(
//                ServiceState.SUSPENDED, 
//                ServiceActivity.STOP), 
//            ServiceState.STOPPED); 
//        
//        TRANSITIONS_STRICT.put(
//            new MultiKey(
//                ServiceState.STOPPED, 
//                ServiceActivity.START), 
//            ServiceState.RUNNING); 
//        
//        TRANSITIONS_STRICT.put(
//            new MultiKey(
//                ServiceState.STOPPED, 
//                ServiceActivity.DESTROY), 
//            ServiceState.DESTROYED); 
//
//        TRANSITIONS_STRICT.put(
//            new MultiKey(
//                ServiceState.DESTROYED, 
//                ServiceActivity.INITIALIZE), 
//            ServiceState.INITIALIZED); 
//        
//        // Populate relaxed service state transitions
//        TRANSITIONS_RELAXED = new HashMap(6);
//        
//        TRANSITIONS_RELAXED.put(
//            ServiceActivity.INITIALIZE, 
//            ServiceState.INITIALIZED);
//        
//        TRANSITIONS_RELAXED.put(
//            ServiceActivity.START, 
//            ServiceState.RUNNING);
//        
//        TRANSITIONS_RELAXED.put(
//            ServiceActivity.STOP, 
//            ServiceState.STOPPED);
//        
//        TRANSITIONS_RELAXED.put(
//            ServiceActivity.SUSPEND, 
//            ServiceState.SUSPENDED);
//        
//        TRANSITIONS_RELAXED.put(
//            ServiceActivity.RESUME, 
//            ServiceState.RUNNING);
//        
//        TRANSITIONS_RELAXED.put(
//            ServiceActivity.DESTROY, 
//            ServiceState.DESTROYED);
    }
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * State machine that simulates transitions between the various service 
     * states.
     */
    private StateMachine machine_;
    
    /**
     * State of this service.
     */
    private ServiceState state_;

    /**
     * Previous state of this service.
     */
    private ServiceState previousState_;

    /**
     * Array of listeners interested in events that this service generates.
     */
    private ServiceListener[] listeners_;
     
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates an AbstractService with the default value for strict state
     * transitions.
     * 
     * @see #DEFAULT_STRICT
     */
    protected AbstractService()
    {
        machine_ = ServiceUtil.createStateMachine(this);
    }
    
    
    /**
     * Creates an AbstractService.
     * 
     * @param natures List of class natures.
     */
    protected AbstractService(Class[] natures)
    {
        machine_ = ServiceUtil.createStateMachine(natures);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the state.
     * 
     * @return ServiceState
     */
    public ServiceState getState()
    {
        return (ServiceState) machine_.getState();
    }

    
    /**
     * Sets the value of state.
     * 
     * @param state The state to set.
     */
    public void forceState(ServiceState state)
    {
        machine_.setBeginState(state);
        machine_.reset();
    }

    
    /**
     * Returns the previousState or null if a change in state has not occurred
     * yet.
     * 
     * @return ServiceState
     */
    public ServiceState getPreviousState()
    {
        return (ServiceState) machine_.getPreviousState();
    }
    
    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map configuration) throws ServiceException
    {
        machine_.transition(ServiceTransition.INITIALIZE);
    }
    
    
    /**
     * @see toolbox.util.service.Startable#start()
     */
    public void start() throws ServiceException
    {
        machine_.transition(ServiceTransition.START);
    }

    
    /**
     * @see toolbox.util.service.Suspendable#suspend()
     */
    public void suspend() throws ServiceException 
    {
        machine_.transition(ServiceTransition.SUSPEND);
    }

    
    /**
     * @see toolbox.util.service.Suspendable#resume()
     */
    public void resume() throws ServiceException
    {
        machine_.transition(ServiceTransition.RESUME);
    }

    
    /**
     * @see toolbox.util.service.Startable#stop()
     */
    public void stop() throws ServiceException
    {
        machine_.transition(ServiceTransition.STOP);
    }

    
    /**
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy() throws ServiceException
    {
        machine_.transition(ServiceTransition.DESTROY);
    }

    
    /**
     * @see toolbox.util.service.Startable#isRunning()
     */
    public boolean isRunning()
    {
        return machine_.getState() == ServiceState.RUNNING;
    }

    
    /**
     * @see toolbox.util.service.Suspendable#isSuspended()
     */
    public boolean isSuspended()
    {
        return machine_.getState() == ServiceState.SUSPENDED;
    }

    
    /**
     * @see toolbox.util.service.Service
     *      #addServiceListener(toolbox.util.service.ServiceListener)
     */
    public void addServiceListener(ServiceListener listener)
    {
        listeners_ = (ServiceListener[]) ArrayUtil.add(listeners_, listener);
    }

    
    /**
     * @see toolbox.util.service.Service
     *      #removeServiceListener(oolbox.util.service.ServiceListener)
     */
    public void removeServiceListener(ServiceListener listener)
    {
        listeners_ = (ServiceListener[]) ArrayUtil.remove(listeners_, listener);
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Notifies registered listeners that this service's state has changed.
     * 
     * @throws ServiceException on service related error.
     */
    protected void fireServiceStateChanged() throws ServiceException
    {
        for (int i = 0; i < listeners_.length; 
            listeners_[i++].serviceStateChanged(this));
    }

    
    /**
     * Answers the following question is strict state transitions are enabled.
     * Does the given activity result in a valid state transition from the 
     * current state to a target state?
     * 
     * @param activity Activity to check.
     * @throws IllegalStateException if the activity is not a valid from the 
     *         current state.
     */
    public void checkTransition(ServiceTransition activity) 
        throws IllegalStateException 
    {
        machine_.checkTransition(activity); 
    }
    
    
    /**
     * Validates that a service state transition is valid if the strict state
     * transition flag is enabled.
     * 
     * @param activity Service activity.
     * @throws ServiceException if the state transition is invalid.
     */
    public void transition(ServiceTransition activity) 
        throws ServiceException
    {
        machine_.transition(activity);
    }
}