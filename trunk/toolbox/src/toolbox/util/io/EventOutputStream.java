package toolbox.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Output stream that generates events for common stream operations and also
 * tracks the number of bytes written to the stream.
 */
public class EventOutputStream extends FilterOutputStream
{
    /**
     * List of registered Listeners interested in stream events
     */
    private List listeners_;
    
    /**
     * Total number of bytes written to the stream
     */
    private int  count_;
    
    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
        
    /**
     * Creates an EventOutputStream
     * 
     * @param  out  OutputStream to chain 
     */
    public EventOutputStream(OutputStream out)
    {
        super(out);
        listeners_ = new ArrayList(2);
        count_ = 0;
    }
    
    //--------------------------------------------------------------------------
    // Overridden from java.io.FilterOutputStream
    //--------------------------------------------------------------------------
    
    /**
     * Writes byte to stream keeping track of the count
     * 
     * @param  b  Byte to write 
     */
    public void write(int b) throws IOException
    {
        super.write(b);
        count_++;
        fireByteWritten(b);
    }
    
    /**
     * @see java.io.FilterOutputStream#flush()
     */
    public void flush() throws IOException
    {
        super.flush();
        fireStreamFlushed();
    }
    
    /**
     * @see java.io.FilterOutputStream#close()
     */
    public void close() throws IOException
    {
        super.close();
        fireStreamClosed();
    }

    //--------------------------------------------------------------------------
    // Event Listener Support
    //--------------------------------------------------------------------------
    
    /**
     * Adds a Listener to the list of registered stream listeners
     * 
     * @param  listener  Listener to register
     */
    public void addListener(Listener listener)
    {
        listeners_.add(listener);
    }
    
    /** 
     * Fires notification that the stream was closed
     */
    protected void fireStreamClosed()
    {
        for (int i=0; i<listeners_.size(); i++)
            ((Listener) listeners_.get(i)).streamClosed(this);               
    }

    /** 
     * Fires notification that the stream was flushed
     */
    protected void fireStreamFlushed()
    {
        for (int i=0; i<listeners_.size(); i++)
            ((Listener) listeners_.get(i)).streamFlushed(this);               
    }

    /** 
     * Fires notification that a byte was written to the stream
     */
    protected void fireByteWritten(int b)
    {
        for (int i=0; i<listeners_.size(); i++)
            ((Listener) listeners_.get(i)).byteWritten(this, b);               
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * @return  Number of bytes written to the stream
     */
    public int getCount()
    {
        return count_;
    }
        
    /**
     * Resets the number of bytes written back to zero
     */
    public void resetCount()
    {
        count_ = 0;
    }    
    
    //--------------------------------------------------------------------------
    // Interfaces
    //--------------------------------------------------------------------------
    
    /**
     * Listener interface used to notify implementers of activity within the
     * stream.
     */
    public interface Listener
    {
        /**
         * Notification that the stream has been closed
         * 
         * @param  stream  Stream that was closed
         */
        public void streamClosed(EventOutputStream stream);
        
        /**
         * Notification that the stream was flushed
         * 
         * @param  stream  Stream that was flushed
         */
        public void streamFlushed(EventOutputStream stream);
        
        /**
         * Notification that data was written to the stream
         * 
         * @param stream  Stream data was written to
         * @param b       Byte written to the stream
         */
        public void byteWritten(EventOutputStream stream, int b);
    }
}
