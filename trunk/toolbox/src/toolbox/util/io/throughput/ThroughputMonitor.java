package toolbox.util.io.throughput;

import toolbox.util.io.monitored.MonitoredChannel;

/**
 * ThroughputMonitor outlines the interface necessary to monitor the byte 
 * throughput of an arbitrary data channel regardless of protocol, direction, 
 * or content.
 * 
 * @see toolbox.util.io.throughput.ThroughputListener
 * @see toolbox.util.io.throughput.ThroughputEvent
 */
public interface ThroughputMonitor extends MonitoredChannel {

    /**
     * Adds a listener to the list of throughput listeners.
     * 
     * @param listener Throughput listener to add.
     */
    void addThroughputListener(ThroughputListener listener);
    
    
    /**
     * Removes a listener from the lsit of throughput listeners.
     * 
     * @param listener Throughput listener to remove.
     */
    void removeThroughputListener(ThroughputListener listener);
    
    
    /**
     * Sets the length of the sample interval to determine throughput.
     * 
     * @param millis Length of the sample interval in milliseconds.
     */
    void setSampleInterval(int millis);
    
    
    /**
     * Returns the length of the sample interval in milliseconds.
     * 
     * @return int
     */
    int getSampleInterval();
    
    
    /**
     * Turns on/off monitoring of channel throughput.
     * 
     * @param monitor True to activate monitoring, false otherwise.
     */
    void setMonitoringThroughput(boolean monitor);
    
    
    /**
     * Returns true if the channel is being monitored for throughput. False
     * otherwise.
     * 
     * @return boolean
     */
    boolean isMonitoringThroughput();
    
    
    /**
     * Notifies this monitor that a given number of bytes have been transferred
     * across the channel.
     * 
     * @param count Number of bytes transferred.
     */
    public void newBytesTransferred(long count); 
}