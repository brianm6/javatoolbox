package toolbox.util;

import java.util.Map;

import org.apache.log4j.Logger;

import toolbox.util.service.AbstractService;
import toolbox.util.service.ServiceException;

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
     * Shutdown flag.
     */
    private boolean keepRunning_;

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
        super(true);
        min_ = 0;
        max_ = 0;
        keepRunning_ = true;
    }

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
        System.gc();
        long currentFree = Runtime.getRuntime().freeMemory();
        long currentAlloc = Runtime.getRuntime().totalMemory();
        min_ = max_ = (currentAlloc - currentFree);        
        super.initialize(configuration);
    }
    
    
    /**
     * @see toolbox.util.service.AbstractService#start()
     */
    public void start() throws ServiceException
    {
        runner_ = new Thread(new Runner());
        runner_.start();
        super.start();
    }

    
    /**
     * Stops monitoring memory consumption.
     * 
     * @see toolbox.util.service.AbstractService#stop()
     */
    public void stop() throws ServiceException
    {
        keepRunning_ = false;
        
        try
        {
            runner_.join(10000);
        }
        catch (InterruptedException e)
        {
            throw new ServiceException(e);
        }
        
        super.stop();
    }
    
    
    /**
     * @see toolbox.util.service.AbstractService#destroy()
     */
    public void destroy() throws ServiceException
    {
        if (runner_ != null)
            ThreadUtil.stop(runner_, 1000);
        
        super.destroy();
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
            while (keepRunning_)
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