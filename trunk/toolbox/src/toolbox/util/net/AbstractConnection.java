package toolbox.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import toolbox.util.ArrayUtil;

/**
 * Abstract connection with support for listenter interface. Subclasses are 
 * expected to call the fire* notification methods to satifsy the requirements 
 * of IConnectionListener
 */
public abstract class AbstractConnection implements IConnection
{
    /**
     * List of connection listeners
     */
    private IConnectionListener[] listeners_ = new IConnectionListener[0];

    /**
     * Connection's name
     */
    private String name_ = "";
        
    //--------------------------------------------------------------------------
    // IConnection Abstract methods 
    //--------------------------------------------------------------------------
    
    /**
     * @see IConnection#connect()
     */
    public abstract void connect() throws IOException;

    /**
     * @see toolbox.util.net.IConnection#close()
     */
    public abstract void close() throws IOException;

    /**
     * @see toolbox.util.net.IConnection#getInputStream()
     */
    public abstract InputStream getInputStream() throws IOException;

    /**
     * @see toolbox.util.net.IConnection#getOutputStream()
     */
    public abstract OutputStream getOutputStream() throws IOException;

    //--------------------------------------------------------------------------
    // IConnection implemented methods
    //--------------------------------------------------------------------------
    
    /**
     * Returns name used to easily identify connection's context
     * 
     * @return Connection Name
     */
    public String getName()
    {
        return name_;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the connection name
     * 
     * @param name Connection name
     */
    public void setName(String name)
    {
        name_ = name;
    }

    //--------------------------------------------------------------------------
    // IConnectionListener Event Notification Support
    //--------------------------------------------------------------------------

    /**
     * Adds a connection to the list of listeners
     * 
     * @param listener Listener to add
     */
    public void addConnectionListener(IConnectionListener listener)
    {
        listeners_ = (IConnectionListener[])ArrayUtil.add(listeners_, listener);
    }

    /**
     * Removes a connection from the list of listeners
     * 
     * @param listener Listener to remove
     */
    public void removeConnectionListener(IConnectionListener listener)
    {
        listeners_ = 
            (IConnectionListener[])ArrayUtil.remove(listeners_, listener);
    }

    /**
     * Fires notification of Connection having been closed
     * 
     * @param connection Connection that was closed
     */    
    protected void fireConnectionClosed(IConnection connection)
    {
        for (int i=0; i<listeners_.length; 
            listeners_[i++].connectionClosed(connection));
    }
    
    /**
     * Fires notification of Connection being closing
     * 
     * @param connection Connection that is being closed
     */    
    protected void fireConnectionClosing(IConnection connection)
    {
        for (int i=0; i<listeners_.length; 
            listeners_[i++].connectionClosing(connection));
    }
    
    /**
     * Fires notification that Connection was interrupted
     * 
     * @param connection Connection that was interrupted
     */    
    protected void fireConnectionInterrupted(IConnection connection)
    {
        for (int i=0; i<listeners_.length; 
            listeners_[i++].connectionInterrupted(connection));
    }
    
    /**
     * Fires notification that Connection was started
     * 
     * @param connection Connection that was started
     */    
    protected void fireConnectionStarted(IConnection connection)
    {
        for (int i=0; i<listeners_.length; 
            listeners_[i++].connectionStarted(connection));
    }
}