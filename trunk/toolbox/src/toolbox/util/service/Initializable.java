package toolbox.util.service;

/**
 * An Initializable service can be initialized once and only once.
 */
public interface Initializable
{
    /**
     * Initializes the service.
     * 
     * @throws ServiceException if the service encounters problems initializing.
     */
    void initialize() throws ServiceException;


}