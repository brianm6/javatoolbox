package toolbox.util.io.throughput;

/**
 * ThroughputListener provides a notification mechanism from a 
 * ThroughputMonitor to an interested client. The details of the throughput
 * are encapsulated in a ThroughputEvent.
 * 
 * @see toolbox.util.io.throughput.ThroughputEvent
 * @see toolbox.util.io.throughput.ThroughputMonitor 
 */
public interface ThroughputListener {

    /**
     * Notification of the number of bytes transfered through the stream
     * over the last sample interval.
     * 
     * @param event Event containing the throughput details. 
     */
    void currentThroughput(ThroughputEvent event);
}