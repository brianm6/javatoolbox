package toolbox.util.invoker;

import toolbox.util.service.Destroyable;
import toolbox.util.service.ServiceException;

/**
 * Interface for invoking a method on an object or a {@link java.lang.Runnable}.
 */
public interface Invoker extends Destroyable
{
    /**
     * Invokes the run() method on class that implements Runnable.
     * 
     * @param invokable Object instance that implements Runnable.
     * @throws Exception on error.
     */
    void invoke(Runnable invokable) throws Exception;

    
    /**
     * Invokes the method on the given target object.
     * 
     * @param target Object to invoke method on.
     * @param method Name of method to invoke.
     * @param params Method's parameters.
     * @throws Exception on error.
     */
    void invoke(Object target, String method, Object[] params) throws Exception;
    
    
    /**
     * Shuts down the invoker.
     * 
     * @throws Exception on destroy error.
     */    
    void destroy() throws ServiceException;
}