package toolbox.util.service;

/**
 * ServiceException is thrown for Service related failures.
 */
public class ServiceException extends Exception
{
    /**
     * Creates a ServiceException.
     */
    public ServiceException()
    {
    }

    
    /**
     * Creates a ServiceException.
     */
    public ServiceException(String message)
    {
        super(message);
    }
    
    
    /**
     * Creates a ServiceException.
     * 
     * @param t Originating exception.
     */
    public ServiceException(Throwable t)
    {
        super(t);
    }
}