package toolbox.util.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import toolbox.util.ArrayUtil;

/**
 * Input stream that generates events for common stream operations and also
 * tracks the number of bytes read from the stream.
 */
public class EventInputStream extends FilterInputStream
{
    /**
     * List of registered listeners interested in stream events.
     */
    private Listener[] listeners_;
    
    /**
     * Total number of bytes read from the stream.
     */
    private int count_;
    
    /**
     * Friendly name for this stream.
     */
    private String name_;
    
    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
        
    /**
     * Creates an EventInputStream.
     * 
     * @param in InputStream to chain 
     */
    public EventInputStream(InputStream in)
    {
        this(null, in);
    }
    
    
    /**
     * Creates an EventInputStream.
     * 
     * @param name Stream name
     * @param in InputStream to chain 
     */
    public EventInputStream(String name, InputStream in)
    {
        super(in);
        name_ = name;
        listeners_ = new Listener[0];
        count_ = 0;
    }
    
    //--------------------------------------------------------------------------
    // Overriddes java.io.FilterInputStream
    //--------------------------------------------------------------------------
    
    /**
     * @see java.io.FilterInputStream#read()
     */
    public int read() throws IOException
    {
        int c = super.read();
        count_ ++;
        fireByteRead(c);
        return c;
    }

    
    /**
     * @see java.io.InputStream#read(byte[], int, int)
     */
    public int read(byte b[], int off, int len) throws IOException 
    {
        int read = in.read(b, off, len);
        
        if (read > 0) 
        {
            count_ += read;
            
            for (int i=0; i<read; i++)
                fireByteRead(b[off+i]);
        }
            
        return read;
    }

    
    /**
     * @see java.io.FilterInputStream#close()
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
     * Adds a Listener to the list of registered stream listeners.
     * 
     * @param listener Listener to register
     */
    public void addListener(Listener listener)
    {
        listeners_ = (Listener[]) ArrayUtil.add(listeners_, listener);
    }
    
    
    /** 
     * Fires notification that the stream was closed.
     */
    protected void fireStreamClosed()
    {
        for (int i=0; i<listeners_.length; i++)
            listeners_[i].streamClosed(this);               
    }

    
    /** 
     * Fires notification that a byte was read from the stream.
     * 
     * @param b Bytes that was read
     */
    protected void fireByteRead(int b)
    {
        for (int i=0; i<listeners_.length; i++)
            listeners_[i].byteRead(this, b);               
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the number of bytes read from the stream.
     * 
     * @return Num bytes read
     */
    public int getCount()
    {
        return count_;
    }
        
    
    /**
     * Resets the number of bytes read back to zero.
     */
    public void resetCount()
    {
        count_ = 0;
    }    
    
    
    /**
     * Returns the friendly name of the stream.
     * 
     * @return Stream name
     */
    public String getName()
    {
        return name_;
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
         * Notification that the stream has been closed.
         * 
         * @param stream Stream that was closed
         */
        public void streamClosed(EventInputStream stream);
        
        
        /**
         * Notification that data was read from the stream.
         * 
         * @param stream Stream data was read from
         * @param b Byte read from the stream
         */
        public void byteRead(EventInputStream stream, int b);
    }
}