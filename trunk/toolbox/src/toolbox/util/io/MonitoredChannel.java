package toolbox.util.io;

/**
 * MonitoredChannel is a channel (any entity in which data can is transferred)
 * with the ability to be monitored by external clients.
 */
public interface MonitoredChannel 
{
    /**
     * Notifies this monitor that a given number of bytes have been transferred
     * across the channel. This method is intended for the consumer/producer of
     * data to notify the monitor.
     * 
     * @param count Number of bytes transferred since the last invocation of
     *        this method.
     */
    void newBytesTransferred(long count);
}
