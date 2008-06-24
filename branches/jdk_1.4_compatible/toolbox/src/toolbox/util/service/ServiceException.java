package toolbox.util.service;

/**
 * ServiceException is thrown in the event of Service related failures.
 */
public class ServiceException extends RuntimeException {

    /**
     * Creates a ServiceException.
     */
    public ServiceException() {
    }


    /**
     * Creates a ServiceException.
     * 
     * @param message Error message.
     */
    public ServiceException(String message) {
        super(message);
    }


    /**
     * Creates a ServiceException.
     * 
     * @param t Originating exception.
     */
    public ServiceException(Throwable t) {
        super(t);
    }
}