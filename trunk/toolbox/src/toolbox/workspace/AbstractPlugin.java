package toolbox.workspace;

import toolbox.util.ArrayUtil;
import toolbox.util.service.AbstractService;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceListener;
import toolbox.util.service.ServiceState;
import toolbox.util.service.ServiceTransition;
import toolbox.util.statemachine.StateMachine;
import toolbox.workspace.prefs.IConfigurator;

/**
 * Abstract base class for IPlugin implementors.
 */
public abstract class AbstractPlugin implements IPlugin
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * State machine that simulates transitions between the various service 
     * states.
     */
    private StateMachine machine_;
    
    /**
     * Array of listeners interested in events that this service generates.
     */
    private ServiceListener[] listeners_;
     
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates an AbstractPlugin.
     */
    protected AbstractPlugin()
    {
        machine_ = AbstractService.createStateMachine(this);
    }
    
    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPlugin#getConfigurator()
     */
    public IConfigurator getConfigurator()
    {
        // TODO: Remove once all plugins implmenments this method.
        return null;
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
//    public void forceState(ServiceState state)
//    {
//        machine_.setBeginState(state);
//        machine_.reset();
//    }

    
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
    public void transition(ServiceTransition activity) throws ServiceException
    {
        machine_.transition(activity);
    }
}