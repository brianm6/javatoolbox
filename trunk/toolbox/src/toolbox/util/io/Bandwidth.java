package toolbox.util.io;

import org.apache.log4j.Logger;

/**
 * Bandwidth from http://freenet.sf.net
 */
public final class Bandwidth
{
    private static final Logger logger_ = Logger.getLogger(Bandwidth.class);

    //--------------------------------------------------------------------------
    // Bandwidth Type Constants
    //--------------------------------------------------------------------------
    
    public static final String TYPE_SENT = "SENT";
    public static final String TYPE_RECEIVED = "RECEIVED";
    public static final String TYPE_BOTH = "BOTH";
    
    //--------------------------------------------------------------------------
    // Units of Time Constants
    //--------------------------------------------------------------------------
    
    private static final int ticksPerSecond = 10;
    private static final int millisPerTick = 1000 / ticksPerSecond;
    private static final int preferredMinFragmentSize = 2000;
    private static final long millisPerWeek = 1000L * 60L * 60L * 24L * 7L;
    private static final long millisPerSecond = 1000L;
    private static final long millisPerAverageCheckingTick = 1000L * 10L;
    private static final long millisPerReportTick = 1000L * 60L * 60L;

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * set to one of the above TYPE_const
     */
    private String type_;

    /**
     * quantity to increment every tick. This might be adjusted up or down to
     * keep the average on target
     */
    protected int bandwidthPerTick_;

    /**
     * how much is available right now
     */
    private int available_ = 0;

    /**
     * when more bandwidth is available
     */
    private long moreBandwidthTime_ = 0L;

    /**
     * when to check the long term averages
     */
    private long checkAverageTime_ = 0L;
    
    /**
     * report time
     */
    private long reportTime_ = 0L;

    /**
     * original constructor arg
     */
    private int origBandwidth_;

    /**
     * original constructor arg
     */
    private int origAverageBandwidth_;

    /**
     * total bytes since startup
     */
    private long totalUsed_ = 0L;

    /**
     * total allowance since startup based on
     */
    private long totalEarned_ = 0L;

