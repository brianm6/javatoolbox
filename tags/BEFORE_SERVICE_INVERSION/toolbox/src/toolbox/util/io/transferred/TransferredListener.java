package toolbox.util.io.transferred;

/**
 * TransferredListener provides a notification mechanism from a 
 * TransferredMonitor to an interested client. The details of the number of 
 * bytes transferred are encapsulated in a TransferredEvent.
 * 
 * @see toolbox.util.io.transferred.TransferredEvent
 * @see toolbox.util.io.transferred.TransferredMonitor 
 */
public interface TransferredListener 
{
    /**
     * Notification that a given number of bytes have been transferred across
     * a channel.
     *
     * @param event Event contains the bytes transferred details. 
     */
    void bytesTransferred(TransferredEvent event);
}