package toolbox.util.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import toolbox.util.ArrayUtil;

/**
 * MulticastOutputStream is an OutputStream that has multicast behavior. 
 * Multiple streams can be added to a multicast group so that writes to the 
 * MulticastOutputStream will be channeled to each stream in the group.
 * 
 * @see toolbox.util.io.test.MulticastOutputStreamTest
 */
public class MulticastOutputStream extends OutputStream
{
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
     * @param out Stream to add to the multicast group
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
     * @param out Stream to add
     */
    public synchronized void addStream(OutputStream out)
    {
        streams_ = (OutputStream[]) 
            ArrayUtil.add(streams_, new BufferedOutputStream(out));
    }

    
    /**
     * Removes a stream from the multicast group.
     * 
     * @param out Stream to remove
     */
    public synchronized void removeStream(OutputStream out)
    {
        streams_ = (OutputStream[]) ArrayUtil.remove(streams_, out);
    }

    //--------------------------------------------------------------------------
    // Overrides java.io.InputStream
    //--------------------------------------------------------------------------

    /**
     * Writes integer to each stream in the multicast group.
     * 
     * @param b Integer to write
     * @throws IOException on I/O error
     */
    public synchronized void write(int b) throws IOException
    {
        for (int i=0; i<streams_.length; streams_[i++].write(b));
    }

    
    /**
     * Flushes all streams in the multicast group.
     * 
     * @throws IOException on I/O error
     */
    public synchronized void flush() throws IOException
    {
        for (int i=0; i<streams_.length; streams_[i++].flush());
    }

    
    /**
     * Closes all streams in the multicast group.
     * 
     * @throws IOException on I/O error
     */
    public synchronized void close() throws IOException
    {
        for (int i=0; i<streams_.length; streams_[i++].close());        
    }
}