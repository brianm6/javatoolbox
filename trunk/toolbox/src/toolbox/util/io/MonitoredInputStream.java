package toolbox.util.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import toolbox.util.ArrayUtil;
import toolbox.util.io.throughput.DefaultThroughputMonitor;
import toolbox.util.io.throughput.ThroughputMonitor;
import toolbox.util.io.transferred.DefaultTransferredMonitor;
import toolbox.util.io.transferred.TransferredMonitor;
import toolbox.util.service.Nameable;

/**
 * MonitoredInputStream supports the following features.
 * <ul>
 *   <li>Access to the total number of bytes read.
 *   <li>Notification of throughput in bytes per time interval.
 *   <li>Notification of bytes transferred every set number of bytes.
 *   <li>Notification of significant stream events (close, flush,etc).
 * </ul>
 * 
 * <b>Example:</b>
 * <pre class="snippet">
 *   MonitoredInputStream mis = new MonitoredInputStream(someInputStream);
 *   
 *   // Setup notification of throughput every 1 second...
 *   mis.getThroughputMonitor().addThroughputListener(myThroughputListener);
 *   mis.getThroughputMonitor().setSampleInterval(1000) // 1000 ms = 1 sec
 *   mis.getThrouhgputMonitor().setMonitoringThroughput(true);
 * 
 *   // Setup notification every 5000 bytes transferred...
 *   mis.getTransferredMonitor().addTransferredListener(myTransferredListener);
 *   mis.getTransferredMonitor().setSampleLength(5000);
 * 
 *   // Setup notification of close/flush events...
 *   mis.addInputStreamListener(myInputStreamListener);
 * 
 *   ... read some data from the InputStream ...
 *  
 *   // Find out how many bytes transferred so far...
 *   int total = mis.getTransferredMonitor().getBytesTransferred();
 * 
 *   ... shutdown ....
 * 
 *   mis.getThroughputMonitor().setMonitoringThroughput(false);
 * </pre>
 * @see toolbox.util.io.throughput.ThroughputMonitor
 * @see toolbox.util.io.transferred.TransferredMonitor
 * @see toolbox.util.io.MonitoredOutputStream 
 */
public class MonitoredInputStream extends FilterInputStream implements Nameable
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
    private InputStreamListener[] listeners_;
    
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
        listeners_ = new InputStreamListener[0];
     }
    
    //--------------------------------------------------------------------------
    // Overriddes java.io.FilterInputStream
    //--------------------------------------------------------------------------
    
    /**
     * Notifies monitors that a single byte was transferred.
     * 
     * @see java.io.FilterInputStream#read()
     */
    public int read() throws IOException
    {
        int c = super.read();
        
        if (c != -1) 
        {
            getThroughputMonitor().newBytesTransferred(1);
            getTransferredMonitor().newBytesTransferred(1);
        }
        
        return c;
    }

    
    /**
     * Notifies monitors that an array of bytes was transferred.
     * 
     * @see java.io.InputStream#read(byte[], int, int)
     */
    public int read(byte b[], int off, int len) throws IOException 
    {
        int read = in.read(b, off, len);
        
        if (read > 0) 
        {
            getThroughputMonitor().newBytesTransferred(read);
            getTransferredMonitor().newBytesTransferred(read);
        }
            
        return read;
    }

    
    /**
     * Contains hook to fire notification that this stream was closed.
     * 
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
     * Adds a listener to this stream.
     * 
     * @param listener Listener to receive notifications.
     */
    public void addInputStreamListener(InputStreamListener listener)
    {
        listeners_ = 
            (InputStreamListener[]) ArrayUtil.add(listeners_, listener);
    }
    
    
    /**
     * Removes a listener from this stream.
     * 
     * @param listener Listener that will no longer receive notifications.
     */
    public void removeInputStreamListener(InputStreamListener listener)
    {
        listeners_ = 
            (InputStreamListener[]) ArrayUtil.remove(listeners_, listener);
    }
    
    
    /** 
     * Fires notification that the stream was closed.
     */
    protected void fireStreamClosed()
    {
        for (int i = 0; i < listeners_.length; i++)
            listeners_[i].streamClosed(this);               
    }
    

    //--------------------------------------------------------------------------
    // Nameable Interface
    //--------------------------------------------------------------------------
    
    /**
     * Returns the friendly name of this stream.
     * 
     * @see toolbox.util.service.Nameable#getName()
     */
    public String getName()
    {
        return name_;
    }

    
    /**
     * Sets the friendly name of this stream.
     * 
     * @see toolbox.util.service.Nameable#setName(java.lang.String)
     */
    public void setName(String name)
    {
        name_ = name;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the throughputMonitor associated with this stream.
     * 
     * @return ThroughputMonitor
     */
    public ThroughputMonitor getThroughputMonitor()
    {
        return throughputMonitor_;
    }
    
    
    /**
     * Sets teh throughput monitor to associated with this stream.
     * 
     * @param throughputMonitor The throughputMonitor to set.
     */
    public void setThroughputMonitor(ThroughputMonitor throughputMonitor)
    {
        throughputMonitor_ = throughputMonitor;
    }

    
    /**
     * Returns the transferredMonitor associated with this stream.
     * 
     * @return TransferredMonitor
     */
    public TransferredMonitor getTransferredMonitor()
    {
        return transferredMonitor_;
    }
    
    
    /**
     * Sets the transferred monitor to associate with this stream.
     * 
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
     * Notifications supported by a MonitoredInputStream in addition to 
     * TransferredListener and ThroughputListener.
     */
    public interface InputStreamListener
    {
        /**
         * Notification that the stream has been closed.
         * 
         * @param stream Stream that was closed.
         */
        void streamClosed(MonitoredInputStream stream);
    }
}