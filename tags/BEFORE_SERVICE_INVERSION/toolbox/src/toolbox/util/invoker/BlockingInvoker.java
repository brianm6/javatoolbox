package toolbox.util.invoker;

import org.apache.commons.beanutils.MethodUtils;

import toolbox.util.service.ServiceException;

/**
 * Invoker that invokes a method synchronously and blocks until the method has
 * completed execution.
 */
public class BlockingInvoker implements Invoker
{
    //--------------------------------------------------------------------------
    // Invoker Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.invoker.Invoker#invoke(java.lang.Runnable)
     */
    public void invoke(Runnable invokable) throws Exception
    {
        invokable.run();
    }

    
    /**
     * @see toolbox.util.invoker.Invoker#invoke(java.lang.Object, 
     *      java.lang.String, java.lang.Object[])
     */
    public void invoke(Object target, String method, Object[] params)
        throws Exception
    {
        MethodUtils.invokeMethod(target, method, params);
    }
    
    //--------------------------------------------------------------------------
    // Destroyable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy() throws ServiceException
    {
        ; // No-op
    }
}