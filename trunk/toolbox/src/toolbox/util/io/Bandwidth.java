package toolbox.util.io;

import org.apache.log4j.Logger;

import toolbox.util.collections.AsMap;

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
    
    private static final int TICKS_PER_SECOND = 10;
    private static final int MILLIS_PER_TICK = 1000 / TICKS_PER_SECOND;
    private static final int PREFFERED_MIN_FRAGMENT_SIZE = 2000;
    private static final long MILLIS_PER_WEEK = 1000L * 60L * 60L * 24L * 7L;
    private static final long MILLIS_PER_SECOND = 1000L;
    private static final long MILLIS_PER_AVERAGE_CHECKING_TICK = 1000L * 10L;
    private static final long MILLIS_PER_REPORT_TICK = 1000L * 60L * 60L;

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Type of bandwidth.
     * 
     * @see #TYPE_BOTH
     * @see #TYPE_RECEIVED
     * @see #TYPE_SENT
     */
    private String type_;

    /**
     * Quantity to increment every tick. This might be adjusted up or down to
     * keep the average on target.
     */
    protected int bandwidthPerTick_;

    /**
     * How much bandwidth is available right now.
     */
    private int available_ = 0;

    /**
     * When will more bandwidth be available.
     */
    private long moreBandwidthTime_ = 0L;

    /**
     * When to check the long term averages.
     */
    private long checkAverageTime_ = 0L;
    
    /**
     * Report time.
     */
    private long reportTime_ = 0L;

    /**
     * Original bandwidth specified in the constructor.
     */
    private int origBandwidth_;

    /**
     * Original average bandwidth specified in the constructor.
     */
    private int origAverageBandwidth_;

    /**
     * Total bytes consumed since startup.
     */
    private long totalUsed_ = 0L;

    /**
     * Total allowance since startup based on.
     */
    private long totalEarned_ = 0L;

    /**
     * Time of startup.
     */
    private long timeStarted_ = 0L; 

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Sets the upper bandwidth limit, in multiples of 10Bps (Bytes, not bits).
     * This class will treat any bandwidth under 100Bps as equal to 0Bps! A 
     * setting of 0 or less will turn bandwidth limiting off.
     * 
     * @param bandwidth Upper bandwidth limit in multiples of 10 bytes/sec
     * @param averageBandwidth Average bandwidth in multiples of 10 bytes/sec
     * @param type Bandwidth type. See {@link #type_} 
     */
    public Bandwidth(int bandwidth, int averageBandwidth, String type)
    {
        type_ = type;
        origBandwidth_ = bandwidth;
        origAverageBandwidth_ = averageBandwidth;
        bandwidthPerTick_ = bandwidth / TICKS_PER_SECOND;
        
        // to ensure new connections stay throttled
        if (averageBandwidth > 0 && bandwidthPerTick_ < 1)
            bandwidthPerTick_ = 1; 
        
        timeStarted_ = System.currentTimeMillis();
        
        logger_.debug(
            "new Bandwidth(" 
            + bandwidth 
            + "," 
            + averageBandwidth
            + "," 
            + type 
            + ")");
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the current bandwidth per second allowed.
     * 
     * @return int
     */
    public int getCurrentBandwidthPerSecondAllowed()
    {
        return TICKS_PER_SECOND * bandwidthPerTick_;
    }


    /**
     * Returns the maximum packet length.
     * 
     * @return int
     */
    public int getMaximumPacketLength()
    {
        //return 1492;
        return Math.max(1492, origBandwidth_ / 3);
    }


    /**
     * Returns the available bandwidth.
     * 
     * @return int
     */
    public int getAvailableBandwidth()
    {
        return available_;
    }

    
    /**
     * Returns the bandwidth per tick.
     * 
     * @return int
     */
    public int getBandwidthPerTick()
    {
        return bandwidthPerTick_;
    }
    
    
    /**
     * Sets the bandwidth per tick.
     * 
     * @param bandwidthPerTick BandwidthPerTick to set.
     */
    public void setBandwidthPerTick(int bandwidthPerTick)
    {
        bandwidthPerTick_ = bandwidthPerTick;
    }

    
    /**
     * Account for bandwidth already used for input.
     * 
     * @param used Number of bytes transferred. May wait up to 4 seconds if low 
     *        on bandwidth.
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
     * Account for bandwidth used already for input in the asynchronous version.
     * Do not mix the two! Do not multiplex async bandwidth limiting.
     * 
     * @param used Number of bytes transferred.
     * @return Number of millis that must be slept before a read will never 
     *         block
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
     * Wait until some desired bytes of bandwidth are available. May return even
     * if less are available but will try for up to 4 seconds.
     * 
     * @param desired Number of bytes of bandwidth wanted.
     * @return Number of bytes granted.
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
     * Returns unused bandwidth back to the pool of available bandwidth.
     * 
     * @param returnedUnused Unused bandwidth.
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
     * Waits for the desired amount of bandwidth to become available.
     * 
     * @param desired Bandwidth desired.
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
     * Waits for the desired amount of bandwidth to become available for the
     * async version.
     * 
     * @param desired Bandwidth desired.
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
            if (available_ >= desired || available_ > PREFFERED_MIN_FRAGMENT_SIZE)
                break;
            
            long now = System.currentTimeMillis();
            
            int eeek = refillAvailableBandwidth(now);
            
            if (eeek != 0)
                return eeek;
            
            if (available_ >= desired || available_ > PREFFERED_MIN_FRAGMENT_SIZE)
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
     * Refills the available bandwidth.
     * 
     * @param now Current time.
     * @return int
     */
    protected synchronized int refillAvailableBandwidth(long now)
    {
        if (now >= moreBandwidthTime_)
        {

            if (now > moreBandwidthTime_ + 10 * MILLIS_PER_TICK)
            {
                available_ += bandwidthPerTick_;
                moreBandwidthTime_ = now + MILLIS_PER_TICK;
            }
            else
            {
                available_ += bandwidthPerTick_;
                moreBandwidthTime_ = moreBandwidthTime_ + MILLIS_PER_TICK;
            }

            // Take the oppportunity to check long term averages 
            if (origAverageBandwidth_ != 0)
            {
                long uptimeThisWeek = now - timeStarted_;
                
                if (timeStarted_ == 0L || (uptimeThisWeek) > MILLIS_PER_WEEK)
                {
                    timeStarted_ = now;
                    totalEarned_ = 0L;
                    totalUsed_ = 0L;
                    checkAverageTime_ = now + MILLIS_PER_AVERAGE_CHECKING_TICK;
                    reportTime_ = now + MILLIS_PER_REPORT_TICK;
                }
                else if (now > checkAverageTime_)
                {
                    checkAverageTime_ = now + MILLIS_PER_AVERAGE_CHECKING_TICK;
                    
                    totalEarned_ = 
                        (uptimeThisWeek * origAverageBandwidth_) / 
                        MILLIS_PER_SECOND;
                    
                    if (totalEarned_ > totalUsed_)
                    {
                        // Restore original bandwidthLimit
                        bandwidthPerTick_ = origBandwidth_ / TICKS_PER_SECOND;
                    }
                    else if (totalEarned_ + 400000L < totalUsed_)
                    {
                        // Throttle to long term average and pause activity
                        bandwidthPerTick_ = 
                            origAverageBandwidth_ / TICKS_PER_SECOND;
                        
                        return 10000;
                    }
                    else if (totalEarned_ + 200000L < totalUsed_)
                    {
                        //Throttle to long term average
                        bandwidthPerTick_ = 
                            origAverageBandwidth_ / TICKS_PER_SECOND;
                    }
                    else if (totalEarned_ + 100000L < totalUsed_)
                    {
                        // Less serious. see if half the bandwidth will do
                        bandwidthPerTick_ = 
                            origBandwidth_ / 2 / TICKS_PER_SECOND;
                    }
                    
                    if (bandwidthPerTick_ < 1) // Must never go less than 1
                        bandwidthPerTick_ = 1;

                    if (now > reportTime_)
                    {
                        reportTime_ = now + MILLIS_PER_REPORT_TICK;
                        
                        logger_.debug(
                            "bytes " 
                            + type_ 
                            + "=" 
                            + totalUsed_ 
                            + " bytes earned = " 
                            + totalEarned_);
                    }
                }
            }
        }
        return 0;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return AsMap.of(this).toString();
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