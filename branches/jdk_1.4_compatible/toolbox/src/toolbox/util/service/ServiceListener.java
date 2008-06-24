package toolbox.util.service;

/**
 * The ServiceListener interface provides notification of service related
 * events.
 */
public interface ServiceListener {
    
    /**
     * Notification that the given services state has changed.
     * 
     * @param service Service whose state has changed.
     * @throws ServiceException on error.
     */
    void serviceStateChanged(Service service) throws ServiceException;
}