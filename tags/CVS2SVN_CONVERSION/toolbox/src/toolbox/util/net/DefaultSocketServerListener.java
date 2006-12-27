package toolbox.util.net;

import java.net.Socket;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

/**
 * Default listener that allows a client to "wait" for things to happen instead
 * of "listening". 
 */        
public class DefaultSocketServerListener implements ISocketServerListener
{
    private static final Logger logger_ =
        Logger.getLogger(DefaultSocketServerListener.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Queue for accepted events.
     */    
    private BlockingQueue accepted_;
    
    /**
     * Queue for started events.
     */
    private BlockingQueue started_;
    
    /**
     * Queue for stopped events.
     */
    private BlockingQueue stopped_;
    
    /**
     * Queue for connection handled events.
     */
    private BlockingQueue handled_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a DefaultSocketServerListener.
     */
    public DefaultSocketServerListener()
    {
        accepted_ = new LinkedBlockingQueue();
        started_  = new LinkedBlockingQueue();
        handled_  = new LinkedBlockingQueue();
        stopped_  = new LinkedBlockingQueue();
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Wait for a server socket to accept().
     * 
     * @return Connection after its socket has been accepted.
     * @throws InterruptedException on error.
     */
    public IConnection waitForAccept() throws InterruptedException
    {
        return (IConnection) accepted_.take();
    }
    
    
    /**
     * Waits for a server socket to startup successfully.
     * 
     * @return Socket server after it has been started. 
     * @throws InterruptedException on error.
     */
    public SocketServer waitForStart() throws InterruptedException
    {
        return (SocketServer) started_.take();
    }

    
    /**
     * Waits for a server socket to stop.
     * 
     * @return Socket server after it has been stopped. 
     * @throws InterruptedException on error.
     */
    public SocketServer waitForStop() throws InterruptedException
    {
        return (SocketServer) stopped_.take();
    }

    
    /**
     * Waits for connection to be handled successfully.
     * 
     * @return IConnectionHandler Connection handler. 
     * @throws InterruptedException on error.
     */
    public IConnectionHandler waitForHandled() throws InterruptedException
    {
        return (IConnectionHandler) handled_.take();
    }
    
    //--------------------------------------------------------------------------
    // ISocketServerListener Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.net.ISocketServerListener#socketAccepted(
     *      java.net.Socket, toolbox.util.net.IConnection)
     */
    public void socketAccepted(Socket socket, IConnection connection)
    {
        try
        {
            accepted_.put(connection);
        }
        catch (InterruptedException e)
        {
            logger_.error(e);
        }
    }

    
    /**
     * @see toolbox.util.net.ISocketServerListener#serverStarted(
     *      toolbox.util.net.SocketServer)
     */
    public void serverStarted(SocketServer server)
    {
        try
        {
            started_.put(server);
        }
        catch (InterruptedException e)
        {
            logger_.error(e);
        }            
    }
    
    
    /**
     * @see toolbox.util.net.ISocketServerListener#connectionHandled(
     *      toolbox.util.net.IConnectionHandler)
     */
    public void connectionHandled(IConnectionHandler connectionHandler)
    {
        try
        {
            handled_.put(connectionHandler);
        }
        catch (InterruptedException e)
        {
            logger_.error(e);
        }
    }
    
    
    /**
     * @see toolbox.util.net.ISocketServerListener#serverStopped(
     *      toolbox.util.net.SocketServer)
     */
    public void serverStopped(SocketServer server)
    {
        try
        {
            stopped_.put(server);
        }
        catch (InterruptedException e)
        {
            logger_.error(e);
        }
    }
}