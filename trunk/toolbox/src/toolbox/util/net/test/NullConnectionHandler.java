package toolbox.util.net.test;

import java.io.InputStream;

import toolbox.util.net.IConnection;
import toolbox.util.net.IConnectionHandler;

/**
 * NullConnectionHandler
 */
public class NullConnectionHandler implements IConnectionHandler
{
    /**
     * Creates a NullConnectionHandler
     */
    public NullConnectionHandler()
    {
    }

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