package toolbox.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * MulticastOutputStream is an OutputStream that has multicast behavior. 
 * Multiple streams can be added to a multicast group so that writes to the 
 * MulticastOutputStream will be channeled to each stream in the group.
 */
public class MulticastOutputStream extends FilterOutputStream
{
    /** Members fo the multicast group of streams */
    private List streams_ = new ArrayList();

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor
     */
    public MulticastOutputStream()
    {
        super(null);
    }
    
    /**
     * Creates a MulticastOutputStream
     * 
     * @param out Stream to add to the multicast group
     */
    public MulticastOutputStream(OutputStream out)
    {
        super(out);
        addStream(out);
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * Adds a stream to the multicast group
     * 
     * @param  out  Stream to add
     */
    public synchronized void addStream(OutputStream out)
    {
        streams_.add(out);
    }

    /**
     * Removes a stream from the multicast group
     * 
     * @param  out  Stream to remove
     */
    public synchronized void removeStream(OutputStream out)
    {
        streams_.remove(out);
    }

    //--------------------------------------------------------------------------
    //  Overridden from java.io.InputStream
    //--------------------------------------------------------------------------

    /**
     * Writes integer to each stream in the multicast group
     * 
     * @param  b  Integer to write
     * @throws IOException on IO error
     */
    public synchronized void write(int b) throws IOException
    {
        for (Iterator e = streams_.iterator(); e.hasNext();)
            ((OutputStream) e.next()).write(b);
    }

    /**
     * Flushes all streams in the multicast group
     * 
     * @throws IOException on IO error
     */
    public synchronized void flush() throws IOException
    {
        for (Iterator e = streams_.iterator(); e.hasNext();)
            ((OutputStream) e.next()).flush();
    }

    /**
     * Closes all streams in the multicast group
     * 
     * @throws  IOException on IO error
     */
    public synchronized void close() throws IOException
    {
        for (Iterator e = streams_.iterator(); e.hasNext();)
            ((OutputStream) e.next()).close();
    }
}