package toolbox.util.io.transferred;

import toolbox.util.io.MonitoredChannel;

/**
 * TransferredMonitor describes the interface necessary to monitor the bytes 
 * transferred over an arbitrary data channel regardless of protocol, direction, 
 * or content.
 * 
 * @see toolbox.util.io.transferred.TransferredListener
 * @see toolbox.util.io.transferred.TransferredEvent
 */
public interface TransferredMonitor extends MonitoredChannel 
{
    /**
     * Adds a listener to this monitor.
     * 
     * @param listener Listener to receive notifications from this monitor.
     */
    void addTransferredListener(TransferredListener listener);
    
    
    /**
     * Removes a listener from this monitor.
     * 
     * @param listener Listener that will no longer receive notifications from
     *        this monitor.
     */
    void removeTransferredListener(TransferredListener listener);
    
    
    /**
     * Sets the number of transferred bytes between which notifications are sent
     * to registered listeners.
     * 
     * @param numBytes Notifications are sent every numBytes transferred.
     */
    void setSampleLength(int numBytes);
    
    
    /**
     * Returns the number of bytes transferred between each notification.
     * 
     * @return int
     */
    int getSampleLength();
    
    
    /**
     * Returns the total number of bytes transferred as witnessed by this
     * monitor.
     * 
     * @return long
     */
    long getBytesTransferred();
}