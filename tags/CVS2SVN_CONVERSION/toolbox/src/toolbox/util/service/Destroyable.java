package toolbox.util.service;

/**
 * Implemented by services which are destroyable.
 * 
 * @see toolbox.util.service.Initializable
 * @see toolbox.util.service.ServiceState
 */
public interface Destroyable extends Service {

    /**
     * Destroys this service.
     * 
     * @throws IllegalStateException on invalid service state.
     * @throws ServiceException on destroy error.
     */
    void destroy() throws IllegalStateException, ServiceException;

    
    /**
     * Returns true if this service is in the {@link ServiceState#DESTROYED}
     * state. False otherwise.
     * 
     * @return boolean
     */
    boolean isDestroyed();
}