package toolbox.util.service;

import toolbox.util.ArrayUtil;

/**
 * Abstract base class for Service implementors.
 */
public abstract class AbstractService implements Service
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * State of the service.
     */
    private ServiceState state_;

    /**
     * Previous state of the service.
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
     * Default constructor.
     */
    protected AbstractService()
    {
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
     * @see toolbox.util.service.Service#initialize()
     */
    public void initialize() throws ServiceException
    {
        setState(ServiceState.INITIALIZED);
        fireServiceStateChanged();
    }
    
    
    /**
     * @see toolbox.util.service.Service#start()
     */
    public void start() throws ServiceException
    {
        if (getState() == ServiceState.INITIALIZED || 
            getState() == ServiceState.STOPPED)
        {    
            setState(ServiceState.RUNNING);
            fireServiceStateChanged();
        }
        else
            throw new IllegalStateException(
                "Cannot start service from the current state of " + getState());
    }

    
    /**
     * @see toolbox.util.service.Service#suspend()
     */
    public void suspend() throws ServiceException
    {
        if (getState() == ServiceState.RUNNING)
        {
            setState(ServiceState.SUSPENDED);
            fireServiceStateChanged();
        }
        else
            throw new IllegalStateException(
                "Cannot suspend from the current state of " + getState());
    }

    
    /**
     * @see toolbox.util.service.Service#resume()
     */
    public void resume() throws ServiceException
    {
        if (getState() == ServiceState.SUSPENDED)
        {
            setState(ServiceState.RUNNING);
            fireServiceStateChanged();
        }
        else
            throw new IllegalStateException(
                "Cannot resume service from the current state of " + getState());
    }

    
    /**
     * @see toolbox.util.service.Service#stop()
     */
    public void stop() throws ServiceException
    {
        if (getState() == ServiceState.RUNNING)
        {
            setState(ServiceState.STOPPED);
            fireServiceStateChanged();
        }
        else
            throw new IllegalStateException(
                "Cannot stop service from the current state of " + getState());
    }

    
    /**
     * @see toolbox.util.service.Service#destroy()
     */
    public void destroy() throws ServiceException
    {
        if (getState() == ServiceState.STOPPED)
        {
            setState(ServiceState.DESTROYED);
            fireServiceStateChanged();
        }
        else
            throw new IllegalStateException(
                "Cannot stop service from the current state of " + getState());
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
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Notifies registered listeners that the service has been initialized.
     * 
     * @throws ServiceException on service related error.
     */
    protected void fireServiceStateChanged() throws ServiceException
    {
        for (int i = 0; i < listeners_.length; 
            listeners_[i++].serviceStateChanged(this));
    }
}