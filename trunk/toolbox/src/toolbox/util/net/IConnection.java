package toolbox.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Generic Connection interface with I/O streams
 */
public interface IConnection
{
    /**
     * Opens the connection
     */
    public void open();


    /**
     * Closes the connection 
     * 
     * @throws  IOException on I/O error
     */
    public void close() throws IOException;


    /**
     * Accessor for the connections' input stream
     * 
     * @return    InputStream
     * @throws    IOException on I/O error
     */
    public InputStream getInputStream() throws IOException;


    /**
     * Accessor for the connections' output stream
     * 
     * @return    OutputStream
     * @throws    IOException on I/O error
     */
    public OutputStream getOutputStream() throws IOException;
}