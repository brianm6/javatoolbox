package toolbox.plugin.netmeter;

/**
 * The ServiceListener interface provides notification of service related 
 * events.
 */
public interface ServiceListener
{
    /**
     * Notification that the service has started running.
     * 
     * @param service Service that started.
     * @throws ServiceException on error.
     */
    void serviceStarted(Service service) throws ServiceException;
    
    
    /**
     * Notification that the service has stopped running.
     *  
     * @param service Service that stopped.
     * @throws ServiceException on error.
     */
    void serviceStopped(Service service) throws ServiceException;
    
    
    /**
     * Notification that the server has been paused.
     * 
     * @param service Service that was paused.
     * @throws ServiceException on error.
     */
    void servicePaused(Service service) throws ServiceException;
    
    
    /**
     * Notification that the service has resumed.
     * 
     * @param service Service that was resumed.
     * @throws ServiceException on error.
     */
    void serviceResumed(Service service) throws ServiceException;
}