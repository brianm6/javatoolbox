package toolbox.util.net;

import java.net.Socket;

import org.apache.log4j.Logger;

import toolbox.util.concurrent.BlockingQueue;

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
        accepted_ = new BlockingQueue();
        started_  = new BlockingQueue();
        handled_  = new BlockingQueue();
        stopped_  = new BlockingQueue();
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
        return (IConnection) accepted_.pull();
    }
    
    
    /**
     * Waits for a server socket to startup successfully.
     * 
     * @return Socket server after it has been started. 
     * @throws InterruptedException on error.
     */
    public SocketServer waitForStart() throws InterruptedException
    {
        return (SocketServer) started_.pull();
    }

    
    /**
     * Waits for a server socket to stop.
     * 
     * @return Socket server after it has been stopped. 
     * @throws InterruptedException on error.
     */
    public SocketServer waitForStop() throws InterruptedException
    {
        return (SocketServer) stopped_.pull();
    }

    
    /**
     * Waits for connection to be handled successfully.
     * 
     * @return IConnectionHandler Connection handler. 
     * @throws InterruptedException on error.
     */
    public IConnectionHandler waitForHandled() throws InterruptedException
    {
        return (IConnectionHandler) handled_.pull();
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
        accepted_.push(connection);
    }

    
    /**
     * @see toolbox.util.net.ISocketServerListener#serverStarted(
     *      toolbox.util.net.SocketServer)
     */
    public void serverStarted(SocketServer server)
    {
        started_.push(server);            
    }
    
    
    /**
     * @see toolbox.util.net.ISocketServerListener#connectionHandled(
     *      toolbox.util.net.IConnectionHandler)
     */
    public void connectionHandled(IConnectionHandler connectionHandler)
    {
        handled_.push(connectionHandler);
    }
    
    
    /**
     * @see toolbox.util.net.ISocketServerListener#serverStopped(
     *      toolbox.util.net.SocketServer)
     */
    public void serverStopped(SocketServer server)
    {
        stopped_.push(server);
    }
}