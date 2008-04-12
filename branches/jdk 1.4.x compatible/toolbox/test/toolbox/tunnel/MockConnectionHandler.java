package toolbox.tunnel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import toolbox.util.io.PositionInputStream;
import toolbox.util.net.IConnection;
import toolbox.util.net.IConnectionHandler;

/**
 * Connection handler just like EchoConnectionHandler but uses streams instead
 * of readers/writers.
 * 
 * @see toolbox.util.net.EchoConnectionHandler
 */
public class MockConnectionHandler implements IConnectionHandler
{
    private static final Logger logger_ = 
        Logger.getLogger(MockConnectionHandler.class);
    
    public MockConnectionHandler()
    {
    }

    
    public Object handle(IConnection conn)
    {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
            
        try
        {
            boolean terminate = false;
            
            bis = new BufferedInputStream(conn.getInputStream());
            bos = new BufferedOutputStream(conn.getOutputStream());           
            
            PositionInputStream pis = new PositionInputStream(bis);
            
            while (!terminate) 
            {
                String request = new String(pis.readUntil("\n".getBytes()));
                logger_.info("Echo: " + request);
                bos.write(request.getBytes());
                bos.flush();
                
                if (request.equals("terminate"))
                    terminate = true;
            }
        }
        catch (IOException e)
        {
            logger_.error(e.getMessage(), e);
        }
        finally
        {
            IOUtils.closeQuietly(bis);
            IOUtils.closeQuietly(bos);
            
            if (conn != null )
            {
                try
                {
                    conn.close();
                }
                catch (Exception e)
                {
                }
                
                conn = null;
            }
        }
        
        return null;
    }
}