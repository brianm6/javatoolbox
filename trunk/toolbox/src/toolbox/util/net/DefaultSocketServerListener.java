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
    
    /**
     * Queue for accepted events.
     */    
    private BlockingQueue accepted_ = new BlockingQueue();
    
    /**
     * Queue for started evetnts.
     */
    private BlockingQueue started_  = new BlockingQueue();
    
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
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Wait for a server socket to accept().
     * 
     * @return Connection after its socket has been accepted
     * @throws InterruptedException on error
     */
    public IConnection waitForAccept() throws InterruptedException
    {
        return (IConnection) accepted_.pull();
    }
    
    
    /**
     * Waits for a server socket to startup successfully.
     * 
     * @return Socket server after it has been started 
     * @throws InterruptedException on error
     */
    public SocketServer waitForStart() throws InterruptedException
    {
        return (SocketServer) started_.pull();
    }
}