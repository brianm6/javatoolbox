package toolbox.util;

import java.io.IOException;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import toolbox.util.net.IConnection;

/**
 * Convenience class that removes the ugliness of closing resources that is so
 * common in managed resources. The premise of this class is to close resources
 * without concern for any exceptional conditions that may arise from doing so.
 * <p>
 * <pre class="snippet">
 * 
 * Before:
 * 
 * try
 * {
 *     resource = new ManagedResource(x,y,z);
 *     resource.doSomething();
 * }
 * catch (Exception e)
 * {
 *     ....
 * }
 * finally
 * {
 *     // Cleanup
 *     if (resource != null)
 *     {
 *         try
 *         {
 *             resource.close();
 *         }
 *         catch (SpecificException se)
 *         {
 *             // Eat any exceptions
 *         } 
 *     }
 * }
 * 
 * After:
 * 
 * ResourceCloser.close(resource);
 * </pre>
 */
public final class ResourceCloser
{
    private static Logger logger_ =
        Logger.getLogger(ResourceCloser.class);

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Prevent construction of this static singleton.
     */
    private ResourceCloser()
    {
    }

    //--------------------------------------------------------------------------
    //  Public 
    //--------------------------------------------------------------------------

    /**
     * Closes a context quietly.
     * 
     * @param context Context to close.
     */
    public static void close(Context context)
    {
        if (context != null)
        {
            try
            {
                context.close();    
            }
            catch (NamingException ne)
            {
                logger_.warn("An error occurred while closing a Context.", ne);
            }
        }
    }
    
    /**
     * Closes an IConnection quietly.
     * 
     * @param conn IConnection to close.
     */
    public static void close(IConnection conn)
    {
        if (conn != null)
        {
            try
            {
                conn.close(); 
            }
            catch (IOException e)
            {
                logger_.warn(
                    "An error occurred while closing an IConnection.", e);
            }
        }        
    }
}
