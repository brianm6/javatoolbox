package toolbox.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Concrete implementaion of a connection that wraps a TCP socket
 */
public class SocketConnection implements IConnection
{
    /** 
     * Socket being wrapped 
     */
    private Socket socket_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SocketConnection with the given Socket
     * 
     * @param    s    Socket
     */
    public SocketConnection(Socket socket)
    {
        setSocket(socket);
    }

    //--------------------------------------------------------------------------
    //  IConnection Interface
    //--------------------------------------------------------------------------
    
    /**
     * Opens the connection
     */
    public void open()
    {
        // no op
    }


    /**
     * Closes the connection
     * 
     * @throws    IOException
     */
    public void close() throws IOException
    {
        getSocket().close();
    }


    /**
     * Accessor for the input stream
     * 
     * @return    InputStream for connection
     * @throws    IOException 
     */
    public InputStream getInputStream() throws IOException
    {
        return getSocket().getInputStream();
    }


    /**
     * Accessor for the output stream
     * 
     * @return    OutputStream for connection
     * @throws    IOException
     */
    public OutputStream getOutputStream() throws IOException
    {
        return getSocket().getOutputStream();
    }

    //--------------------------------------------------------------------------
    //  Accessors/Mutators
    //--------------------------------------------------------------------------

    /**
     * Mutator for the socket
     * 
     * @param    newSocket    Socket
     */
    protected void setSocket(Socket newSocket)
    {
        socket_ = newSocket;
    }


    /**
     * Accessor for the socket
     * 
     * @return    Socket
     */
    protected Socket getSocket()
    {
        return socket_;
    }
}