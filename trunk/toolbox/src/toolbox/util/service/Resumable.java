package toolbox.util.service;

/**
 * A service that can be paused/resumed.
 */              
public interface Resumable
{
    /**
     * Pauses the service indefinitely. Once a service is paused, it may only
     * be resumed.
     * 
     * @throws ServiceException if the service encounters problems pausing.
     */
    void pause() throws ServiceException;
    
    
    /**
     * Resumes the service and returns it to a running state. Once a service is
     * resumed, it may either be paused again or stopped.
     *  
     * @throws ServiceException if the service encounters problems resuming.
     */
    void resume() throws ServiceException;
    
    
    /**
     * Returns true if the service is paused, false otherwise.
     * 
     * @return boolean
     */
    boolean isPaused();
}