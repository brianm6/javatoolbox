package toolbox.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.io.throughput.DefaultThroughputMonitor;
import toolbox.util.io.throughput.ThroughputMonitor;
import toolbox.util.io.transferred.DefaultTransferredMonitor;
import toolbox.util.io.transferred.TransferredMonitor;
import toolbox.util.service.Nameable;

/**
 * MonitoredOutputStream is a stream decorator that enables the monitoring of
 * various stream related events and activities. These include:
 * <ul>
 *   <li>Stream throughput in bytes/second or any time interval
 *   <li>Bytes transferred every x number of bytes
 *   <li>Total bytes written
 *   <li>Stream close() and flush() operations
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre class="snippet">
 * 
 *   FileOutputStream myStream = new FileOutputStream("foo.xml");
 *   MonitoredOutputStream mos = new MonitoredOutputStream(myStream);
 *  
 *   // Notification of close() and flush()
 *   mos.addOutputStreamListener(new MyOutputStreamListener());
 * 
 *   // Notification every time 500 bytes are written to the stream
 *   mos.getTransferredMonitor().setSampleLength(500);
 *   mos.getTransferredMonitor().addTransferredListener(new MyTransferredListener());
 * 
 *   // Notification of the streams throughput in bytes/second
 *   mos.getThroughputMonitor().setSampleInterval(1000);
 *   mos.getThroughputMonitor().addThroughputListener(new MyThroughputListener());
 * 
 *   // Have to turn the throughput monitoring on 
 *   mos.getThroughputMonitor().setMonitoringThroughput(true);
 * 
 *   // Write activity
 *   myStream.write("this is a test".getBytes());
 *   myStream.flush();
 * 
 *   // Get count of bytes written to the stream directly
 *   System.out.println("Bytes written: " + mos.getCount());
 * 
 *   // Cleanup
 *   mos.getThroughputMonitor.setMonitoringThroughput(false);
 *   mos.close();
 * </pre>
 * 
 * @see toolbox.util.io.MonitoredInputStream
 */
public class MonitoredOutputStream extends FilterOutputStream 
    implements Nameable 
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
     * Listeners interested in close and flush events.
     */
    private OutputStreamListener[] listeners_;
    
    /**
     * Monitor for the throughput of bytes written to this stream.
     */
    private ThroughputMonitor throughputMonitor_;

    /**
     * Monitor for the number of bytes written to this stream.
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
        listeners_ = new OutputStreamListener[0];
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.io.FilterOutputStream
    //--------------------------------------------------------------------------
    
    /**
     * Updates monitors with the number of bytes transferred.
     * 
     * @see java.io.OutputStream#write(int)
     */
    public void write(int b) throws IOException
    {
        super.write(b);
        throughputMonitor_.newBytesTransferred(1);
        transferredMonitor_.newBytesTransferred(1);
    }

    
    /**
     * Causes firing of stream flushed event.
     * 
     * @see java.io.FilterOutputStream#flush()
     */
    public void flush() throws IOException
    {
        super.flush();
        fireStreamFlushed();
    }
    
    
    /**
     * Causes firing of stream close event and turns monitoring off if the
     * client forgot.
     * 
     * @see java.io.FilterOutputStream#close()
     */
    public void close() throws IOException
    {
        super.close();
        
        // Turn off monitoring if user forgot.
        if (getThroughputMonitor().isMonitoringThroughput())
            getThroughputMonitor().setMonitoringThroughput(false);
        
        fireStreamClosed();
    }
    
    //--------------------------------------------------------------------------
    // Fire Notification Methods
    //--------------------------------------------------------------------------
    
    /** 
     * Fires notification that the stream was closed.
     */
    protected void fireStreamClosed()
    {
        for (int i = 0; i < listeners_.length; i++)
            listeners_[i].streamClosed(this);
    }

    
    /** 
     * Fires notification that the stream was flushed.
     */
    protected void fireStreamFlushed()
    {
        for (int i = 0; i < listeners_.length; i++)
            listeners_[i].streamFlushed(this);
    }

    //--------------------------------------------------------------------------
    // Nameable Interface
    //--------------------------------------------------------------------------
    
    /**
     * Returns the stream name.
     * 
     * @see toolbox.util.service.Nameable#getName()
     */
    public String getName()
    {
        return name_;
    }

    
    /**
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
     * Returns the number of bytes written to the stream.
     * 
     * @return long
     */
    public long getCount()
    {
        return getTransferredMonitor().getBytesTransferred();
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

    
    /**
     * Adds a Listener to the list of registered stream listeners.
     * 
     * @param listener Listener to register.
     */
    public void addOutputStreamListener(OutputStreamListener listener)
    {
        listeners_ = 
            (OutputStreamListener[]) ArrayUtil.add(listeners_, listener);
    }
    
    
    /**
     * Removes a Listener from the list of registered stream listeners.
     * 
     * @param listener Listener to remove.
     */
    public void removeOutputStreamListener(OutputStreamListener listener)
    {
        listeners_ = 
            (OutputStreamListener[]) ArrayUtil.remove(listeners_, listener);
    }
    
    //--------------------------------------------------------------------------
    // Interfaces
    //--------------------------------------------------------------------------
    
    /**
     * Listener interface used to notify implementers of activity within the
     * stream.
     */
    public interface OutputStreamListener
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
    }
}

///** 
//* Fires notification that a byte was written to the stream.
//* 
//* @param b Byte written to stream.
//*/
//protected void fireByteWritten(int b)
//{
// //
// // Throughput in bytes/second using various strategies for 
// // notification
// //
// 
// // 4.7 million
// //for (int i=0, n=listeners_.size(); i<n; i++)
// //    ((Listener) listeners_.get(i)).byteWritten(this, b);  
// 
// // 7.3 million
// //((Listener) listeners_.get(0)).byteWritten(this, b);
// 
// // 10.5 million
// //if (listener == null)
// //    listener = (Listener) listeners_.get(0);
// //    
// //listener.byteWritten(this, b);
// 
// // 7.9 million
// //if (larray == null)
// //{
// //    larray = new Listener[listeners_.size()];
// //    for (int i=0; i<larray.length; i++)
// //        larray[i] = (Listener) listeners_.get(i);
// //}
// 
// //for (int i=0, n=larray.length; i<n; i++)
// //    larray[i].byteWritten(this, b);
//         
// // 9.5 million                
// //        if (larray == null)
// //        {
// //            larray = new Listener[listeners_.size()];
// //            for (int i=0; i<larray.length; i++)
// //                larray[i] = (Listener) listeners_.get(i);
// //        }
// //        
// //        if (larray.length == 1)
// //            larray[0].byteWritten(this, b);
// //        else
// //            for (int i=0, n=larray.length; i<n; i++)
// //                larray[i].byteWritten(this, b);
// 
// // 9.8 million
// //        if (listenerArray_.length == 1)
// //            listenerArray_[0].byteWritten(this, b);
// //        else
// //            for (int i=0, n=listenerArray_.length; i<n; i++)
// //                listenerArray_[i].byteWritten(this, b);
//         
// // 9.3 million
// //for (int i=0, n=listenerArray_.length; i<n; i++)
// //    listenerArray_[i].byteWritten(this, b);
//        
//        
// // 18.1 million - no code        
// 
// // 10.2 millions - best compromise
// switch (listeners_.length)
// {
//     case 0 : return;
//     case 1 : listeners_[0].byteWritten(this, b); return;
//     default: for (int i = 0, n = listeners_.length; i < n; i++)
//                 listeners_[i].byteWritten(this, b);
// }
//}
