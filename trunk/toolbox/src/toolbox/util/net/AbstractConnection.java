package toolbox.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private List listeners_ = new ArrayList();

    /**
     * Connection's name
     */
    private String name_ = "";
        
    //--------------------------------------------------------------------------
    //  IConnection Abstract methods 
    //--------------------------------------------------------------------------
    
    /**
     * @see IConnection#connect()
     */
    public abstract void connect() throws IOException;

    /**
     * @see IConnection#close()
     */
    public abstract void close() throws IOException;

    /**
     * @see IConnection#getInputStream()
     */
    public abstract InputStream getInputStream() throws IOException;

    /**
     * @see IConnection#getOutputStream()
     */
    public abstract OutputStream getOutputStream() throws IOException;

    //--------------------------------------------------------------------------
    //  IConnection implemented methods
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
     * Sets Connection name
     * 
     * @param  name  Name
     */
    public void setName(String name)
    {
        name_ = name;
    }

    //--------------------------------------------------------------------------
    //  Support for IConnectionListener interface
    //--------------------------------------------------------------------------

    /**
     * Adds a connection to the list of listeners
     * 
     * @param  listener  Listener to add
     */
    public void addConnectionListener(IConnectionListener listener)
    {
        listeners_.add(listener);
    }

    /**
     * Removes a connection from the list of listeners
     * 
     * @param  listener  Listener to remove
     */
    public void removeConnectionListener(IConnectionListener listener)
    {
        listeners_.remove(listener);
    }

    /**
     * Fires notification of Connection having been closed
     * 
     * @param  connection  Connection that was closed
     */    
    protected void fireConnectionClosed(IConnection connection)
    {
        for (Iterator i=listeners_.iterator(); i.hasNext(); )
            ((IConnectionListener)i.next()).connectionClosed(connection);
    }
    
    /**
     * Fires notification of Connection being closing
     * 
     * @param  connection  Connection that is being closed
     */    
    protected void fireConnectionClosing(IConnection connection)
    {
        for (Iterator i=listeners_.iterator(); i.hasNext(); )
            ((IConnectionListener)i.next()).connectionClosing(connection);
    }
    
    /**
     * Fires notification that Connection was interrupted
     * 
     * @param  connection  Connection that was interrupted
     */    
    protected void fireConnectionInterrupted(IConnection connection)
    {
        for (Iterator i=listeners_.iterator(); i.hasNext(); )
            ((IConnectionListener)i.next()).connectionInterrupted(connection);
    }
    
    /**
     * Fires notification that Connection was started
     * 
     * @param  connection  Connection that was started
     */    
    protected void fireConnectionStarted(IConnection connection)
    {
        for (Iterator i=listeners_.iterator(); i.hasNext(); )
            ((IConnectionListener)i.next()).connectionStarted(connection);
    }
}