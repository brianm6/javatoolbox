package toolbox.util.net.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.log4j.Category;

import toolbox.util.net.IConnection;
import toolbox.util.net.IConnectionHandler;


/**
 * Sample connection handler that echoes all messages that come in on the
 * input stream back to the output stream. The secret token to terminate
 * the connection is "terminate"
 */
public class EchoConnectionHandler implements IConnectionHandler
{
    /** Logger **/
    private static final Category logger_ = 
        Category.getInstance(EchoConnectionHandler.class);
    
    /** Terminate token **/
    public static final String TOKEN_TERMINATE = "terminate";
    
    /**
     * Constructor for EchoConnectionHandler
     */
    public EchoConnectionHandler()
    {
    }
    
    /**
     * Implementation for IConnectionHandler
     * 
     * @see AsyncConnectionHandler#handle(IConnection)
     */
    public Object handle(IConnection conn)
    {
        try
        {
            boolean terminate = false;
            
            BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
                
            PrintWriter pw = new PrintWriter(conn.getOutputStream());           
            
            while(!terminate) 
            {
                String request = br.readLine();
                logger_.info("Echo: " + request);
                pw.println(request);
                pw.flush();
                
                if(request.equals(TOKEN_TERMINATE))
                    terminate = true;
            }
            
            conn.close();
        }
        catch(IOException e)
        {
            logger_.error(e.getMessage(), e);
        }
        finally
        {
            // nothing
        }
        
        return null;
    }
}