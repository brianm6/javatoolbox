package toolbox.util.service;

/**
 * Destroyable is the terminal state for a Service that has been destroyed and
 * is no longer usable.
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
