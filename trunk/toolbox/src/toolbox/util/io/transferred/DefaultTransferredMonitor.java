package toolbox.util.io.transferred;

import toolbox.util.ArrayUtil;

/**
 * DefaultTransferredMonitor is a basic implementation of a TransferredMonitor.
 * 
 * @see toolbox.util.io.transferred.TransferredListener
 * @see toolbox.util.io.transferred.TransferredEvent
 */
public class DefaultTransferredMonitor implements TransferredMonitor {

    //--------------------------------------------------------------------------
    // Defaults Constants
    //--------------------------------------------------------------------------
    
    /**
     * Default sample length is 1K.
     */
    private static final int DEFAULT_SAMPLE_LENGTH = 1024;
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /** 
     * Array of registered listeners. 
     */
    private TransferredListener[] listeners_;

    /**
     * Number of bytes read between each transferred notification.
     */
    private int length_;

    /**
     * Total number of bytes transferred across the channel.
     */
    private int totalTransferred_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    public DefaultTransferredMonitor() 
    {
        length_ = DEFAULT_SAMPLE_LENGTH;
        totalTransferred_ = 0;
        listeners_ = new TransferredListener[0];
    }
    
    //--------------------------------------------------------------------------
    // TransferredMonitor Interface
    //--------------------------------------------------------------------------

	/**
	 * @see toolbox.util.io.transferred.TransferredMonitor#addTransferredListener(
     *      toolbox.util.io.transferred.TransferredListener)
	 */
	public void addTransferredListener(TransferredListener listener) 
    {
        listeners_ = (TransferredListener[]) ArrayUtil.add(listeners_, listener);
	}

    
	/**
	 * @see toolbox.util.io.transferred.TransferredMonitor
     *      #removeTransferredListener(
     *      toolbox.util.io.transferred.TransferredListener)
	 */
	public void removeTransferredListener(TransferredListener listener) 
    {
        listeners_ = (TransferredListener[]) ArrayUtil.add(listeners_, listener);
	}

    
	/**
     * @see toolbox.util.io.transferred.TransferredMonitor#setSampleLength(int)
     */
    public void setSampleLength(int numBytes)
    {
        length_ = numBytes;
    }
    

    /**
     * @see toolbox.util.io.transferred.TransferredMonitor#getSampleLength()
     */
    public int getSampleLength()
    {
        return length_;
    }
    
    
    /**
     * @see toolbox.util.io.transferred.TransferredMonitor#newBytesTransferred(
     *      long)
     */
    public void newBytesTransferred(long count) 
    {
        for (int i = 0; i < count; i++) 
        {
            ++totalTransferred_;
            
            if ((totalTransferred_ % getSampleLength()) == 0)
                fireBytesTransferred();
        }
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /** 
     * Fires notification of stream transferred per sample period.
     * 
     * @param transferred Number of bytes written per sample period.
     */
    protected void fireBytesTransferred()
    {
        if (listeners_.length == 0)
            return;
        
        TransferredEvent event = null;
        
        // Sync on reset of bytesTransferred
        synchronized (this) 
        {
            
            event = new TransferredEvent(
                DefaultTransferredMonitor.this,
                getSampleLength(),
                totalTransferred_);
        }
        
        for (int i = 0, n = listeners_.length; i < n; i++)
            listeners_[i].bytesTransferred(event);
    }
}