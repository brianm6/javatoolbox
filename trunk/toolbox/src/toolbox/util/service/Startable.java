package toolbox.util.service;

/**
 * Startable service.
 */
public interface Startable extends Service
{
    /**
     * Starts the service.
     * 
     * @throws IllegalStateException if the service cannot be started from its
     *         current state.
     * @throws ServiceException if the service encounters problems starting 
     *         itself.
     */
    void start() throws IllegalStateException, ServiceException;


    /**
     * Stops the service. One a service is stopped, it may be started or 
     * detroyed.

     * @throws IllegalStateException if the service cannot be stopped from its
     *         current state.
     * @throws ServiceException if the service encounters problems stopping
     *         itself.
     */
    void stop() throws IllegalStateException, ServiceException;


    /**
     * Returns true if the service has been started, false otherwise.
     * 
     * @return boolean
     */
    boolean isRunning();
}