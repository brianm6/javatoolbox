package toolbox.workspace;

import toolbox.util.service.ObservableService;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceListener;
import toolbox.util.service.ServiceNotifier;
import toolbox.util.service.ServiceState;
import toolbox.util.service.ServiceTransition;
import toolbox.util.service.ServiceUtil;
import toolbox.util.statemachine.StateMachine;
import toolbox.workspace.prefs.IConfigurator;

/**
 * Abstract base class for IPlugins.
 */
public abstract class AbstractPlugin implements IPlugin, ObservableService
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
     * Handles notification of service state changes.
     */
    private ServiceNotifier notifier_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates an AbstractPlugin.
     */
    protected AbstractPlugin()
    {
        machine_ = ServiceUtil.createStateMachine(this);
        notifier_ = new ServiceNotifier(this);
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
    // ObservableService Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.ObservableService#getStateMachine()
     */
    public StateMachine getStateMachine()
    {
        return machine_;
    }
    
    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState()
    {
        return (ServiceState) machine_.getState();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
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

    
    /**
     * @param listener
     */
    public void addServiceListener(ServiceListener listener)
    {
        notifier_.addServiceListener(listener);
    }

    
    /**
     * @param listener
     */
    public void removeServiceListener(ServiceListener listener)
    {
        notifier_.removeServiceListener(listener);
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

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