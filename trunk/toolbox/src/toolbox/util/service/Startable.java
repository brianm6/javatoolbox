package toolbox.util.service;

/**
 * A Startable service is one that can be started/stopped at will.
 */
public interface Startable
{
    //--------------------------------------------------------------------------
    // LifeCycle
    //--------------------------------------------------------------------------

    /**
     * Starts the service. Once a service it started, it may either be paused or
     * stopped.
     * 
     * @throws ServiceException if the service encounters problems starting up.
     */
    void start() throws ServiceException;



    /**
     * Stops the service. One a service is stopped, it may be restarted safely.
     * 
     * @throws ServiceException if the service encounters problems stopping.
     */
    void stop() throws ServiceException;


    //--------------------------------------------------------------------------
    // Monitoring
    //--------------------------------------------------------------------------

    /**
     * Returns true if the service is running, false otherwise.
     * 
     * @return boolean
     */
    boolean isRunning();
}