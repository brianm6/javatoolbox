package toolbox.util.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * An InputStream which limits (or "throttles") the bandwidth of bytes written
 * to it. Original version from http://freenet.sf.net
 */
public class ThrottledInputStream extends FilterInputStream
{
    private static final Logger logger_ = 
        Logger.getLogger(ThrottledInputStream.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private Bandwidth bandwidth_;
    
    private int reservedBandwidth_ = 0;
    
    protected boolean disabled_ = false;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    public ThrottledInputStream(InputStream in, boolean disabled)
    {
        super(in);
        disabled_ = disabled;
        
        // slow down the creation of new streams if bandwidth is low.
        if (bandwidth_ != null && bandwidth_.getBandwidthPerTick() > 0)
            reservedBandwidth_ = bandwidth_.getBandwidth(10000); 
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Activate throttling on InputStreams.
     * 
     * @param bandwidth the available bandwidth for InputStreams.
     */
    public void setThrottle(Bandwidth bandwidth)
    {
        bandwidth_ = bandwidth;
    }

    
    /**
     * @param disabled
     */
    public void setDisabled(boolean disabled)
    {
        disabled_ = disabled;
    }

    //--------------------------------------------------------------------------
    // Overrides FilterInputStream
    //--------------------------------------------------------------------------
    
    /**
     * Read a single byte from this InputStream.
     * 
     * @throws IOException if an I/O error occurs on the InputStream.
     */
    public int read() throws IOException
    {
        boolean trace = false;
        
        if (trace)
            logger_.debug("read()");
        
        int rv = super.read();
        
        if (trace)
            logger_.debug("done super.read()");
        
        if (disabled_)
            return rv;
        
        if (rv != -1)
            bandwidth_.chargeBandwidth(1);
        
        if (trace)
            logger_.debug("Charged");
        
        if (reservedBandwidth_ > 0)
        {
            bandwidth_.putBandwidth(reservedBandwidth_);
            reservedBandwidth_ = 0;
        }
        
        if (trace)
            logger_.debug("Put");
        
        return rv;
    }


    /**
     * Read an array of bytes from this InputStream.
     * 
     * @param data the bytes to read.
     * @param offset the index in the array to start at.
     * @param length the number of bytes to read.
     * @throws IOException if an I/O error occurs on the InputStream.
     */
    public int read(byte[] data, int offset, int length) throws IOException
    {
        boolean trace = false;
        
        if (trace)
            logger_.debug("read(,,)");
        
        int rv = in.read(data, offset, length);
        
        if (trace)
            logger_.debug("super.read(,,) done");
        
        if (disabled_)
            return rv;
        
        if (rv > 0)
        {
            bandwidth_.chargeBandwidth(rv);
        }
        
        if (reservedBandwidth_ > 0)
        {
            bandwidth_.putBandwidth(reservedBandwidth_);
            reservedBandwidth_ = 0;
        }
        
        return rv;
    }
}

///**
//* Gets a ThrottledInputStream based on the given InputStream. If throttling
//* is turned off (by calling {@ref #setThrottle setThrottle}with a zero or
//* negative argument) then the given InputStream is simply returned.
//* 
//* @param in the InputStream to throttle.
//* @return an InputStream which is either the original InputStream if
//*         throttling is turned off, or a new ThrottledInputStream if not.
//*/
//public static InputStream throttledStream(InputStream in)
//{
//
// if (bandwidth_ == null || bandwidth_.getBandwidthPerTick() <= 0)
//     return in;
//
// logger_.debug(
//     "ThrottledInput, creating new stream, bpt = " + 
//     bandwidth_.getBandwidthPerTick());
//
// return new ThrottledInputStream(in, false);
//}
