package toolbox.util.service;

/**
 * Destroyable service.
 * 
 * @see toolbox.util.service.Initializable
 */
public interface Destroyable extends Service
{
    /**
     * Destroys this service.
     * 
     * @throws IllegalStateException on invalid service state.
     * @throws ServiceException on destroy error.
     */
    void destroy() throws IllegalStateException, ServiceException;
}
