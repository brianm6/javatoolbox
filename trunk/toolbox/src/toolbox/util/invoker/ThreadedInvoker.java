package toolbox.util.invoker;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.log4j.Logger;

/**
 * Invoker that executes a method on a separate thread resulting in 
 * asynchronous execution behavior.
 */
public class ThreadedInvoker implements Invoker
{
    private static final Logger logger_ =
        Logger.getLogger(ThreadedInvoker.class);

    //----------------------------------------------------------------------------
    // Invoker Interface
    //----------------------------------------------------------------------------

    public void invoke(Runnable invokable) throws Exception
    {
        new Thread(invokable).start();
    }

    public void invoke(
        final Object target, final String method, final Object[] params) 
        throws Exception
    {
        invoke(new Runnable()
        {
            public void run()
            {
                try
                {
                    MethodUtils.invokeMethod(target, method, params);
                }
                catch (Exception e)
                {
                    logger_.error(logger_, e);
                }
            }
        });
    }
}