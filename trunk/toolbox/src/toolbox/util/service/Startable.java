package toolbox.util.service;

/**
 * A Startable service can be started and stopped at will.
 */
public interface Startable extends ServiceNature
{
    /**
     * Starts the service. Once a service it started, it may either be suspended
     * or stopped.
     * 
     * @throws ServiceException if the service encounters problems starting 
     *         itself.
     */
    void start() throws ServiceException;


    /**
     * Stops the service. One a service is stopped, it may be started or 
     * detroyed.
     * 
     * @throws ServiceException if the service encounters problems stopping
     *         itself.
     */
    void stop() throws ServiceException;


    /**
     * Returns true if the service has been started, false otherwise.
     * 
     * @return boolean
     */
    boolean isRunning();
}