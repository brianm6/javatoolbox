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
        
    private BlockingQueue closed_      = new BlockingQueue();
    private BlockingQueue closing_     = new BlockingQueue();
    private BlockingQueue interrupted_ = new BlockingQueue();
    private BlockingQueue started_     = new BlockingQueue(); 
    
    //--------------------------------------------------------------------------
    // IConnectionListener Interface
    //--------------------------------------------------------------------------
    
    public void connectionClosed(IConnection connection)
    {
        logger_.info("Notification: Connection closed " + connection);
        
        try
        {
            closed_.push(connection);
        }
        catch (InterruptedException e)
        {
            logger_.error(e);
        }
    }
    
    public void connectionClosing(IConnection connection)
    {
        logger_.info("Notification: Connection closing " + connection);
        
        try
        {
            closing_.push(connection);
        }
        catch (InterruptedException e)
        {
            logger_.error(e);
        }
    }
    
    public void connectionInterrupted(IConnection connection)
    {
        logger_.info("Notification: Connection interrupted" + connection);
        
        try
        {
            interrupted_.push(connection);
        }
        catch (InterruptedException e)
        {
            logger_.error(e);
        }
    }
    
    public void connectionStarted(IConnection connection)
    {
        logger_.info("Notification: Connection started" + connection);
        
        try
        {
            started_.push(connection);
        }
        catch (InterruptedException e)
        {
            logger_.error(e);
        }
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    public IConnection waitForClose() throws InterruptedException
    {
        return (IConnection) closed_.pull();
    }
    
    public IConnection waitForClosing() throws InterruptedException
    {
        return (IConnection) closing_.pull();
    }
    
    public IConnection waitForInterrupted() throws InterruptedException
    {
        return (IConnection) interrupted_.pull();
    }

    public IConnection waitForStarted() throws InterruptedException
    {
        return (IConnection) started_.pull();
    }
}