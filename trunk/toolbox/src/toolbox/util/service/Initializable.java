package toolbox.util.service;

/**
 * Initializable is responsible for _____.
 */
public interface Initializable
{
    //--------------------------------------------------------------------------
    // LifeCycle
    //--------------------------------------------------------------------------

    /**
     * Initializes the service.
     * 
     * @throws ServiceException if the service encounters problems initializing.
     */
    void initialize() throws ServiceException;


}