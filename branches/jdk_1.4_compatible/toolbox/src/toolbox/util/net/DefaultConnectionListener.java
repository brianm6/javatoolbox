package toolbox.util.net;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

/**
 * Default implementation of an IConnectionListener that enables synchronous 
 * "waiting" on events vs asynchronous callback listening. 
 */
public class DefaultConnectionListener implements IConnectionListener
{
    private static final Logger logger_ =
        Logger.getLogger(DefaultConnectionListener.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Queue for connection closed events.
     */    
    private BlockingQueue closed_;
    
    /**
     * Queue for connection closing events.
     */
    private BlockingQueue closing_;
    
    /**
     * Queue for connection interrupted events.
     */
    private BlockingQueue interrupted_;
    
    /**
     * Queue for connection started events.
     */
    private BlockingQueue started_; 
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DefaultConnectionListener.
     */
    public DefaultConnectionListener()
    {
        closed_      = new LinkedBlockingQueue();
        closing_     = new LinkedBlockingQueue();
        interrupted_ = new LinkedBlockingQueue();
        started_     = new LinkedBlockingQueue(); 
    }
    
    //--------------------------------------------------------------------------
    // IConnectionListener Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.net.IConnectionListener#connectionClosed(
     *      toolbox.util.net.IConnection)
     */
    public void connectionClosed(IConnection connection)
    {
        logger_.debug("Notification: Connection closed " + connection);
        
        try
        {
            closed_.put(connection);
        }
        catch (InterruptedException e)
        {
            logger_.error(e);
        }
    }
    
    
    /**
     * @see toolbox.util.net.IConnectionListener#connectionClosing(
     *      toolbox.util.net.IConnection)
     */
    public void connectionClosing(IConnection connection)
    {
        logger_.debug("Notification: Connection closing " + connection);
        
        try
        {
            closing_.put(connection);
        }
        catch (InterruptedException e)
        {
            logger_.error(e);
        }
    }
    
    
    /**
     * @see toolbox.util.net.IConnectionListener#connectionInterrupted(
     *      toolbox.util.net.IConnection)
     */
    public void connectionInterrupted(IConnection connection)
    {
        logger_.debug("Notification: Connection interrupted" + connection);
        
        try
        {
            interrupted_.put(connection);
        }
        catch (InterruptedException e)
        {
            logger_.error(e);
        }
    }
    
    
    /**
     * @see toolbox.util.net.IConnectionListener#connectionStarted(
     *      toolbox.util.net.IConnection)
     */
    public void connectionStarted(IConnection connection)
    {
        logger_.debug("Notification: Connection started" + connection);
        
        try
        {
            started_.put(connection);
        }
        catch (InterruptedException e)
        {
            logger_.error(e);
        }
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Blocks indefinitely until a close event is received.
     * 
     * @return IConnection
     * @throws InterruptedException on error.
     */
    public IConnection waitForClose() throws InterruptedException
    {
        return (IConnection) closed_.take();
    }


    /**
     * Blocks indefinitely until a closing event is received.
     * 
     * @return IConnection
     * @throws InterruptedException on error.
     */
    public IConnection waitForClosing() throws InterruptedException
    {
        return (IConnection) closing_.take();
    }
    
    
    /**
     * Blocks indefinitely until a interrupted event is received.
     * 
     * @return IConnection
     * @throws InterruptedException on error.
     */
    public IConnection waitForInterrupted() throws InterruptedException
    {
        return (IConnection) interrupted_.take();
    }


    /**
     * Blocks indefinitely until a started event is received.
     * 
     * @return IConnection
     * @throws InterruptedException on error.
     */
    public IConnection waitForStarted() throws InterruptedException
    {
        return (IConnection) started_.take();
    }
}