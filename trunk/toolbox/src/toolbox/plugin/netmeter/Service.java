package toolbox.plugin.netmeter;

/**
 * An object that implements the Service interface adheres to basic lifecycle
 * management and (start/stop/resume) and a query interface expose current
 * state. 
 */
public interface Service
{
	/**
	 * Starts the service. Once a service it started, it may either be paused or
	 * stopped.
	 * 
	 * @throws ServiceException if the service encounters problems starting up.
	 */
    public void start() throws ServiceException;
    
    
    /**
     * Stops the service. One a service is stopped, it may be restarted safely.
     * 
     * @throws ServiceException if the service encounters problems stopping.
     */
    public void stop() throws ServiceException;
    
    
    /**
     * Pauses the service indefinitely. Once a service is paused, it may only
     * be resumed.
     * 
     * @throws ServiceException if the service encounters problems pausing.
     */
    public void pause() throws ServiceException;
    
    
    /**
     * Resumes the service and returns it to a running state. Once a service is
     * resumed, it may either be paused again or stopped.
     *  
     * @throws ServiceException if the service encounters problems resuming.
     */
    public void resume() throws ServiceException;
    
    
    /**
     * Returns true if the service is running, false otherwise.
     * 
     * @return boolean
     */
    public boolean isRunning();
    
    
    /**
     * Returns true if the service is paused, false otherwise.
     * 
     * @return boolean
     */
    public boolean isPaused();
}