    /**
     * time of startup
     */
    private long timeStarted_ = 0L; 

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Sets the upper bandwidth limit, in multiples of 10Bps (Bytes, not bits),
     * for all ThrottledOutputStreams. This class will treat any bandwidth under
     * 100Bps as equals to 0Bps! A setting of 0 or less will turn bandwidth
     * limiting off for all ThrottledOutputStreams, and prevent new ones from
     * being created.
     * 
     * @param bandwidth the upper bandwidth limit in multiples of 10 bytes/sec
     * @param averageBandwidth Average bandwidth in multiples of 10 bytes/sec
     * @param type Bandwidth type. See {@link #TYPE_SENT} 
     */
    public Bandwidth(int bandwidth, int averageBandwidth, String type)
    {
        type_ = type;
        origBandwidth_ = bandwidth;
        origAverageBandwidth_ = averageBandwidth;
        bandwidthPerTick_ = bandwidth / ticksPerSecond;
        
        // to ensure new connections stay throttled
        if (averageBandwidth > 0 && bandwidthPerTick_ < 1)
            bandwidthPerTick_ = 1; 
        
        timeStarted_ = System.currentTimeMillis();
        
        logger_.debug("new Bandwidth(" + bandwidth + "," + averageBandwidth
            + "," + type + ")");
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * @return int
     */
    public int getCurrentBandwidthPerSecondAllowed()
    {
        return ticksPerSecond * bandwidthPerTick_;
    }


    /**
     * @return int
     */
    public int getMaximumPacketLength()
    {
        //return 1492;
        return Math.max(1492, origBandwidth_ / 3);
    }


    /**
     * @return int
     */
    public int getAvailableBandwidth()
    {
        return available_;
    }

    
    /**
     * @return Returns the bandwidthPerTick.
     */
    public int getBandwidthPerTick()
    {
        return bandwidthPerTick_;
    }
    
    
    /**
     * @param bandwidthPerTick The bandwidthPerTick to set.
     */
    public void setBandwidthPerTick(int bandwidthPerTick)
    {
        bandwidthPerTick_ = bandwidthPerTick;
    }

    
    /**
     * account for bandwidth already used for input
     * 
     * @param used the number of bytes read from input may wait up to 4 seconds
     *        if low on bandwidth.
     */
    public void chargeBandwidth(int used)
    {
        boolean trace = false; 
        
        if (trace)
            logger_.debug(
                "chargeBandwidth(" + used + ")", 
                new Exception("debug"));
        
        synchronized (this)
        {
            if (trace)
                logger_.debug("chargeBandwidth(" + used + ") synchronized");
            
            if (used < 0)
            {
                logger_.debug(
                    "used=" + used + " seems unreasonable bandwidth to charge");
                return;
            }
            
            totalUsed_ += used;
            waitForBandwidth(used);
            available_ -= used;
        }
        
        if (trace)
            logger_.debug("Leaving chargeBandwidth(" + used + ")");
    }

    
    /**
     * account for bandwidth used already for input asynchronous version. Do not
     * mix the two! Do not multiplex async bandwidth limiting.
     * 
     * @param used the number of bytes read from input
     * @return number of millis that must be slept before can read again will
     *         never block
     */
    public BandwidthToken chargeBandwidthAsync(int used)
    {
        boolean trace = false;
        int sleepTime = 0;
        
        if (trace)
            logger_.debug(
                "chargeBandwidthAsync(" + used + ")", 
                new Exception("debug"));
        
        BandwidthToken bt = new BandwidthToken();
        
        synchronized (this)
        {
            if (trace)
                logger_.debug("chargeBandwidth(" + used + ") synchronized");
            
            if (used < 0)
            {
                if (trace)
                    logger_.debug(
                        "used=" + used + 
                        " seems unreasonable bandwidth to charge");
                
                bt.availableNow = 0;
                bt.sleepUntil = -1;
                return bt;
            }
            
            if (available_ < 0)
                logger_.debug("available = " + available_);
            
            if (available_ > used)
            {
                bt.availableNow = available_;
                available_ -= used;
                totalUsed_ += used;
                bt.sleepUntil = -1;
                
                if (trace)
                    logger_.debug("chargeBandwidthAsync(" + used
                        + ") fulfilled order, no sleep: " + "available="
                        + available_ + ", totalUsed=" + totalUsed_);
                
                return bt;
            }
            else
            {
                long x = waitForBandwidthAsync(used);
                long now = System.currentTimeMillis();
                
                if (x > 0)
                    bt.sleepUntil = now + x;
                else
                    bt.sleepUntil = -1;
                
                if (available_ > used)
                {
                    bt.availableNow = used;
                }
                else
                {
                    bt.availableNow = available_;
                }
                
                available_ -= bt.availableNow;
                totalUsed_ += bt.availableNow;
                
                if (bt.sleepUntil > 0)
                {
                    if (trace)
                        logger_.debug("chargeBandwidthAsync(" + used
                            + ") partially fulfilled: sleep "
                            + (bt.sleepUntil - now) + "ms, available now "
                            + bt.availableNow + ", available=" + available_
                            + ", totalUsed=" + totalUsed_);
                }
                else if (trace)
                    logger_.debug("chargeBandwidthAsync(" + used
                        + ") fulfilled (late): available now "
                        + bt.availableNow + ", available=" + available_
                        + ", totalUsed=" + totalUsed_);
                
                return bt;
            }
        }
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * wait until some desired bytes of bandwidth are available may return even
     * if less are available but will try for up to 4 seconds
     * 
     * @param desired the number of bytes of bandwidth wanted
     * @return the number of bytes granted.
     */
    protected int getBandwidth(int desired)
    {
        boolean trace = false;
        
        if (trace)
            logger_.debug(
                "getBandwidth(" + desired + ")", new Exception("debug"));
        
        synchronized (this)
        {
            if (trace)
                logger_.debug("getBandwidth(" + desired + ") synchronized");
            
            if (desired < 0)
            {
                logger_.debug(
                    "desired=" + desired + 
                    " seems unreasonable bandwidth to ask for");
                
                return desired;
            }
            
            waitForBandwidth(desired);
            int result = Math.min(desired, available_);
            available_ -= result;
            totalUsed_ += result;
            
            if (trace)
                logger_.debug("Leaving getBandwidth(" + desired + ")");
            
            return result;
        }
    }


    /**
     * @param returnedUnused
     */
    protected synchronized void putBandwidth(int returnedUnused)
    {
        if (returnedUnused < 0)
        {
            logger_.debug(
                "returnedUnused=" + returnedUnused + 
                " seems unreasonable bandwidth to put back");
        }

        available_ += returnedUnused;
    }


    /**
     * @param desired
     */
    protected synchronized void waitForBandwidth(int desired)
    {
        int millisToSleep = 0;
        
        while ((millisToSleep = waitForBandwidthAsync(desired)) > 0)
        {
            try
            {
                Thread.sleep(millisToSleep);
            }
            catch (InterruptedException e)
            {
                ; // Noop
            }
        }
    }


    /**
     * @param desired
     * @return int
     */
    protected synchronized int waitForBandwidthAsync(int desired)
    {
        if (desired == 0)
            return 0;
        
        if (desired < 0)
        {
            logger_.debug(
                "desired=" + desired + 
                " seems unreasonable bandwidth to ask for");
            
            return 0;
        }

        long totalWaitMillis = 0L;
        
        for (;;)
        {
            // quick test to see if we can avoid the system call
            if (available_ >= desired || available_ > preferredMinFragmentSize)
                break;
            
            long now = System.currentTimeMillis();
            
            int eeek = refillAvailableBandwidth(now);
            
            if (eeek != 0)
                return eeek;
            
            if (available_ >= desired || available_ > preferredMinFragmentSize)
                break;
            
            long millisToSleep = moreBandwidthTime_ - now;
            
            if (millisToSleep > 1000L)
            {
                logger_.debug(
                    "millisToSleep=" + millisToSleep + 
                    " seems unreasonable. Limiting this wait to 1000");
                
                millisToSleep = 1000L;
            }
            
            if (totalWaitMillis > 4000L)
            {
                logger_.debug(
                    "totalWaitMillis=" + totalWaitMillis + 
                    " seems unreasonable. Abandon wait. desired=" + desired + 
                    " available=" + available_);
                break;
            }
            
            if (millisToSleep > 0)
            {
                if (logger_.isDebugEnabled() || millisToSleep > 500)
                    logger_.debug(
                        "Bandwidth waiting " + millisToSleep + 
                        ", total so far " + totalWaitMillis + 
                        "; available=" + available_ + ", desired=" + desired);

                totalWaitMillis += millisToSleep;
                return (int) millisToSleep;
            }
        }
        return 0;
    }


    /**
     * @param now
     * @return int
     */
    protected synchronized int refillAvailableBandwidth(long now)
    {
        if (now >= moreBandwidthTime_)
        {

            if (now > moreBandwidthTime_ + 10 * millisPerTick)
            {
                available_ += bandwidthPerTick_;
                moreBandwidthTime_ = now + millisPerTick;
            }
            else
            {
                available_ += bandwidthPerTick_;
                moreBandwidthTime_ = moreBandwidthTime_ + millisPerTick;
            }

            // take the oppportunity to check long term averages 
            if (origAverageBandwidth_ != 0)
            {
                long uptimeThisWeek = now - timeStarted_;
                
                if (timeStarted_ == 0L || (uptimeThisWeek) > millisPerWeek)
                {
                    timeStarted_ = now;
                    totalEarned_ = 0L;
                    totalUsed_ = 0L;
                    checkAverageTime_ = now + millisPerAverageCheckingTick;
                    reportTime_ = now + millisPerReportTick;
                }
                else if (now > checkAverageTime_)
                {
                    checkAverageTime_ = now + millisPerAverageCheckingTick;
                    
                    totalEarned_ = 
                        (uptimeThisWeek * origAverageBandwidth_) / 
                        millisPerSecond;
                    
                    if (totalEarned_ > totalUsed_)
                    {
                        // restore original bandwidthLimit
                        bandwidthPerTick_ = origBandwidth_ / ticksPerSecond;
                    }
                    else if (totalEarned_ + 400000L < totalUsed_)
                    {
                        // throttle to long term average and pause activity
                        bandwidthPerTick_ = 
                            origAverageBandwidth_ / ticksPerSecond;
                        return 10000;
                    }
                    else if (totalEarned_ + 200000L < totalUsed_)
                    {
                        //throttle to long term average
                        bandwidthPerTick_ = 
                            origAverageBandwidth_ / ticksPerSecond;
                    }
                    else if (totalEarned_ + 100000L < totalUsed_)
                    {
                        //less serious. see if half the bandwidth will do
                        bandwidthPerTick_ = origBandwidth_ / 2 / ticksPerSecond;
                    }
                    
                    if (bandwidthPerTick_ < 1) //must never go less than 1
                        bandwidthPerTick_ = 1;

                    if (now > reportTime_)
                    {
                        reportTime_ = now + millisPerReportTick;
                        logger_.debug(
                            "bytes " + type_ + "=" + totalUsed_ + 
                            " bytes earned=" + totalEarned_);
                    }
                }
            }
        }
        return 0;
    }
    
    //--------------------------------------------------------------------------
    // BandwidthToken
    //--------------------------------------------------------------------------
    
    public class BandwidthToken
    {
        public int availableNow;
        public long sleepUntil;
    }
}