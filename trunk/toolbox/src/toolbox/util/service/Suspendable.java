package toolbox.util.service;

/**
 * Implemented by services which can suspend and resume operation.
 */
public interface Suspendable extends Service {
    
    /**
     * Suspends this service.
     * 
     * @throws IllegalStateException if the service cannot be suspended from its
     *         current state.
     * @throws ServiceException if the service encounters problems suspending
     *         itself.
     */
    void suspend() throws IllegalStateException, ServiceException;


    /**
     * Resumes the service.
     * 
     * @throws IllegalStateException if the service cannot be resumed from its
     *         current state.
     * @throws ServiceException if the service encounters problems resuming
     *         itself.
     */
    void resume() throws IllegalStateException, ServiceException;


    /**
     * Returns true if the service is suspended, false otherwise.
     * 
     * @return boolean
     */
    boolean isSuspended();
}