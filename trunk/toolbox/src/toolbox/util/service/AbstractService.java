package toolbox.util.service;

import toolbox.util.ArrayUtil;

/**
 * Abstract base class for Service implementors.
 */
public abstract class AbstractService implements Service
{
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
        fireServiceStarted();
    }

    
    /**
     * @see toolbox.plugin.netmeter.Service#stop()
     */
    public void stop() throws ServiceException
    {
        fireServiceStopped();
    }

    
    /**
     * @see toolbox.plugin.netmeter.Service#pause()
     */
    public void pause() throws ServiceException
    {
        fireServicePaused();
    }

    
    /**
     * @see toolbox.plugin.netmeter.Service#resume()
     */
    public void resume() throws ServiceException
    {
        fireServiceResumed();
    }

    
    /**
     * @see toolbox.plugin.netmeter.Service#isRunning()
     */
    public boolean isRunning()
    {
        return false;
    }

    
    /**
     * @see toolbox.plugin.netmeter.Service#isPaused()
     */
    public boolean isPaused()
    {
        return false;
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