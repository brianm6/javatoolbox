package toolbox.util.io.throughput;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;

/**
 * DefaultThroughputMonitor is a basic implementation of a ThroughputMonitor.
 * 
 * @see toolbox.util.io.throughput.ThroughputListener
 * @see toolbox.util.io.throughput.ThroughputEvent
 */
public class DefaultThroughputMonitor implements ThroughputMonitor {

    private static final Logger logger_ = 
        Logger.getLogger(DefaultThroughputMonitor.class);
    
    //--------------------------------------------------------------------------
    // Defaults Constants
    //--------------------------------------------------------------------------
    
    /**
     * Default sample interval is 1 second.
     */
    private static final int DEFAULT_SAMPLE_INTERVAL = 1000;
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /** 
     * Array of registered listeners. 
     */
    private ThroughputListener[] listeners_;

    /**
     * Number of milliseconds between each notification of throughput.
     */
    private int interval_;

    /**
     * Number of bytes transferred over the current interval.
     */
    private int bytesTransferred_;
    
    /**
     * Timer use to generated notification events every sample interval.
     */
    private Timer timer_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    public DefaultThroughputMonitor() 
    {
        interval_ = DEFAULT_SAMPLE_INTERVAL;
        bytesTransferred_ = 0;
        listeners_ = new ThroughputListener[0];
    }
    
    //--------------------------------------------------------------------------
    // ThroughputMonitor Interface
    //--------------------------------------------------------------------------

	/**
	 * @see toolbox.util.io.throughput.ThroughputMonitor#addThroughputListener(
     *      toolbox.util.io.throughput.ThroughputListener)
	 */
	public void addThroughputListener(ThroughputListener listener) 
    {
        if (listener != null)
            listeners_ = (ThroughputListener[]) 
                ArrayUtil.add(listeners_, listener);
        else
            logger_.error("Cannot add a null ThroughputListener!");
	}

    
	/**
	 * @see toolbox.util.io.throughput.ThroughputMonitor
     *      #removeThroughputListener(
     *      toolbox.util.io.throughput.ThroughputListener)
	 */
	public void removeThroughputListener(ThroughputListener listener) 
    {
        listeners_ = (ThroughputListener[]) ArrayUtil.add(listeners_, listener);
	}

    
	/**
	 * @see toolbox.util.io.throughput.ThroughputMonitor#setSampleInterval(
     *      int)
	 */
	public void setSampleInterval(int millis) 
    {
        interval_ = millis;
	}

    
	/**
	 * @see toolbox.util.io.throughput.ThroughputMonitor#setSampleInterval()
	 */
	public int getSampleInterval() 
    {
		return interval_;
	}

    
    /**
     * @see toolbox.util.io.throughput.ThroughputMonitor#newBytesTransferred(
     *      long)
     */
    public void newBytesTransferred(long count) 
    {
    	bytesTransferred_ += count;
    }

    
    /**
     * @see toolbox.util.io.throughput.ThroughputMonitor
     *      #setMonitoringThroughput(boolean)
     */
    public void setMonitoringThroughput(boolean monitor)
    {
        if (monitor) 
        {
            if (timer_ != null)
                timer_.cancel();
            
            timer_ = new Timer(false);
            timer_.schedule(new ThroughputSampler(), 0, getSampleInterval());
        }
        else 
        {
            if (timer_ != null) {
                timer_.cancel();
                timer_ = null;
            }
        }
    }

    
    /**
     * @see toolbox.util.io.throughput.ThroughputMonitor#isMonitoringThroughput()
     */
    public boolean isMonitoringThroughput()
    {
        return timer_ == null;
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /** 
     * Fires notification of stream throughput per sample period.
     * 
     * @param throughput Number of bytes written per sample period.
     */
    protected void fireCurrentThroughput()
    {
        if (listeners_.length == 0)
            return;
        
        ThroughputEvent event = null;
        
        // Sync on reset of bytesTransferred
        //synchronized (this) 
        //{
            
            event = new ThroughputEvent(
                DefaultThroughputMonitor.this,
                getSampleInterval(),
                bytesTransferred_);
            
            bytesTransferred_ = 0;
        //}
        
        for (int i = 0, n = listeners_.length; i < n; i++)
            listeners_[i].currentThroughput(event);
    }

    //--------------------------------------------------------------------------
    // ThroughputSampler
    //--------------------------------------------------------------------------
    
    /** 
     * Task that collects samples on stream throughput and firesNotification
     * to interested listeners.
     */
    class ThroughputSampler extends TimerTask
    {
        public void run()
        {
            fireCurrentThroughput();
        }
    }
}