package toolbox.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * IConnection models the basic characteristics of a bi-directional stream
 * based connection.
 */
public interface IConnection
{
    /**
     * Returns connection name for easy identification
     * 
     * @return Connection name
     */
    public String getName();

    
    /**
     * Connects to an endpoint
     * 
     * @throws IOException on I/O error
     */
    public void connect() throws IOException;


    /**
     * Checks the connection to the endpoint has been establisthed
     * 
     * @return True if connected, false otherwise
     */
    public boolean isConnected();
    

    /**
     * Closes the connection 
     * 
     * @throws IOException on I/O error
     */
    public void close() throws IOException;


    /**
     * Accessor for the connections' input stream
     * 
     * @return InputStream
     * @throws IOException on I/O error
     */
    public InputStream getInputStream() throws IOException;


    /**
     * Accessor for the connections' output stream
     * 
     * @return OutputStream
     * @throws IOException on I/O error
     */
    public OutputStream getOutputStream() throws IOException;
    
    
    /**
     * Adds a listener to the connection
     * 
     * @param listener Connection listener to add
     */
    public void addConnectionListener(IConnectionListener listener);


    /**
     * Removes a listener from the connection
     * 
     * @param listener Connection listener to remove
     */
    public void removeConnectionListener(IConnectionListener listener);
}
