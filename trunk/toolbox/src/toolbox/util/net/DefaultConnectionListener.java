package toolbox.util.net;

import org.apache.log4j.Logger;

import toolbox.util.concurrent.BlockingQueue;

/**
 * Default implementation of an IConnectionListener that enables synchronous 
 * "waiting" on events vs asynchronous callback listening. 
 */
public class DefaultConnectionListener implements IConnectionListener
{
    private static final Logger logger_ =
        Logger.getLogger(DefaultConnectionListener.class);
    
    /**
     * Queue for connection closed events.
     */    
    private BlockingQueue closed_ = new BlockingQueue();
    
    /**
     * Queue for connection closing events.
     */
    private BlockingQueue closing_ = new BlockingQueue();
    
    /**
     * Queue for connection interrupted events.
     */
    private BlockingQueue interrupted_ = new BlockingQueue();
    
    /**
     * Queue for connection started events.
     */
    private BlockingQueue started_ = new BlockingQueue(); 
    
    //--------------------------------------------------------------------------
    // IConnectionListener Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.net.IConnectionListener#connectionClosed(
     *          toolbox.util.net.IConnection)
     */
    public void connectionClosed(IConnection connection)
    {
        logger_.info("Notification: Connection closed " + connection);
        
        closed_.push(connection);
    }
    
    
    /**
     * @see toolbox.util.net.IConnectionListener#connectionClosing(
     *          toolbox.util.net.IConnection)
     */
    public void connectionClosing(IConnection connection)
    {
        logger_.info("Notification: Connection closing " + connection);
        
        closing_.push(connection);
    }
    
    
    /**
     * @see toolbox.util.net.IConnectionListener#connectionInterrupted(
     *          toolbox.util.net.IConnection)
     */
    public void connectionInterrupted(IConnection connection)
    {
        logger_.info("Notification: Connection interrupted" + connection);
        interrupted_.push(connection);
    }
    
    
    /**
     * @see toolbox.util.net.IConnectionListener#connectionStarted(
     *          toolbox.util.net.IConnection)
     */
    public void connectionStarted(IConnection connection)
    {
        logger_.info("Notification: Connection started" + connection);
        started_.push(connection);
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Blocks indefinitely until a close event is received.
     * 
     * @return IConnection
     * @throws InterruptedException on error
     */
    public IConnection waitForClose() throws InterruptedException
    {
        return (IConnection) closed_.pull();
    }


    /**
     * Blocks indefinitely until a closing event is received.
     * 
     * @return IConnection
     * @throws InterruptedException on error
     */
    public IConnection waitForClosing() throws InterruptedException
    {
        return (IConnection) closing_.pull();
    }
    
    
    /**
     * Blocks indefinitely until a interrupted event is received.
     * 
     * @return IConnection
     * @throws InterruptedException on error
     */
    public IConnection waitForInterrupted() throws InterruptedException
    {
        return (IConnection) interrupted_.pull();
    }


    /**
     * Blocks indefinitely until a started event is received.
     * 
     * @return IConnection
     * @throws InterruptedException on error
     */
    public IConnection waitForStarted() throws InterruptedException
    {
        return (IConnection) started_.pull();
    }
}