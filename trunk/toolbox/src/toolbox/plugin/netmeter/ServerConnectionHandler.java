package toolbox.plugin.netmeter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import toolbox.util.net.IConnection;
import toolbox.util.net.IConnectionHandler;

/**
 * ServerConnectionHandler is a server side connection handler for incoming
 * client requests. 
 */
public class ServerConnectionHandler implements IConnectionHandler
{
    private static final Logger logger_ = 
        Logger.getLogger(ServerConnectionHandler.class);
    
    //--------------------------------------------------------------------------
    // IConnectionHandler Interface 
    //--------------------------------------------------------------------------
        
    /**
     * @see toolbox.util.net.IConnectionHandler#handle(
     *      toolbox.util.net.IConnection)
     */
    public Object handle(IConnection conn)
    {
        try
        {
            //EventInputStream is = 
            //  new EventInputStream("ServerConnectionHandler",

            //CountingInputStream is = 
            //    new CountingInputStream(
            InputStream is = new BufferedInputStream(conn.getInputStream());
                
            //ElapsedTime time = new ElapsedTime();
            
            //byte[] buffer = new byte[30000];
            
            while (is.read() != -1);
            
            //time.setEndTime();
            
            //int secs = time.getSeconds();
            //int count = is.getCount();
            //double thruput = count/secs;
            
            //logger_.info("Server throughput: " + count + "/" + secs + " ==> "
            // + thruput + "KBytes/sec");
            
            is.close();
        }
        catch (IOException e)
        {
            logger_.error(e);
        }
        
        return null;
    }
}