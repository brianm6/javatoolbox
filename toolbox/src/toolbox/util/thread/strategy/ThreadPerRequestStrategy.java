package toolbox.util.thread.strategy;

import toolbox.util.thread.IThreadable;
import toolbox.util.thread.ReturnValue;

/**
 * ThreadPerRequestStrategy implements a thread-per-request strategy that 
 * processes every request in a separate thread.  Although the most flexible 
 * strategy, thread creation is costly and does not scale well to bursty 
 * architectures in which request activity occurs in bursts.
 */
public class ThreadPerRequestStrategy extends ThreadedDispatcherStrategy
{
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Services the request in new thread and records the result.
     *
     * @param request Request to dispatch.
     * @param result Holds the request result.
     */
    public void service(IThreadable request, ReturnValue result)
    {
        createThread(new ThreadPerRequestRunnable(request, result));
    }

    //--------------------------------------------------------------------------
    // ThreadPerRequestRunnable
    //--------------------------------------------------------------------------
    
    /**
     * Strategy specific runnable for thread-per-request strategy.
     */
    class ThreadPerRequestRunnable implements Runnable
    {
        /**
         * Request.
         */
        private IThreadable request_;
        
        /**
         * Return value.
         */
        private ReturnValue result_;

        /**
         * Creates a new runnable that will process request.
         *
         * @param request Request to process.
         * @param result Holder for the return value.
         */
        public ThreadPerRequestRunnable(IThreadable request, ReturnValue result)
        {
            request_ = request;
            result_ = result;
        }


        /**
         * Process my encapsulated request and update the return value.
         */
        public void run()
        {
            try
            {
                setStarted(result_);
                setResult(result_, process(request_));
            }
            catch (Exception e)
            {
                setResult(result_, e);
            }

            request_ = null;
            result_ = null;
        }
    }
}