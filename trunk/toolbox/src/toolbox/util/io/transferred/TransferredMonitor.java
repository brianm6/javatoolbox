package toolbox.util.io.transferred;

import toolbox.util.io.MonitoredChannel;

/**
 * TransferredMonitor outlines the interface necessary to monitor the bytes 
 * transferred over an arbitrary data channel regardless of protocol, direction, 
 * or content.
 * 
 * @see toolbox.util.io.transferred.TransferredListener
 * @see toolbox.util.io.transferred.TransferredEvent
 */
public interface TransferredMonitor extends MonitoredChannel 
{
    /**
     * Adds a listener to the list of transferred listeners.
     * 
     * @param listener Transferred listener to add.
     */
    void addTransferredListener(TransferredListener listener);
    
    
    /**
     * Removes a listener from the list of transferred listeners.
     * 
     * @param listener Transferred listener to remove.
     */
    void removeTransferredListener(TransferredListener listener);
    
    
    /**
     * Sets the length of the number of bytes transferred at which to report
     * the number of bytes transferred.
     * 
     * @param numBytes Length of the sample interval in number of bytes.
     */
    void setSampleLength(int numBytes);
    
    
    /**
     * Returns the length of the sample in number of bytes.
     * 
     * @return int
     */
    int getSampleLength();
    
    
    /**
     * Notifies this monitor that a given number of bytes have been transferred
     * across the channel.
     * 
     * @param count Number of bytes transferred.
     */
    public void newBytesTransferred(long count);


    /**
     * Returns the total number of bytes transferred.
     * 
     * @return long
     */
    long getBytesTransferred();
}