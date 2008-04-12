package toolbox.util.io.throughput;

import toolbox.util.io.MonitoredChannel;

/**
 * ThroughputMonitor outlines the interface necessary to monitor the byte 
 * throughput of an arbitrary data channel regardless of protocol, direction, 
 * or content.
 * 
 * @see toolbox.util.io.throughput.ThroughputListener
 * @see toolbox.util.io.throughput.ThroughputEvent
 */
public interface ThroughputMonitor extends MonitoredChannel 
{
    /**
     * Adds a listener to this monitor.
     * 
     * @param listener Listener to receive notifications from this monitor.
     */
    void addThroughputListener(ThroughputListener listener);
    
    
    /**
     * Removes a listener from this monitor.
     * 
     * @param listener Listener that will no longer receive notifications from
     *        this monitor.
     */
    void removeThroughputListener(ThroughputListener listener);
    
    
    /**
     * Sets the number of milliseconds between each throughput notification.
     * 
     * @param millis Length of the sample interval in milliseconds.
     */
    void setSampleInterval(int millis);
    
    
    /**
     * Returns the number of milliseconds between each throughput notification.
     * 
     * @return int
     */
    int getSampleInterval();
    
    
    /**
     * Turns monitoring (generation of notification) of channel throughput on 
     * and off.
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
}