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
    // TODO: Write unit test.
    
    private static final Logger logger_ = Logger.getLogger(
        ThrottledOutputStream.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Bandwidth by which this stream will be limited.
     */
    private Bandwidth bandwidth_;
    
    /**
     * Reserved bandwidth.
     */
    private int reservedBandwidth_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a ThrottledOutputStream.
     * 
     * @param out Outputstream to throttle.
     */
    public ThrottledOutputStream(OutputStream out)
    {
        super(out);
        
        // Force the use of a static method that checks if throttling
        // is on before creating a throttled stream.
        // slow down the creation of new streams if bandwidth is low.
        
        if (bandwidth_ != null && bandwidth_.getBandwidthPerTick() > 0)
            reservedBandwidth_ = bandwidth_.getBandwidth(10000);
        else
            reservedBandwidth_ = 0;
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the throttle for this stream.
     * 
     * @param bandwidth Available bandwidth for this stream.
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