package toolbox.util.io.throughput;

import java.util.Date;

/**
 * ThroughputEvent contains the details of a throughput snapshot over a sample
 * interval.
 * 
 * @see toolbox.util.io.throughput.ThroughputListener
 * @see toolbox.util.io.throughput.ThroughputMonitor
 */
public class ThroughputEvent
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Monitor that generated this event.
     */
    private ThroughputMonitor source_;
    
    /**
     * Timestamp at which this event was created.
     */
    private Date timestamp_;
    
    /**
     * The interva in milliseconds that the throughput was sampled.
     */
    private int sampleInterval_;
    
    /**
     * The number of bytes transferred over the sample interval. This is
     * effectivly the throughput.
     */
    private int bytesTransferred_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ThroughputEvent.
     * 
     * @param source Monitor that generated the event.
     * @param sampleInterval Sample interval in milliseconds.
     * @param bytesTransferred Number of bytes transferred over the sample
     *        interval.
     */
    public ThroughputEvent(
        ThroughputMonitor source, 
        int sampleInterval,
        int bytesTransferred)
    {
        setSource(source);
        setSampleInterval(sampleInterval);
        setBytesTransferred(bytesTransferred);
        setTimestamp(new Date());
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Convenience method to get the throughput. Delegates to
     * getBytesTransferred().
     * 
     * @return int
     */
    public int getThroughput() 
    {
        return getBytesTransferred();
    }
    
    //--------------------------------------------------------------------------
    // Accessors/Mutators
    //--------------------------------------------------------------------------
    
    /**
     * Returns the bytesTransferred over the sample interval.
     * 
     * @return int
     */
    public int getBytesTransferred()
    {
        return bytesTransferred_;
    }


    /**
     * Sets the number of bytes transferred over the sample interval.
     * 
     * @param bytesTransferred Number of bytes transferred.
     */
    public void setBytesTransferred(int bytesTransferred)
    {
        bytesTransferred_ = bytesTransferred;
    }


    /**
     * Returns the sampleInterval in milliseconds.
     * 
     * @return int
     */
    public int getSampleInterval()
    {
        return sampleInterval_;
    }


    /**
     * Sets the sampleInterval in milliseconds.
     * 
     * @param sampleInterval The sampleInterval to set.
     */
    public void setSampleInterval(int sampleInterval)
    {
        sampleInterval_ = sampleInterval;
    }


    /**
     * Returns the source of the event.
     * 
     * @return ThroughputMonitor
     */
    public ThroughputMonitor getSource()
    {
        return source_;
    }


    /**
     * Sets the source of the event.
     * 
     * @param source The source to set.
     */
    public void setSource(ThroughputMonitor source)
    {
        source_ = source;
    }


    /**
     * Returns the timestamp of the event creation.
     * 
     * @return Date
     */
    public Date getTimestamp()
    {
        return timestamp_;
    }


    /**
     * Sets the timestamp of the event creation.
     * 
     * @param timestamp The timestamp to set.
     */
    public void setTimestamp(Date timestamp)
    {
        timestamp_ = timestamp;
    }
}