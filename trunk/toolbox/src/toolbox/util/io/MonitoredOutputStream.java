package toolbox.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import toolbox.util.io.throughput.DefaultThroughputMonitor;
import toolbox.util.io.throughput.ThroughputMonitor;
import toolbox.util.io.transferred.DefaultTransferredMonitor;
import toolbox.util.io.transferred.TransferredMonitor;

/**
 * MonitoredOutputStream supports the following features.
 * <ul>
 *   <li>Monitoring of throughput in bytes/interval (observable)
 *   <li>Monitoring of bytes transferred every x number of bytes (observable)
 *   <li>Monitoring of the total bytes written (polling)
 *   <li>Monitoring of significant stream events (close, flush,etc) (observable)
 * </ul>
 */
public class MonitoredOutputStream extends FilterOutputStream 
{
    private static final Logger logger_ = 
        Logger.getLogger(MonitoredOutputStream.class);
    
    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    /** 
     * Friendly name for this stream.
     */
    private String name_;

    /**
     * Stream throughput monitor.
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
     * Creates an MonitoredOutputStream.
     * 
     * @param out OutputStream to chain. 
     */
    public MonitoredOutputStream(OutputStream out)
    {
        this(null, out);
    }
    
    
    /**
     * Creates an MonitoredOutputStream with the given name outputstream to
     * decorate.
     * 
     * @param name Stream name.
     * @param out OutputStream to chain. 
     */
    public MonitoredOutputStream(String name, OutputStream out)
    {
        super(out);
        setName(name);
        setThroughputMonitor(new DefaultThroughputMonitor());
        setTransferredMonitor(new DefaultTransferredMonitor());
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.io.FilterOutputStream
    //--------------------------------------------------------------------------
    
    /**
     * Writes byte to stream keeping track of the count.
     * 
     * @param b Byte to write. 
     * @throws IOException on I/O error.
     */
    public void write(int b) throws IOException
    {
        super.write(b);
        throughputMonitor_.newBytesTransferred(1);
        transferredMonitor_.newBytesTransferred(1);
        //transferredProxy_.bytesTransferred(1);
        //fireByteWritten(b);
    }

    
    /**
     * Hooks for when the stream is flushed.
     * 
     * @see java.io.FilterOutputStream#flush()
     */
    public void flush() throws IOException
    {
        super.flush();
//        fireStreamFlushed();
    }
    
    
    /**
     * Hooks for when the stream is closed.
     * 
     * @see java.io.FilterOutputStream#close()
     */
    public void close() throws IOException
    {
        try
        {
            super.close();
        }
        finally
        {
//            fireStreamClosed();
        }
    }
    
    //--------------------------------------------------------------------------
    // ThroughputMonitor Interface
    //--------------------------------------------------------------------------
    
    /** 
     * Fires notification that the stream was closed.
     */
//    protected void fireStreamClosed()
//    {
//        switch (listeners_.length)
//        {
//            case 0 : return;
//            case 1 : listeners_[0].streamClosed(this); return;
//            default: for (int i = 0, n = listeners_.length; i < n; i++)
//                        listeners_[i].streamClosed(this);
//        }
//    }
//
//    
//    /** 
//     * Fires notification that the stream was flushed.
//     */
//    protected void fireStreamFlushed()
//    {
//        switch (listeners_.length)
//        {
//            case 0 : return;
//            case 1 : listeners_[0].streamFlushed(this); return;
//            default: for (int i = 0, n = listeners_.length; i < n; i++)
//                        listeners_[i].streamFlushed(this);
//        }
//    }

    
//    /** 
//     * Fires notification that a byte was written to the stream.
//     * 
//     * @param b Byte written to stream.
//     */
//    protected void fireByteWritten(int b)
//    {
//        //
//        // Throughput in bytes/second using various strategies for 
//        // notification
//        //
//        
//        // 4.7 million
//        //for (int i=0, n=listeners_.size(); i<n; i++)
//        //    ((Listener) listeners_.get(i)).byteWritten(this, b);  
//        
//        // 7.3 million
//        //((Listener) listeners_.get(0)).byteWritten(this, b);
//        
//        // 10.5 million
//        //if (listener == null)
//        //    listener = (Listener) listeners_.get(0);
//        //    
//        //listener.byteWritten(this, b);
//        
//        // 7.9 million
//        //if (larray == null)
//        //{
//        //    larray = new Listener[listeners_.size()];
//        //    for (int i=0; i<larray.length; i++)
//        //        larray[i] = (Listener) listeners_.get(i);
//        //}
//        
//        //for (int i=0, n=larray.length; i<n; i++)
//        //    larray[i].byteWritten(this, b);
//                
//        // 9.5 million                
//        //        if (larray == null)
//        //        {
//        //            larray = new Listener[listeners_.size()];
//        //            for (int i=0; i<larray.length; i++)
//        //                larray[i] = (Listener) listeners_.get(i);
//        //        }
//        //        
//        //        if (larray.length == 1)
//        //            larray[0].byteWritten(this, b);
//        //        else
//        //            for (int i=0, n=larray.length; i<n; i++)
//        //                larray[i].byteWritten(this, b);
//        
//        // 9.8 million
//        //        if (listenerArray_.length == 1)
//        //            listenerArray_[0].byteWritten(this, b);
//        //        else
//        //            for (int i=0, n=listenerArray_.length; i<n; i++)
//        //                listenerArray_[i].byteWritten(this, b);
//                
//        // 9.3 million
//        //for (int i=0, n=listenerArray_.length; i<n; i++)
//        //    listenerArray_[i].byteWritten(this, b);
//               
//               
//        // 18.1 million - no code        
//        
//        // 10.2 millions - best compromise
//        switch (listeners_.length)
//        {
//            case 0 : return;
//            case 1 : listeners_[0].byteWritten(this, b); return;
//            default: for (int i = 0, n = listeners_.length; i < n; i++)
//                        listeners_[i].byteWritten(this, b);
//        }
//    }

    
    /** 
     * Fires notification of stream throughput per sample period.
     * 
     * @param throughput Number of bytes written per sample period.
     */
//    protected void fireStreamThroughput(float throughput)
//    {
//        switch (listeners_.length)
//        {
//            case 0 : return;
//            case 1 : listeners_[0].streamThroughput(this, throughput); 
//                     return;
//            default: for (int i = 0, n = listeners_.length; i < n; i++)
//                        listeners_[i].streamThroughput(this, throughput);
//        }
//    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the number of bytes written to the stream.
     * 
     * @return long
     */
    public long getCount()
    {
        return getTransferredMonitor().getBytesTransferred();
    }
        
    
    /**
     * Returns the stream name.
     * 
     * @return String
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
    public void setThroughputMonitor(DefaultThroughputMonitor throughputMonitor)
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
        void streamClosed(MonitoredOutputStream stream);
        
        
        /**
         * Notification that the stream was flushed.
         * 
         * @param stream Stream that was flushed.
         */
        void streamFlushed(MonitoredOutputStream stream);
        
        
        /**
         * Notification that data was written to the stream.
         * 
         * @param stream Stream data was written to.
         * @param b Byte written to the stream.
         */
        void byteWritten(MonitoredOutputStream stream, int b);
        
        
        /**
         * Notification of the number of bytes transfered through the stream
         * over the last sample period.
         * 
         * @param stream Stream being monitored for throughput.
         * @param bytesPerPeriod Number of bytes transferred over sample period.
         */
        void streamThroughput(MonitoredOutputStream stream, float bytesPerPeriod);
    }
}