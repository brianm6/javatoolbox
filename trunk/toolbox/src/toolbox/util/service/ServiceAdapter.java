package toolbox.util.service;

import java.util.Map;

/**
 * ServiceAdapter
 */
public class ServiceAdapter implements Service 
{
    /**
     * Creates a ServiceAdapter.  
     */
    public ServiceAdapter() {
    }
    
    /**
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState() 
    {
        return null;
    }
    
    /**
     * @see toolbox.util.service.Service#addServiceListener(
     *      toolbox.util.service.ServiceListener)
     */
    public void addServiceListener(ServiceListener listener) 
    {
    }
    
    /**
     * @see toolbox.util.service.Service#removeServiceListener(
     *      toolbox.util.service.ServiceListener)
     */
    public void removeServiceListener(ServiceListener listener) 
    {
    }
    
    /**
     * @see toolbox.util.service.Service#setStrict(boolean)
     */
    public void setStrict(boolean b) 
    {
    }
    
    /**
     * @see toolbox.util.service.Service#isStrict()
     */
    public boolean isStrict() 
    {
        return false;
    }
    
    /**
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map configuration) throws ServiceException 
    {
    }
    
    /**
     * @see toolbox.util.service.Startable#start()
     */
    public void start() throws ServiceException 
    {
    }
    
    /**
     * @see toolbox.util.service.Startable#stop()
     */
    public void stop() throws ServiceException 
    {
    }
    
    /**
     * @see toolbox.util.service.Startable#isRunning()
     */
    public boolean isRunning() 
    {
        return false;
    }
    
    /**
     * @see toolbox.util.service.Suspendable#suspend()
     */
    public void suspend() throws ServiceException 
    {
    }
    
    /**
     * @see toolbox.util.service.Suspendable#resume()
     */
    public void resume() throws ServiceException 
    {
    }
    
    /**
     * @see toolbox.util.service.Suspendable#isSuspended()
     */
    public boolean isSuspended() 
    {
        return false;
    }
    
    /**
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy() throws ServiceException 
    {
    }
}