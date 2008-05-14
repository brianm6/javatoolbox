package toolbox.util.io.relay;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * A StreamRelay transfers bytes from an InputStream to an OutputStream
 * asynchronously in conjunction with a {@link java.lang.Thread}. When the 
 * end of the stream is reached, both stream are closed.
 * <p>
 * Example:
 * <pre>
 * InputStream is = new StringInputStream();
 * OutputStream os = new StringOutputStream();
 * StreamRelay relay = new StreamRelay(is, os);
 * Thread t = new Thread(relay);
 * t.start();
 * </pre>
 */
public class StreamRelay implements Runnable {

    private static final Logger logger_ = Logger.getLogger(StreamRelay.class);
    
    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /**
     * Size of data window.
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * Default flushing policy is not to flush at all.
     */
    private static final IFlushPolicy DEFAULT_FLUSH_POLICY = new NeverFlushPolicy();
    
    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /**
     * InputStream data is read from.
     */
    private InputStream in_;

    /**
     * OutputStream data is forwarded to.
     */
    private OutputStream out_;

    /**
     * Number of bytes transferred.
     */
    private int count_;

    /**
     * Copy buffer.
     */
    private byte[] buffer_;

    /**
     * Number of read operations executed on input stream.
     */
    private int iterations_;

    /**
     * Flushing policy that controls how often the output stream is flushed.
     */
    private IFlushPolicy flushPolicy_ = DEFAULT_FLUSH_POLICY;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Creates a new relay between an input and output stream.
     * 
     * @param in Input stream to read bytes from.
     * @param out Output stream to write bytes to.
     */
    public StreamRelay(InputStream in, OutputStream out) {
        this(in, out, DEFAULT_FLUSH_POLICY);
    }

    public StreamRelay(InputStream in, OutputStream out, IFlushPolicy flushPolicy) {
        in_ = in;
        out_ = out;
        count_ = 0;
        buffer_ = new byte[BUFFER_SIZE];
        iterations_ = 0;
        flushPolicy_ = flushPolicy;
    }
    
    // -------------------------------------------------------------------------
    // Runnable Interface
    // -------------------------------------------------------------------------

    /**
     * Starts the relay.
     */
    public void run() {
        int n;
        
        Timer t = new Timer(true);
        
        if (logger_.isTraceEnabled()) {
            
            // Schedule a task to dump the avg number of bytes relayed per 
            // second
            
            t.schedule(new TimerTask() {
                
                public void run() {
                    logger_.trace(
                        "avg bytes per read = " 
                        + ((iterations_ > 0) 
                            ? (count_ / (float) iterations_) + "" 
                            : "n/a"));
                }
            }, 1000, 1000);
        }
        
        try {
            while ((n = in_.read(buffer_)) > 0) {
                out_.write(buffer_, 0, n);
                
                // Let the flush policy decide how often to flush
                if (flushPolicy_.shouldFlush(out_))
                    out_.flush();
                
                count_ += n;
                iterations_++;
            }
        }
        catch (IOException e) {
            logger_.error(e); // Ignore
        }
        finally {
            IOUtils.closeQuietly(in_);
            IOUtils.closeQuietly(out_);
            t.cancel();
        }
    }
}