package toolbox.util.io.transferred;

import toolbox.util.io.monitored.MonitoredChannel;


public interface TransferredListener 
{
    /**
     * Notification that a given number of bytes have been transferred across
     * a channel.
     * 
     * @param stream Stream data was written to.
     * @param b Byte written to the stream.
     */
    void bytesTransferred(MonitoredChannel channel, TransferredEvent event);
}