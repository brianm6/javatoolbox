package toolbox.util.net.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;

import org.apache.log4j.Logger;

import toolbox.util.net.IConnection;
import toolbox.util.net.IConnectionHandler;

/**
 * Simple connection handler that adheres to the following sequence of events.
 * <pre>
 * 
 * 1. Client initiates connection
 * 2. Client sends message
 * 3. Server sends pong
 * 4. Server terminates connection
 * 
 * </pre>
 */
public class PingConnectionHandler implements IConnectionHandler
{
    private static final Logger logger_ = 
        Logger.getLogger(PingConnectionHandler.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor
     */
    public PingConnectionHandler()
    {
    }
    
    //--------------------------------------------------------------------------
    // IConnectionHandler Interface
    //--------------------------------------------------------------------------
    
    public Object handle(IConnection conn)
    {
        logger_.info("ping->handle()");
        
        try
        {
            BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
                
            String request = br.readLine();
            
            logger_.info(
                "Server received ping:" + request + " at " + new Date());
            
            PrintWriter pw = new PrintWriter(conn.getOutputStream());
            logger_.info("Server sent pong at " + new Date());
            pw.println("pong");
            pw.flush();
            
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