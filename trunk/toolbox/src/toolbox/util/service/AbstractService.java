package toolbox.util.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.keyvalue.MultiKey;

import toolbox.util.ArrayUtil;

/**
 * Abstract base class for Service implementors.
 */
public abstract class AbstractService implements Service
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Strict state transitions are turned off by default.
     */
    private static final boolean DEFAULT_STRICT = false;
    
    /**
     * Mapping of the strict set of transitions.
     * <pre>  
     * Key   = (ServiceState beginState, ServiceActivity activity)
     * Value = (ServiceState endState)
     * </pre>
     */
    private static final Map TRANSITIONS_STRICT;
    
    /**
     * Mapping of the relaxed or unchecked set of transitions.
     * <pre>  
     * Key   = (ServiceActivity activity)
     * Value = (ServiceState endState)
     * </pre>
     */
    private static final Map TRANSITIONS_RELAXED;
    
    //--------------------------------------------------------------------------
    // Static Initializers
    //--------------------------------------------------------------------------
    
    static 
    {
        // Populate strict service state transitions
        TRANSITIONS_STRICT = new HashMap(8);

        TRANSITIONS_STRICT.put(
            new MultiKey(
                ServiceState.UNINITIALIZED, 
                ServiceActivity.INITIALIZE), 
            ServiceState.INITIALIZED);

        TRANSITIONS_STRICT.put(
            new MultiKey(
                ServiceState.INITIALIZED, 
                ServiceActivity.START), 
            ServiceState.RUNNING);
        
        TRANSITIONS_STRICT.put(
            new MultiKey(
                ServiceState.RUNNING, 
                ServiceActivity.STOP), 
            ServiceState.STOPPED); 
        
        TRANSITIONS_STRICT.put(
            new MultiKey(
                ServiceState.RUNNING, 
                ServiceActivity.SUSPEND), 
            ServiceState.SUSPENDED); 
        
        TRANSITIONS_STRICT.put(
             new MultiKey(
                ServiceState.SUSPENDED, 
                ServiceActivity.RESUME), 
             ServiceState.RUNNING);

        TRANSITIONS_STRICT.put(
                new MultiKey(
                   ServiceState.SUSPENDED, 
                   ServiceActivity.START), 
                ServiceState.RUNNING);
        
        TRANSITIONS_STRICT.put(
            new MultiKey(
                ServiceState.SUSPENDED, 
                ServiceActivity.STOP), 
            ServiceState.STOPPED); 
        
        TRANSITIONS_STRICT.put(
            new MultiKey(
                ServiceState.STOPPED, 
                ServiceActivity.START), 
            ServiceState.RUNNING); 
        
        TRANSITIONS_STRICT.put(
            new MultiKey(
                ServiceState.STOPPED, 
                ServiceActivity.DESTROY), 
            ServiceState.DESTROYED); 

        TRANSITIONS_STRICT.put(
            new MultiKey(
                ServiceState.DESTROYED, 
                ServiceActivity.INITIALIZE), 
            ServiceState.INITIALIZED); 
        
        // Populate relaxed service state transitions
        TRANSITIONS_RELAXED = new HashMap(6);
        
        TRANSITIONS_RELAXED.put(
            ServiceActivity.INITIALIZE, 
            ServiceState.INITIALIZED);
        
        TRANSITIONS_RELAXED.put(
            ServiceActivity.START, 
            ServiceState.RUNNING);
        
        TRANSITIONS_RELAXED.put(
            ServiceActivity.STOP, 
            ServiceState.STOPPED);
        
        TRANSITIONS_RELAXED.put(
            ServiceActivity.SUSPEND, 
            ServiceState.SUSPENDED);
        
        TRANSITIONS_RELAXED.put(
            ServiceActivity.RESUME, 
            ServiceState.RUNNING);
        
        TRANSITIONS_RELAXED.put(
            ServiceActivity.DESTROY, 
            ServiceState.DESTROYED);
    }
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

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
     
    /**
     * If true, this service will observe strict state transitions, otherwise
     * relaxed state transitions will apply.
     * 
     * @see #TRANSITIONS_RELAXED
     * @see #TRANSITIONS_STRICT
     */
    private boolean strict_;

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
        this(DEFAULT_STRICT);
    }

    
    /**
     * Creates an AbstractService.
     * 
     * @param strict True for strict state transitions, false otherwise.
     */
    protected AbstractService(boolean strict)
    {
        setStrict(strict);
        setPreviousState(ServiceState.UNINITIALIZED);
        setState(ServiceState.UNINITIALIZED);
        listeners_ = new ServiceListener[0];
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
        return state_;
    }

    
    /**
     * Sets the value of state.
     * 
     * @param state The state to set.
     */
    public void setState(ServiceState state)
    {
        setPreviousState(getState());
        state_ = state;
    }

    
    /**
     * Returns the previousState.
     * 
     * @return ServiceState
     */
    public ServiceState getPreviousState()
    {
        return previousState_;
    }
    
    
    /**
     * Sets the previousState.
     * 
     * @param previousState The previousState to set.
     */
    public void setPreviousState(ServiceState previousState)
    {
        previousState_ = previousState;
    }
    
    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Service#initialize(Map)
     */
    public void initialize(Map configuration) throws ServiceException
    {
        transition(ServiceActivity.INITIALIZE);
    }
    
    
    /**
     * @see toolbox.util.service.Service#start()
     */
    public void start() throws ServiceException
    {
        transition(ServiceActivity.START);
    }

    
    /**
     * @see toolbox.util.service.Service#suspend()
     */
    public void suspend() throws ServiceException 
    {
        transition(ServiceActivity.SUSPEND);
    }

    
    /**
     * @see toolbox.util.service.Service#resume()
     */
    public void resume() throws ServiceException
    {
        transition(ServiceActivity.RESUME);
    }

    
    /**
     * @see toolbox.util.service.Service#stop()
     */
    public void stop() throws ServiceException
    {
        transition(ServiceActivity.STOP);
    }

    
    /**
     * @see toolbox.util.service.Service#destroy()
     */
    public void destroy() throws ServiceException
    {
        transition(ServiceActivity.DESTROY);
    }

    
    /**
     * @see toolbox.util.service.Service#isRunning()
     */
    public boolean isRunning()
    {
        return getState() == ServiceState.RUNNING;
    }

    
    /**
     * @see toolbox.util.service.Service#isSuspended()
     */
    public boolean isSuspended()
    {
        return getState() == ServiceState.SUSPENDED;
    }

    
    /**
     * @see toolbox.util.service.Service#addServiceListener(
     *      toolbox.util.service.ServiceListener)
     */
    public void addServiceListener(ServiceListener listener)
    {
        listeners_ = (ServiceListener[]) ArrayUtil.add(listeners_, listener);
    }

    
    /**
     * @see toolbox.util.service.Service#removeServiceListener(
     *      toolbox.util.service.ServiceListener)
     */
    public void removeServiceListener(ServiceListener listener)
    {
        listeners_ = (ServiceListener[]) ArrayUtil.remove(listeners_, listener);
    }
    
    
    /**
     * @see toolbox.util.service.Service#isStrict()
     */
    public boolean isStrict() 
    {
        return strict_;
    }
    
    
    /**
     * @see toolbox.util.service.Service#setStrict(boolean)
     */
    public void setStrict(boolean b) 
    {
        strict_ = b;
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
     * @throws ServiceException if the activity is not a valid from the current
     *         state.
     */
    protected void checkTransition(ServiceActivity activity) 
        throws ServiceException
    {
        if (isStrict()) 
        {
            ServiceState nextState = (ServiceState)
                TRANSITIONS_STRICT.get(new MultiKey(getState(), activity));

            if (nextState == null)
            {
                throw new ServiceException(
                    "Invalid service state transition from " 
                    + getState()
                    + " state with the "
                    + activity 
                    + " activity.");
            }
        }
    }
    
    
    /**
     * Validates that a service state transition is valid if the strict state
     * transition flag is enabled.
     * 
     * @param activity Service activity.
     * @throws ServiceException if the state transition is invalid.
     */
    protected void transition(ServiceActivity activity) throws ServiceException
    {
        checkTransition(activity);
        setState((ServiceState) TRANSITIONS_RELAXED.get(activity));
        fireServiceStateChanged();
    }
}