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
     * Returns connection name for easy identification.
     * 
     * @return Connection name
     */
    String getName();

    
    /**
     * Connects to an endpoint.
     * 
     * @throws IOException on I/O error
     */
    void connect() throws IOException;


    /**
     * Checks the connection to the endpoint has been establisthed.
     * 
     * @return True if connected, false otherwise
     */
    boolean isConnected();
    

    /**
     * Closes the connection.
     * 
     * @throws IOException on I/O error
     */
    void close() throws IOException;


    /**
     * Accessor for the connections' input stream.
     * 
     * @return InputStream
     * @throws IOException on I/O error
     */
    InputStream getInputStream() throws IOException;


    /**
     * Accessor for the connections' output stream.
     * 
     * @return OutputStream
     * @throws IOException on I/O error
     */
    OutputStream getOutputStream() throws IOException;
    
    
    /**
     * Adds a listener to the connection.
     * 
     * @param listener Connection listener to add
     */
    void addConnectionListener(IConnectionListener listener);


    /**
     * Removes a listener from the connection.
     * 
     * @param listener Connection listener to remove
     */
    void removeConnectionListener(IConnectionListener listener);
}
