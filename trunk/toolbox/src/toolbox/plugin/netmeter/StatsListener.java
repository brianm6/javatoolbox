package toolbox.plugin.netmeter;

/**
 * StatsListener provides notification related to collected statistics based on 
 * network activity.
 */
public interface StatsListener
{
    /**
     * Notifcation of data throughput in kilobytes per second.
     * 
     * @param kbs Throughput per second.
     */
    public void throughput(int kbs);
}