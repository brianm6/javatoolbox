package toolbox.util.io.transferred;

import java.util.Date;

import toolbox.util.io.MonitoredChannel;

/**
 * TransferredEvent contains the details of a data transfer event.
 * 
 * @see toolbox.util.io.transferred.TransferredListener
 */
public class TransferredEvent 
{
    /**
     * Source of the transfer event.
     */
    private MonitoredChannel source_;
    
    /**
     * Timestamp of the creation of the event.
     */
    private Date timestamp_;
    
    /**
     * Event is generated every sampleLength bytes that are transferred.
     */
    private int sampleLength_;
    
    /**
     * Total number of bytes transferred across the channel.
     */
    private long total_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a TransferredEvent.
     * 
     * @param source Source of the event.
     * @param sampleLength Event is generated every sampleLength bytes read.
     * @param total Total number of bytes read.
     */
    public TransferredEvent(
        MonitoredChannel source, 
        int sampleLength, 
        long total) {
        
        setSource(source);
        setTimestamp(new Date());
        setSampleLength(sampleLength);
        setTotal(total);
    }
    
    //--------------------------------------------------------------------------
    // Accessors/Mutators
    //--------------------------------------------------------------------------
    
    /**
     * @return Returns the sampleLength.
     */
    public int getSampleLength()
    {
        return sampleLength_;
    }


    /**
     * @param sampleLength The sampleLength to set.
     */
    public void setSampleLength(int sampleLength)
    {
        sampleLength_ = sampleLength;
    }


    /**
     * @return Returns the source.
     */
    public MonitoredChannel getSource()
    {
        return source_;
    }


    /**
     * @param source The source to set.
     */
    public void setSource(MonitoredChannel source)
    {
        source_ = source;
    }


    /**
     * @return Returns the timestamp.
     */
    public Date getTimestamp()
    {
        return timestamp_;
    }


    /**
     * @param timestamp The timestamp to set.
     */
    public void setTimestamp(Date timestamp)
    {
        timestamp_ = timestamp;
    }


    /**
     * @return Returns the total.
     */
    public long getTotal()
    {
        return total_;
    }


    /**
     * @param total The total to set.
     */
    public void setTotal(long total)
    {
        total_ = total;
    }
}
