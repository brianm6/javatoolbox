package toolbox.util;

import java.io.IOException;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import toolbox.util.net.IConnection;

/**
 * Convenience class that removes the ugliness of closing resoures that is so
 * common in managed resources. The premise of this class is to close resources
 * without concern for any exceptional conditions that may arise from doing so.
 * <pre>
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
 * 
 * </pre>
 */
public final class ResourceCloser
{
    private static Logger logger_ =
        Logger.getLogger(ResourceCloser.class);

    // Clover private constructor workaround
    static { new ResourceCloser(); }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Prevent construction
     */
    private ResourceCloser()
    {
    }

    //--------------------------------------------------------------------------
    //  Public 
    //--------------------------------------------------------------------------

    /**
     * Closes a context quietly
     * 
     * @param  context  Context to close
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
     * Removes an EJB quietly
     *
     * @param  ejb  EJB to remove
     */
//    public static void remove(EJBObject ejb)
//    {
//        if (ejb != null)
//        {
//            try
//            {
//                ejb.remove();
//            }
//            catch (RemoteException e)
//            {
//                logger_.warn(
//                  "An error occurred while removing an EJBObject.", e);
//            }
//            catch (RemoveException e)
//            {
//                logger_.warn(
//                      "An error occurred while removing an EJBObject.", e);
//            }
//        }
//    }

    
    /**
     * Closes an IConnection quietly
     *
     * @param  conn  IConnection to close
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

    
    /**
     * Closes a QueueReceiver quietly
     *
     * @param  queueReceiver  Queue receiver to close
     */
//    public static void close(QueueReceiver queueReceiver)
//    {
//        if (queueReceiver != null)
//        {
//            try
//            {
//                queueReceiver.close(); 
//            }
//            catch (JMSException e)
//            {
//                logger_.warn(
//                      "An error occurred while closing a QueueReceiver.", e);
//            }
//        }        
//    }
}
