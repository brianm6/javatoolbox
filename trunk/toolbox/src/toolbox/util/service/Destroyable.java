package toolbox.util.service;

/**
 * A Destroyable service can be destroyed once and only once. The destroyed
 * state is a terminal state. A service can only be destroyed if in the Stopped
 * state.
 */
public interface Destroyable
{
    /**
     * Destroys this service.
     * 
     * @throws ServiceException on destroy error.
     */
    void destroy() throws ServiceException;
}
