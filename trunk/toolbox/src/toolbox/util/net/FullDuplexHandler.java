package toolbox.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import toolbox.util.ThreadUtil;

/**
 * Abstract class that facilitates the creation of a handler that handles 
 * input/output in a full duplex manner. That is, neither of the streams
 * are coupled or dependent on one another thus allowing a full duplex mode
 * of communication to take place.
 * 
 * <pre>
 * 
 *   input  thread-&gt;         +--handleInput  &lt;== input stream----+
 *                          /                                     \
 *   caller thread-&gt;  handle                                       connection
 *                          \                                     /
 *   output thread-&gt;         +--handleOutput ==&gt; output stream---+
 * 
 * </pre>
 */
public abstract class FullDuplexHandler implements IConnectionHandler
{
    private static final Logger logger_ = 
        Logger.getLogger(FullDuplexHandler.class);

    /** 
     * Connection to be handled
     */
    private IConnection conn_;

    /**
     * Thread of execution for handling the input stream
     */
    private Thread inputStreamThread_; 
    
    /**
     * Thread of execution for handling the output stream
     */
    private Thread outputStreamThread_;


    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a FullDuplexHandler
     */
    public FullDuplexHandler()
    {
    }

    //--------------------------------------------------------------------------
    // IConnectionHandler interface
    //--------------------------------------------------------------------------

    /**
     * Handles a connection by taking the input/output streams and 
     * spawning them off into separate threads of execution.
     * 
     * @param conn Connection to read/write messages to/from
     * @return Null
     */
    public final Object handle(IConnection conn) 
    {
        conn_ = conn;
        
        // Startup input/output stream handlers on separate threads
        inputStreamThread_  = ThreadUtil.run(this, "handleInput", null);
        outputStreamThread_ = ThreadUtil.run(this, "handleOutput", null);
        
        return null;
    }

    //--------------------------------------------------------------------------
    // Abstract Methods
    //--------------------------------------------------------------------------
    
    /**
     * Handles the inputstream
     * 
     * @throws Exception on error
     */
    public abstract void handleInput() throws Exception;
    
        
    /**
     * Handles the output stream
     * 
     * @throws Exception on error
     */
    public abstract void handleOutput() throws Exception;


    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Mutator for the connection
     * 
     * @param newConnection The connection
     */
    public void setConnection(IConnection newConnection)
    {
        conn_ = newConnection;
    }

    /**
     * Accessor for the connection
     * 
     * @return IConnection
     */
    public IConnection getConnection()
    {
        return conn_;
    }

    /**
     * Convenience accessor for the input stream
     * 
     * @return Inputstream
     * @throws IOException on I/O error
     */    
    public InputStream getInputStream() throws IOException
    {
        return getConnection().getInputStream();
    }
    
    /**
     * Convenience accessor for the output stream
     * 
     * @return OutputStream
     * @throws IOException on I/O error
     */
    public OutputStream getOutputStream() throws IOException
    {
        return getConnection().getOutputStream();
    }
    
    /**
     * Returns the inputStreamThread.
     * 
     * @return Thread
     */
    public Thread getInputStreamThread()
    {
        return inputStreamThread_;
    }

    /**
     * Returns the outputStreamThread.
     * 
     * @return Thread
     */
    public Thread getOutputStreamThread()
    {
        return outputStreamThread_;
    }
}