package toolbox.util.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;

/**
 * An OutputStream that multicasts stream operations to one or more registered 
 * OutputStreams. 
 * <p>
 * Example:
 * <pre>
 * MulticastOutputStream mos = new MulticastOutputStream();
 * 
 * try
 * {
 *     mos.addStream(new FileOutputStream("file.txt"));
 *     mos.addStream(new PipedOutputStream());
 *     mos.addStream(new DataOutputStream(new StringOutputStream()));
 *     mos.write("hey ya!".getBytes());
 * }
 * catch (Exception e)
 * {
 *     System.err.println(e);
 * }
 * finally
 * {
 *      StreamUtil.close(mos);
 * }
 * </pre>
 *
 * @see toolbox.util.io.MulticastOutputStreamTest
 */
public class MulticastOutputStream extends OutputStream
{
    private static final Logger logger_ = 
        Logger.getLogger(MulticastOutputStream.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Members of the multicast group of streams. 
     */
    private OutputStream[] streams_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an empty MulticastOutputStream.
     */
    public MulticastOutputStream()
    {
        streams_ = new OutputStream[0];
    }
    
    
    /**
     * Creates a MulticastOutputStream.
     * 
     * @param out Stream to add to the multicast group.
     */
    public MulticastOutputStream(OutputStream out)
    {
        streams_ = new OutputStream[0];
        addStream(out);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Adds a stream to the multicast group.
     * 
     * @param out Stream to add.
     */
    public synchronized void addStream(OutputStream out)
    {
        streams_ = (OutputStream[]) 
            ArrayUtil.add(streams_, new BufferedOutputStream(out));
    }

    
    /**
     * Removes a stream from the multicast group.
     * 
     * @param out Stream to remove.
     */
    public synchronized void removeStream(OutputStream out)
    {
        streams_ = (OutputStream[]) ArrayUtil.remove(streams_, out);
    }

    //--------------------------------------------------------------------------
    // Overrides java.io.InputStream
    //--------------------------------------------------------------------------

    /**
     * Writes an integer to each stream in the multicast group. If one of the 
     * writes should fail, the failure is logged and the operation continues. 
     * 
     * @param b Integer to write.
     * @throws IOException but really logs them instead.
     */
    public synchronized void write(int b) throws IOException
    {
        for (int i = 0; i < streams_.length; i++)
        {
            try
            {
                streams_[i].write(b);
            }
            catch (IOException ioe)
            {
                logger_.error(ioe);
            }
        }
    }

    
    /**
     * Flushes all streams in the multicast group. If one of the streams should
     * fail, the failure is logged and the operation continues.
     * 
     * @throws IOException but really logs them instead. 
     */
    public synchronized void flush() throws IOException
    {
        for (int i = 0; i < streams_.length; i++)
        {
            try
            {
                streams_[i].flush();
            }
            catch (IOException ioe)
            {
                logger_.error(ioe);
            }
        }
    }

    
    /**
     * Closes all streams in the multicast group. If one of the streams should
     * fail, the failure is logged and the operation continues.
     * 
     * @throws IOException but really logs them instead.
     */
    public synchronized void close() throws IOException
    {
        for (int i = 0; i < streams_.length; i++)
        {    
            try
            {
                streams_[i].close();
            }
            catch (IOException ioe)
            {
                logger_.error(ioe);
            }
        }
    }
}