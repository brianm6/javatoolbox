package toolbox.util.service;

import toolbox.util.ArrayUtil;

/**
 * Abstract base class for Service implementors.
 */
public abstract class AbstractService implements Service
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
//    private static final int STATE_NONE     = -1;
//    private static final int STATE_INIT     = 0;
//    private static final int STATE_RUNNING  = 1;
//    private static final int STATE_STOPPED  = 2;
//    private static final int STATE_PAUSED   = 3;
//    private static final int STATE_SHUTDOWN = 4;
//    private static final int STATE_MAX      = 5;
//    
//    private static final String[] STATES; 
//
//    static
//    {
//        STATES = new String[STATE_MAX];
//        STATES[STATE_INIT]     = "initialized";
//        STATES[STATE_RUNNING]  = "running";
//        STATES[STATE_STOPPED]  = "stopped";
//        STATES[STATE_PAUSED]   = "paused";
//        STATES[STATE_SHUTDOWN] = "shutdown";
//    }

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * State of the service.
     */
    private ServiceState state_;
    
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
    public AbstractService()
    {
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
        state_ = state;
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
        fireServiceChanged();
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
            fireServiceChanged();
        }
        else
            throw new IllegalStateException(
                "Cannot start service from the current state of " + getState());
    }

    
    /**
     * @see toolbox.util.service.Service#stop()
     */
    public void stop() throws ServiceException
    {
        if (getState() == ServiceState.RUNNING)
        {
            setState(ServiceState.STOPPED);
            fireServiceChanged();
        }
        else
            throw new IllegalStateException(
                "Cannot stop service from the current state of " + getState());
    }

    
    /**
     * @see toolbox.util.service.Service#suspend()
     */
    public void suspend() throws ServiceException
    {
        if (getState() == ServiceState.RUNNING)
        {
            setState(ServiceState.SUSPENDED);
            fireServiceChanged();
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
            fireServiceChanged();
        }
        else
            throw new IllegalStateException(
                "Cannot resume service from the current state of " + getState());
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
        return state_ == ServiceState.SUSPENDED;
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
    protected void fireServiceChanged() throws ServiceException
    {
        for (int i = 0; i < listeners_.length; 
            listeners_[i++].serviceChanged(this));
    }

    
//    /**
//     * Notifies registered listeners that the service has started.
//     * 
//     * @throws ServiceException on service related error.
//     */
//    protected void fireServiceStarted() throws ServiceException
//    {
//        for (int i = 0; i < listeners_.length; 
//            listeners_[i++].serviceStarted(this));
//    }
//
//    
//    /**
//     * Notifies registered listeners that the service has stopped.
//     * 
//     * @throws ServiceException on service related error.
//     */
//    protected void fireServiceStopped() throws ServiceException
//    {
//        for (int i = 0; i < listeners_.length;
//            listeners_[i++].serviceStopped(this));
//    }
//
//    
//    /**
//     * Notifies registered listeners that the service has been paused.
//     * 
//     * @throws ServiceException on service related error.
//     */
//    protected void fireServicePaused() throws ServiceException
//    {
//        for (int i = 0; i < listeners_.length;
//            listeners_[i++].servicePaused(this));
//    }
//
//    
//    /**
//     * Notifies registered listeners that the service has been resumed.
//     * 
//     * @throws ServiceException on service related error.
//     */
//    protected void fireServiceResumed() throws ServiceException
//    {
//        for (int i = 0; i < listeners_.length;
//            listeners_[i++].serviceResumed(this));
//    }

}