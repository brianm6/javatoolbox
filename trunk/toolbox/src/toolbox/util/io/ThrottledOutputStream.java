package toolbox.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

/**
 * An OutputStream which limits (or "throttles") the bandwidth of bytes written
 * to it. Originally from http://freenet.sf.net.
 */
public class ThrottledOutputStream extends FilterOutputStream
{
    private static final Logger logger_ = Logger.getLogger(
        ThrottledOutputStream.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Bandwidth by which this stream will be limited.
     */
    private Bandwidth bandwidth_;
    
    private int reservedBandwidth_ = 0;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    public ThrottledOutputStream(OutputStream out)
    {
        super(out);
        
        //Force the use of a static method that checks if throttling
        // is on before creating a throttled stream.
        // slow down the creation of new streams if bandwidth is low.
        
        if (bandwidth_ != null && bandwidth_.getBandwidthPerTick() > 0)
            reservedBandwidth_ = bandwidth_.getBandwidth(10000); 
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Activate throttling on OutputStreams.
     * 
     * @param bandwidth the available bandwidth for OutputStreams.
     */
    public void setThrottle(Bandwidth bandwidth)
    {
        bandwidth_ = bandwidth;
    }


    //--------------------------------------------------------------------------
    // Overrides FilterOutputStream
    //--------------------------------------------------------------------------
    
    /**
     * @see java.io.OutputStream#write(int)
     */
    public void write(final int b) throws IOException
    {
        bandwidth_.getBandwidth(1);
        out.write(b);
        
        if (reservedBandwidth_ > 0)
        {
            bandwidth_.putBandwidth(reservedBandwidth_);
            reservedBandwidth_ = 0;
        }
    }


    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    public void write(byte[] data, int offset, int totalLength)
        throws IOException
    {
        while (totalLength > 0)
        {
            int length = bandwidth_.getBandwidth(totalLength);
            out.write(data, offset, length);
            totalLength -= length;
            offset += length;
        }
        
        if (reservedBandwidth_ > 0)
        {
            bandwidth_.putBandwidth(reservedBandwidth_);
            reservedBandwidth_ = 0;
        }
    }
}

/**
 * Gets a ThrottledOutputStream based on the given OutputStream. If
 * throttling is turned off (by calling {@ref #setThrottle setThrottle}with
 * a zero or negative argument) then the given OutputStream is simply
 * returned.
 * 
 * @param out the OutputStream to throttle.
 * @return an OutputStream which is either the original OutputStream if
 *         throttling is turned off, or a new ThrottledOutputStream if not.
 */
//public static OutputStream throttledStream(OutputStream out)
//{
//    if (bandwidth_ == null || bandwidth_.getBandwidthPerTick() <= 0)
//        return out;
//    
//    logger_.debug("ThrottledOutput, creating new stream, bpt = "
//        + bandwidth_.getBandwidthPerTick());
//
//    return new ThrottledOutputStream(out);
//}

