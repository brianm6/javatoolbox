package toolbox.util;

import java.util.Map;

import org.apache.log4j.Logger;

import toolbox.util.service.AbstractService;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceTransition;

/**
 * MemoryWatcher is a service that monitors memory in realtime and is able to 
 * provide the min and max memory consumption over a period of time.
 */
public class MemoryWatcher extends AbstractService
{
    private static final Logger logger_ = Logger.getLogger(MemoryWatcher.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Minimum memory consumption.
     */
    private long min_;
    
    /**
     * Maximum memory consumption.
     */
    private long max_;
    
    /**
     * Thread that the memory watcher runs on.
     */
    private Thread runner_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a MemoryWatcher.
     */
    public MemoryWatcher()
    {
        min_ = 0;
        max_ = 0;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the max.
     * 
     * @return long
     */
    public long getMax()
    {
        return max_;
    }
    
    
    /**
     * Returns the min.
     * 
     * @return long
     */
    public long getMin()
    {
        return min_;
    }
    
    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------

    /**
     * Resets this MemoryWatcher so it can be started again.
     * 
     * @see toolbox.util.service.AbstractService#initialize(java.util.Map)
     */
    public void initialize(Map configuration) throws ServiceException
    {
        checkTransition(ServiceTransition.INITIALIZE);
        
        System.gc();
        long currentFree = Runtime.getRuntime().freeMemory();
        long currentAlloc = Runtime.getRuntime().totalMemory();
        min_ = max_ = (currentAlloc - currentFree);
        
        transition(ServiceTransition.INITIALIZE);
    }
    
    
    /**
     * @see toolbox.util.service.AbstractService#start()
     */
    public void start() throws ServiceException
    {
        checkTransition(ServiceTransition.START);
        
        runner_ = new Thread(new Runner());
        runner_.start();
        
        transition(ServiceTransition.START);
    }

    
    /**
     * Stops monitoring memory consumption.
     * 
     * @see toolbox.util.service.AbstractService#stop()
     */
    public void stop() throws ServiceException
    {
        checkTransition(ServiceTransition.STOP);
        transition(ServiceTransition.STOP);
        
        try
        {
            runner_.join(10000);
        }
        catch (InterruptedException e)
        {
            throw new ServiceException(e);
        }
    }
    
    
    /**
     * @see toolbox.util.service.AbstractService#destroy()
     */
    public void destroy() throws ServiceException
    {
        checkTransition(ServiceTransition.DESTROY);
        
        if (runner_ != null)
            ThreadUtil.stop(runner_, 1000);
        
        transition(ServiceTransition.DESTROY);
    }
    
    //--------------------------------------------------------------------------
    // Runnable Interface
    //--------------------------------------------------------------------------
    
    /**
     * Samples memory consumption until told to stop.
     */
    class Runner implements Runnable 
    {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            while (isRunning())
            {
                long currentFree = Runtime.getRuntime().freeMemory();
                long currentAlloc = Runtime.getRuntime().totalMemory();
                long used = currentAlloc - currentFree;
    
                if (used < min_)
                    min_ = used;
    
                if (used > max_)
                    max_ = used;
    
                ThreadUtil.sleep(100);
            }
        }
    }
}