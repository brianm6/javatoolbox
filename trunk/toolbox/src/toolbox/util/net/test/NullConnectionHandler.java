package toolbox.util.net.test;

import java.io.InputStream;

import toolbox.util.net.IConnection;
import toolbox.util.net.IConnectionHandler;

/**
 * NullConnectionHandler.
 */
public class NullConnectionHandler implements IConnectionHandler
{
    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Creates a NullConnectionHandler.
     */
    public NullConnectionHandler()
    {
    }

    //--------------------------------------------------------------------------
    // IConnectionHandler Interface 
    //--------------------------------------------------------------------------
    
    /**
     * @see IConnectionHandler#handle(IConnection)
     */
    public Object handle(IConnection conn)
    {
        try
        {
            InputStream is = conn.getInputStream();
            while (is.read() != -1);
        }
        catch (Exception e)
        {
            
            // Ignore
        }
        return null;        
    }
}