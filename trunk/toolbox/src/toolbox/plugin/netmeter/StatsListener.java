package toolbox.plugin.netmeter;

import toolbox.util.service.ServiceListener;

/**
 * StatsListener provides notification related to collected statistics based on 
 * network activity.
 * 
 * @see toolbox.plugin.netmeter.NetMeterPlugin
 */
public interface StatsListener extends ServiceListener
{
    /**
     * Notification of data throughput in kilobytes per second.
     * 
     * @param kbs Throughput per second.
     */
    void throughput(int kbs);
}