package toolbox.util.invoker;

import org.apache.commons.beanutils.MethodUtils;

/**
 * Invoker that invokes a method synchronously and blocks until the method has
 * completed execution.
 */
public class BlockingInvoker implements Invoker
{
    //--------------------------------------------------------------------------
    // Invoker Interface
    //--------------------------------------------------------------------------

    public void invoke(Runnable invokable) throws Exception
    {
        invokable.run();
    }

    public void invoke(Object target, String method, Object[] params)
        throws Exception
    {
        MethodUtils.invokeMethod(target, method, params);
    }
    
    public void shutdown() throws Exception
    {
    }
}