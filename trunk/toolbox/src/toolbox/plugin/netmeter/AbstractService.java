package toolbox.plugin.netmeter;

import toolbox.util.ArrayUtil;

/**
 * Abstract base class for Service implementors.
 */
public abstract class AbstractService implements Service
{
    /**
     * Server listeners.
     */
    private ServiceListener[] listeners_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
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
    
    protected void fireServiceStarted() throws ServiceException
    {
        for (int i=0; i<listeners_.length;listeners_[i++].serviceStarted(this));
    }

    protected void fireServiceStopped() throws ServiceException
    {
        for (int i=0; i<listeners_.length;listeners_[i++].serviceStopped(this));
    }

    protected void fireServicePaused() throws ServiceException
    {
        for (int i=0; i<listeners_.length;listeners_[i++].servicePaused(this));
    }

    protected void fireServiceResumed() throws ServiceException
    {
        for (int i=0; i<listeners_.length;listeners_[i++].serviceResumed(this));
    }
}