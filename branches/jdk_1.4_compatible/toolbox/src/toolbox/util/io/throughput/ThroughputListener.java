package toolbox.util.io.throughput;

/**
 * ThroughputListener provides a notification mechanism from a 
 * ThroughputMonitor to an interested client. The details of the throughput
 * are encapsulated in a ThroughputEvent.
 * 
 * @see toolbox.util.io.throughput.ThroughputEvent
 * @see toolbox.util.io.throughput.ThroughputMonitor 
 */
public interface ThroughputListener 
{
    /**
     * Notification of the number of bytes transferred across the channel over
     * the last sample interval.
     * 
     * @param event Event containing the throughput information. 
     */
    void currentThroughput(ThroughputEvent event);
}