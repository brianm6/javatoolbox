package toolbox.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

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
     * Closes an inputstream quitely
     * 
     * @param  inputStream  Inputstraem to close
     */    
    public static void close(InputStream inputStream)
    {
        if (inputStream != null)
        {
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                logger_.warn(
                    "An error occurred while closing an InputStream.", e);
            }
        }
    }


    /**
     * Closes an outputstream quitely
     * 
     * @param  outputStream  Output stream to close
     */    
    public static void close(OutputStream outputStream)
    {
        if (outputStream != null)
        {
            try
            {
                outputStream.close();
            }
            catch (IOException e)
            {
                logger_.warn(
                    "An error occurred while closing an OutputStream.", e);
            }
        }
    }


    /**
     * Closes a writer quietly
     * 
     * @param  writer  Writer to close
     */    
    public static void close(Writer writer)
    {
        if (writer != null)
        {
            try
            {
                writer.close();
            }
            catch (IOException e)
            {
                logger_.warn("An error occurred while closing a Writer.", e);
            }
        }
    }


    /**
     * Closes a reader quietly
     * 
     * @param  reader  Reader to close
     */    
    public static void close(Reader reader)
    {
        if (reader != null)
        {
            try
            {
                reader.close();
            }
            catch (IOException e)
            {
                logger_.warn("An error occurred while closing a Reader.", e);
            }
        }
    }


    /**
     * Closes a socket quietly
     * 
     * @param  socket  Socket to close
     */    
    public static void close(Socket socket)
    {
        if (socket != null)
        {
            try
            {
                socket.close();
            }
            catch (IOException e)
            {
                logger_.warn("An error occurred while closing a Socket.", e);
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
