package toolbox.util.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import toolbox.util.ArrayUtil;
import toolbox.util.io.throughput.DefaultThroughputMonitor;
import toolbox.util.io.throughput.ThroughputMonitor;
import toolbox.util.io.transferred.DefaultTransferredMonitor;
import toolbox.util.io.transferred.TransferredMonitor;

/**
 * MonitoredInputStream supports the following features.
 * <ul>
 *   <li>Monitoring of throughput in bytes/interval (observable)
 *   <li>Monitoring of bytes transferred every x number of bytes (observable)
 *   <li>Monitoring of the total bytes read (polling)
 *   <li>Monitoring of significant stream events (close, flush,etc) (observable)
 * </ul>
 * 
 * @see toolbox.util.io.throughput.ThroughputMonitor
 * @see toolbox.util.io.transferred.TransferredMonitor 
 */
public class MonitoredInputStream extends FilterInputStream
{
    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    /**
     * Friendly name for this stream.
     */
    private String name_;
    
    /**
     * List of registered listeners interested in stream events.
     */
    private Listener[] listeners_;
    
    /**
     * Bandwidth usage monitor.
     */
    private ThroughputMonitor throughputMonitor_;
    
    /**
     * Bytes transferred monitor.
     */
    private TransferredMonitor transferredMonitor_;
    
    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
        
    /**
     * Creates an MonitoredInputStream.
     * 
     * @param in InputStream to chain. 
     */
    public MonitoredInputStream(InputStream in)
    {
        this(null, in);
    }
    
    
    /**
     * Creates an MonitoredInputStream.
     * 
     * @param name Stream name.
     * @param in InputStream to chain. 
     */
    public MonitoredInputStream(String name, InputStream in)
    {
        super(in);
        setName(name);
        setThroughputMonitor(new DefaultThroughputMonitor());
        setTransferredMonitor(new DefaultTransferredMonitor());
        listeners_ = new Listener[0];
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
        
        if (c != -1) {
            //count_++;
            getThroughputMonitor().newBytesTransferred(1);
            getTransferredMonitor().newBytesTransferred(1);
            
            if (listeners_.length > 0)
                fireBytesRead(new byte[] {(byte) c});
        }
        
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
            //count_ += read;
            
            if (listeners_.length > 0)
                fireBytesRead(ArrayUtil.subset(b, off, off + len - 1));
            
            getThroughputMonitor().newBytesTransferred(read);
            getTransferredMonitor().newBytesTransferred(read);
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
     * @param listener Listener to register.
     */
    public void addListener(Listener listener)
    {
        listeners_ = (Listener[]) ArrayUtil.add(listeners_, listener);
    }
    
    
    /**
     * Removes a Listener from the list of registered stream listeners.
     * 
     * @param listener Listener to remove.
     */
    public void removeListener(Listener listener)
    {
        listeners_ = (Listener[]) ArrayUtil.remove(listeners_, listener);
    }
    
    
    /** 
     * Fires notification that the stream was closed.
     */
    protected void fireStreamClosed()
    {
        for (int i = 0; i < listeners_.length; i++)
            listeners_[i].streamClosed(this);               
    }

    
    /** 
     * Fires notification that a byte was read from the stream.
     * 
     * @param bytes Bytes that was read.
     */
    protected void fireBytesRead(byte[] bytes)
    {
        for (int i = 0; i < listeners_.length; i++)
            listeners_[i].bytesRead(this, bytes);               
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the friendly name of the stream.
     * 
     * @return Stream name.
     */
    public String getName()
    {
        return name_;
    }

    
    /**
     * @param name The name to set.
     */
    public void setName(String name)
    {
        name_ = name;
    }

    
    /**
     * @return Returns the throughputMonitor.
     */
    public ThroughputMonitor getThroughputMonitor()
    {
        return throughputMonitor_;
    }
    
    
    /**
     * @param throughputMonitor The throughputMonitor to set.
     */
    public void setThroughputMonitor(ThroughputMonitor throughputMonitor)
    {
        throughputMonitor_ = throughputMonitor;
    }

    
    /**
     * @return Returns the transferredMonitor.
     */
    public TransferredMonitor getTransferredMonitor()
    {
        return transferredMonitor_;
    }
    
    
    /**
     * @param transferredMonitor The transferredMonitor to set.
     */
    public void setTransferredMonitor(TransferredMonitor transferredMonitor)
    {
        transferredMonitor_ = transferredMonitor;
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
         * @param stream Stream that was closed.
         */
        void streamClosed(MonitoredInputStream stream);
        
        
        /**
         * Notification that data was read from the stream.
         * 
         * @param stream Stream data was read from.
         * @param bytes Bytes read from the stream.
         */
        void bytesRead(MonitoredInputStream stream, byte[] bytes);
    }
    
}