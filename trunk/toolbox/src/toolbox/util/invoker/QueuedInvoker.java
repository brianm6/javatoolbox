package toolbox.util.invoker;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.log4j.Logger;

import toolbox.util.concurrent.BlockingQueue;

/**
 * Invoker that queues up invocations requests on a queue and executes them in
 * a serial manner. The invoke() method does not wait for the execution to 
 * complete. Instead, invoke() returns immediately after the request is placed
 * on the queue. The size the queue is not bounded (maybe later).
 */
public class QueuedInvoker implements Invoker
{
    private static final Logger logger_ = 
        Logger.getLogger(QueuedInvoker.class);
    
    /** Work queue */
    private BlockingQueue queue_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a queued invoker
     */
    public QueuedInvoker()
    {
        queue_ = new BlockingQueue();

        // Creates the consumer thread and starts it    
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                while (true)
                {
                    try
                    {
                        Runnable r = (Runnable) queue_.pull();
                        r.run();
                    }
                    catch (InterruptedException ie)
                    {
                        logger_.warn("run", ie);
                    }
                }
            }
        }, "QueuedInvoker");

        // Start the consumer
        t.start();
    }

    //--------------------------------------------------------------------------
    // Invoker Interface
    //--------------------------------------------------------------------------

    public void invoke(Runnable invokable) throws Exception
    {
        queue_.push(invokable);
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
                    logger_.error("run", e);
                }
            }
        });
    }
}
