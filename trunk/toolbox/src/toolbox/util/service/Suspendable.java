package toolbox.util.service;

/**
 * A Suspendable service can suspend and resume operation at will.
 */              
public interface Suspendable
{
    /**
     * Suspends the service indefinitely. Once a service is suspended, it has to
     * be resumed before it can be stopped.
     * 
     * @throws ServiceException if the service encounters problems suspending
     *         itself.
     */
    void suspend() throws ServiceException;
    
    
    /**
     * Resumes the service and returns it to a running state. Once a service is
     * resumed, it may either be suspended again or stopped.
     *  
     * @throws ServiceException if the service encounters problems resuming
     *         itself.
     */
    void resume() throws ServiceException;
    
    
    /**
     * Returns true if the service is suspended, false otherwise.
     * 
     * @return boolean
     */
    boolean isSuspended();
}