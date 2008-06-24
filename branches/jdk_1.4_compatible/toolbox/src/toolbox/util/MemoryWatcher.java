package toolbox.util;

import java.util.Map;

import org.apache.log4j.Logger;

import toolbox.util.service.Destroyable;
import toolbox.util.service.Initializable;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceState;
import toolbox.util.service.ServiceTransition;
import toolbox.util.service.ServiceUtil;
import toolbox.util.service.Startable;
import toolbox.util.statemachine.StateMachine;

/**
 * MemoryWatcher is a service that monitors memory in realtime and is able to 
 * provide the min and max memory consumption over a period of time.
 */
public class MemoryWatcher implements Initializable, Startable, Destroyable 
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
    
    /**
     * State machine for this memory watcher.
     */
    private StateMachine machine_;
    
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
        machine_ = ServiceUtil.createStateMachine(this);
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
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState()
    {
        return (ServiceState) machine_.getState();
    }
    
    //--------------------------------------------------------------------------
    // Initializable Interface
    //--------------------------------------------------------------------------

    /**
     * Resets this MemoryWatcher so it can be started again.
     * 
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map configuration) throws ServiceException
    {
        machine_.checkTransition(ServiceTransition.INITIALIZE);
        
        System.gc();
        long currentFree = Runtime.getRuntime().freeMemory();
        long currentAlloc = Runtime.getRuntime().totalMemory();
        min_ = max_ = (currentAlloc - currentFree);
        
        machine_.transition(ServiceTransition.INITIALIZE);
    }
    
    //--------------------------------------------------------------------------
    // Startable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Startable#start()
     */
    public void start() throws ServiceException
    {
        machine_.checkTransition(ServiceTransition.START);
        
        runner_ = new Thread(new Runner());
        runner_.start();
        
        machine_.transition(ServiceTransition.START);
    }

    
    /**
     * Stops monitoring memory consumption.
     *
     * @see toolbox.util.service.Startable#stop()
     */
    public void stop() throws ServiceException
    {
        machine_.checkTransition(ServiceTransition.STOP);
        machine_.transition(ServiceTransition.STOP);
        
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
     * @see toolbox.util.service.Startable#isRunning()
     */
    public boolean isRunning()
    {
        return getState() == ServiceState.RUNNING;
    }
    
    //--------------------------------------------------------------------------
    // Destroyable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy() throws ServiceException
    {
        machine_.checkTransition(ServiceTransition.DESTROY);
        
        if (runner_ != null)
            ThreadUtil.stop(runner_, 1000);
        
        machine_.transition(ServiceTransition.DESTROY);
    }
    
    /*
     * @see toolbox.util.service.Destroyable#isDestroyed()
     */
    public boolean isDestroyed() {
        return getState() == ServiceState.DESTROYED;
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