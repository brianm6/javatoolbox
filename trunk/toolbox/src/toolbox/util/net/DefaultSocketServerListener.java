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
        
    private BlockingQueue accepted_ = new BlockingQueue();
    private BlockingQueue started_  = new BlockingQueue();
    
    //--------------------------------------------------------------------------
    // ISocketServerListener Interface
    //--------------------------------------------------------------------------
        
    public void socketAccepted(Socket socket, IConnection connection)
    {
        //SocketServerTest.logger_.info("Listener notified of accept on socket " + socket);
        
        try
        {
            accepted_.push(connection);
        }
        catch (InterruptedException e)
        {
            logger_.error("socketAccepted", e);
        }
    }
    
    public void serverStarted(SocketServer server)
    {
        try
        {
            started_.push(server);
        }
        catch (InterruptedException e)
        {
            logger_.error("serverStarted", e);
        }            
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * @return Socket after it has been accepted
     * @throws InterruptedException on error
     */
    public IConnection waitForAccept() throws InterruptedException
    {
        return (IConnection) accepted_.pull();
    }
    
    /**
     * @return Socket server after it has been started 
     * @throws InterruptedException on error
     */
    public SocketServer waitForStart() throws InterruptedException
    {
        return (SocketServer) started_.pull();
    }
}