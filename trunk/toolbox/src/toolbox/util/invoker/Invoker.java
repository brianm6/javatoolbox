package toolbox.util.invoker;

/**
 * Interface for invoking a method on an object or a {@link java.lang.Runnable}
 */
public interface Invoker
{
    /**
     * Invokes run() on a Runnable
     * 
     * @param invokable Runnable 
     * @throws Exception on error
     */
    public void invoke(Runnable invokable) throws Exception;

    /**
     * Invokes the method on the given target object
     * 
     * @param target Object to invoke method on
     * @param method Name of method to invoke
     * @param params Method parameters
     * @throws Exception on error
     */
    public void invoke(Object target, String method, Object[] params)
        throws Exception;
    
    /**
     * Shuts down the invoker
     * 
     * @throws Exception on error
     */    
    public void shutdown() throws Exception;
}