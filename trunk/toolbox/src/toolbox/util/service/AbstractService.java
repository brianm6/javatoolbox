package toolbox.util.service;

import toolbox.util.ArrayUtil;

/**
 * Abstract base class for Service implementors.
 */
public abstract class AbstractService implements Service
{
    private static final int STATE_INIT     = 0;
    private static final int STATE_RUNNING  = 1;
    private static final int STATE_STOPPED  = 2;
    private static final int STATE_PAUSED   = 3;
    private static final int STATE_SHUTDOWN = 4;
    private static final int STATE_MAX      = 5;
    
    private static final String[] STATES; 

    static
    {
        STATES = new String[STATE_MAX];
        STATES[STATE_INIT]     = "initialized";
        STATES[STATE_RUNNING]  = "running";
        STATES[STATE_STOPPED]  = "stopped";
        STATES[STATE_PAUSED]   = "paused";
        STATES[STATE_SHUTDOWN] = "shutdown";
    }
    
    /**
     * State of the service.
     */
    private int state_;
    
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
        state_ = STATE_INIT;
        listeners_ = new ServiceListener[0];
    }

    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.plugin.netmeter.Service#start()
     */
    public void start() throws ServiceException
    {
        if (state_ == STATE_INIT || state_ == STATE_STOPPED)
        {    
            state_ = STATE_RUNNING;
            fireServiceStarted();
        }
        else
            throw new IllegalStateException(
                "Cannot start from current state of " + STATES[state_]);
    }

    
    /**
     * @see toolbox.plugin.netmeter.Service#stop()
     */
    public void stop() throws ServiceException
    {
        if (state_ == STATE_RUNNING)
        {
            state_ = STATE_STOPPED;
            fireServiceStopped();
        }
        else
            throw new IllegalStateException(
                "Cannot stop from current state of " + STATES[state_]);
    }

    
    /**
     * @see toolbox.plugin.netmeter.Service#pause()
     */
    public void pause() throws ServiceException
    {
        if (state_ == STATE_RUNNING)
        {
            state_ = STATE_PAUSED;
            fireServicePaused();
        }
        else
            throw new IllegalStateException(
                "Cannot pause from current state of " + STATES[state_]);
    }

    
    /**
     * @see toolbox.plugin.netmeter.Service#resume()
     */
    public void resume() throws ServiceException
    {
        if (state_ == STATE_PAUSED)
        {
            state_ = STATE_RUNNING;
            fireServiceResumed();
        }
        else
            throw new IllegalStateException(
                "Cannot resume from current state of " + STATES[state_]);
    }

    
    /**
     * @see toolbox.plugin.netmeter.Service#isRunning()
     */
    public boolean isRunning()
    {
        return state_ == STATE_RUNNING;
    }

    
    /**
     * @see toolbox.plugin.netmeter.Service#isPaused()
     */
    public boolean isPaused()
    {
        return state_ == STATE_PAUSED;
    }

    
    /**
     * @see toolbox.plugin.netmeter.Service#addServiceListener(
     *      toolbox.plugin.netmeter.ServiceListener)
     */
    public void addServiceListener(ServiceListener listener)
    {
        listeners_ = (ServiceListener[]) ArrayUtil.add(listeners_, listener);
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Notifies registered listeners that the service has started.
     * 
     * @throws ServiceException on service related error.
     */
    protected void fireServiceStarted() throws ServiceException
    {
        for (int i=0; i<listeners_.length;listeners_[i++].serviceStarted(this));
    }

    
    /**
     * Notifies registered listeners that the service has stopped.
     * 
     * @throws ServiceException on service related error.
     */
    protected void fireServiceStopped() throws ServiceException
    {
        for (int i=0; i<listeners_.length;listeners_[i++].serviceStopped(this));
    }

    
    /**
     * Notifies registered listeners that the service has been paused.
     * 
     * @throws ServiceException on service related error.
     */
    protected void fireServicePaused() throws ServiceException
    {
        for (int i=0; i<listeners_.length;listeners_[i++].servicePaused(this));
    }

    
    /**
     * Notifies registered listeners that the service has been resumed.
     * 
     * @throws ServiceException on service related error.
     */
    protected void fireServiceResumed() throws ServiceException
    {
        for (int i=0; i<listeners_.length;listeners_[i++].serviceResumed(this));
    }
}