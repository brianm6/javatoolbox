package toolbox.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import toolbox.util.ArrayUtil;

/**
 * Output stream that generates events for common stream operations and also
 * tracks the number of bytes written to the stream.
 */
public class EventOutputStream extends FilterOutputStream
{
    /**
     * Array of registered listeners
     */
    private Listener[] listeners_;
    
    /**
     * Total number of bytes written to the stream
     */
    private int count_;
    
    /**
     * Friendly name for this stream
     */
    private String name_;
    
    /**
     * Number of bytes written last time a sample was taken
     */
    private int lastSample_;
    
    /** 
     * Sample period in milliseconds
     */
    private long samplePeriod_ = 1000;

    /**
     * Timer that takes samples to monitor the stream throughput
     */    
    private Timer throughputTimer_;
    
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
        this(null, out);
    }
    
    /**
     * Creates an EventOutputStream with the given name outputstream to
     * decorate.
     * 
     * @param  name Stream name
     * @param  out  OutputStream to chain 
     */
    public EventOutputStream(String name, OutputStream out)
    {
        super(out);
        name_ = name;
        listeners_ = new Listener[0];
        count_ = 0;
    }
    
    //--------------------------------------------------------------------------
    // Overridden from java.io.FilterOutputStream
    //--------------------------------------------------------------------------
    
    /**
     * Writes byte to stream keeping track of the count
     * 
     * @param  b  Byte to write 
     * @throws IOException on I/O error
     */
    public void write(int b) throws IOException
    {
        super.write(b);
        count_++;
        fireByteWritten(b);
    }

    /**
     * Hooks for when the stream is flushed
     * 
     * @see java.io.FilterOutputStream#flush()
     */
    public void flush() throws IOException
    {
        super.flush();
        fireStreamFlushed();
    }
    
    /**
     * Hooks for when the stream is closed
     * 
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
        // Stored in array for faster access
        listeners_ = (Listener[]) ArrayUtil.add(listeners_, listener);
    }
    
    /** 
     * Fires notification that the stream was closed
     */
    protected void fireStreamClosed()
    {
        switch (listeners_.length)
        {
            case 0 : return;
            case 1 : listeners_[0].streamClosed(this); return;
            default: for (int i=0, n=listeners_.length; i<n; i++)
                        listeners_[i].streamClosed(this);
        }
    }

    /** 
     * Fires notification that the stream was flushed
     */
    protected void fireStreamFlushed()
    {
        switch (listeners_.length)
        {
            case 0 : return;
            case 1 : listeners_[0].streamFlushed(this); return;
            default: for (int i=0, n=listeners_.length; i<n; i++)
                        listeners_[i].streamFlushed(this);
        }
    }

    /** 
     * Fires notification that a byte was written to the stream
     * 
     * @param  b  Byte written to stream
     */
    protected void fireByteWritten(int b)
    {
        //
        // Throughput in bytes/second using various strategies for 
        // notification
        //
        
        // 4.7 million
        //for (int i=0, n=listeners_.size(); i<n; i++)
        //    ((Listener) listeners_.get(i)).byteWritten(this, b);  
        
        // 7.3 million
        //((Listener) listeners_.get(0)).byteWritten(this, b);
        
        // 10.5 million
        //if (listener == null)
        //    listener = (Listener) listeners_.get(0);
        //    
        //listener.byteWritten(this, b);
        
        // 7.9 million
        //if (larray == null)
        //{
        //    larray = new Listener[listeners_.size()];
        //    for (int i=0; i<larray.length; i++)
        //        larray[i] = (Listener) listeners_.get(i);
        //}
        
        //for (int i=0, n=larray.length; i<n; i++)
        //    larray[i].byteWritten(this, b);
                
        // 9.5 million                
        //        if (larray == null)
        //        {
        //            larray = new Listener[listeners_.size()];
        //            for (int i=0; i<larray.length; i++)
        //                larray[i] = (Listener) listeners_.get(i);
        //        }
        //        
        //        if (larray.length == 1)
        //            larray[0].byteWritten(this, b);
        //        else
        //            for (int i=0, n=larray.length; i<n; i++)
        //                larray[i].byteWritten(this, b);
        
        // 9.8 million
        //        if (listenerArray_.length == 1)
        //            listenerArray_[0].byteWritten(this, b);
        //        else
        //            for (int i=0, n=listenerArray_.length; i<n; i++)
        //                listenerArray_[i].byteWritten(this, b);
                
        // 9.3 million
        //for (int i=0, n=listenerArray_.length; i<n; i++)
        //    listenerArray_[i].byteWritten(this, b);
               
               
        // 18.1 million - no code        
        
        // 10.2 millions - best compromise
        switch (listeners_.length)
        {
            case 0 : return;
            case 1 : listeners_[0].byteWritten(this, b); return;
            default: for (int i=0, n=listeners_.length; i<n; i++)
                        listeners_[i].byteWritten(this, b);
        }
    }

    /** 
     * Fires notification of stream throughput per sample period
     * 
     * @param  throughput  Number of bytes written per sample period
     */
    protected void fireStreamThroughput(float throughput)
    {
        switch (listeners_.length)
        {
            case 0 : return;
            case 1 : listeners_[0].streamThroughput(this, throughput); 
                     return;
            default: for (int i=0, n=listeners_.length; i<n; i++)
                        listeners_[i].streamThroughput(this, throughput);
        }
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
    
    /**
     * @return Stream name
     */
    public String getName()
    {
        return name_;
    }
    
    /**
     * Starts monitoring of stream throughtput
     * 
     * @see #streamThroughput()
     */
    public void startThroughputMonitor()
    {
        lastSample_ = 0;
        throughputTimer_ = new Timer(false);
        throughputTimer_.schedule(new ThroughputTask(), 0, samplePeriod_);
    }

    /**
     * Stops monitoring of stream throughput
     * 
     * @see #unmonitorThroughput()
     */    
    public void stopThroughputMonitor()
    {
        throughputTimer_.cancel();
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
        
        /**
         * Notification of the number of bytes transfered through the stream
         * over the last sample period
         * 
         * @param stream          Stream being monitored for throughput
         * @param bytesPerPeriod  Number of bytes transferred over sample period
         */
        public void streamThroughput(EventOutputStream stream, 
            float bytesPerPeriod);
    }
    
    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    /** 
     * Task that collects samples on stream throughput and firesNotification
     * to interested listeners.
     */
    class ThroughputTask extends TimerTask
    {
        public void run()
        {
            int current = getCount();
            
            fireStreamThroughput(
                (current - lastSample_)/(samplePeriod_/1000));
                
            lastSample_ = current;
        }
    }
}
