package toolbox.util.net.test;

import java.io.File;
import java.util.Properties;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.net.SocketServerConfig;

/**
 * Unit test for SocketServerConfig
 */
public class SocketServerConfigTest extends TestCase
{
    /** Logger */
    public static final Logger logger_ =
        Logger.getLogger(SocketServerConfigTest.class);

    /**
     * Entrypoint 
     * 
     * @param  args  None
     */
    public static void main(String[] args)
    {
        TestRunner.run(SocketServerConfigTest.class);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
            
    /**
     * Constructor for SocketServerConfigTest
     * 
     * @param  arg0  Name
     */
    public SocketServerConfigTest(String arg0)
    {
        super(arg0);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the load(File) method 
     * 
     * @throws  Exception on error
     */
    public void testLoadByFile() throws Exception
    {
        logger_.info("Running testLoadByFile...");
        
        // Create properties file
        String file = FileUtil.getTempFilename();
        
        logger_.info("Props file: " + file);
        
        try
        {
            StringBuffer sb = new StringBuffer();
            sb.append("socketserver.serverport=1000\n");
            sb.append("socketserver.activeconnections=2000\n");
            sb.append("socketserver.socketqueuesize=3000\n");
            sb.append("socketserver.handlerqueuesize=4000\n");
            sb.append("socketserver.name=server\n");            
            sb.append("socketserver.sockettimeout=5000\n");
            sb.append("socketserver.connectionhandler=" + 
                      "toolbox.util.net.test.NullConnectionHandler\n");

            FileUtil.setFileContents(file, sb.toString(), false);
            
            logger_.info("Generated properties file: \n" + 
                FileUtil.getFileContents(file));
            
            // Create config and read props in from file
            SocketServerConfig config = new SocketServerConfig();
            config.load(file);

            logger_.info("Loaded config: \n " + config);
            assertEquals("ports don't match", 1000, config.getServerPort());
            assertEquals("names don't match", "server", config.getName());
            
            assertEquals("timeouts don't match", 
                5000, config.getSocketTimeout());
                
            assertEquals("handlers don't match", 
                "toolbox.util.net.test.NullConnectionHandler", 
                config.getConnectionHandlerType());
                
            assertEquals("active conns don't match", 
                2000, config.getActiveConnections());
    
            assertEquals("queue sizes don't match", 
                3000, config.getSocketQueueSize());
               
            assertEquals("handler sizes don't match", 
                4000, config.getHandlerQueueSize()); 
        }
        finally
        {
            try
            {
                File f = new File(file);
                if (f.exists())
                    f.delete();
            }
            catch (Exception e) 
            {
                // Ignore
            }
        }
    }
    
    /**
     * Tests the load(Properties) method 
     * 
     * @throws  Exception on error
     */
    public void testLoadByProps() throws Exception
    {
        logger_.info("Running testLoadByProps...");
        
        Properties props = new Properties();
        props.put(SocketServerConfig.PROP_ACTIVE_CONNECTIONS, "3");
        props.put(SocketServerConfig.PROP_HANDLER_QUEUE_SIZE, "5");
        props.put(SocketServerConfig.PROP_SERVER_PORT, "7");
        props.put(SocketServerConfig.PROP_SOCKET_QUEUE_SIZE, "11");
        props.put(SocketServerConfig.PROP_SOCKET_TIMEOUT, "13");
        props.put(SocketServerConfig.PROP_CONNECTION_HANDLER, 
            "toolbox.util.net.test.NullConnectionHandler");
        
        SocketServerConfig config = new SocketServerConfig();
        config.load(props);
        
        assertEquals("active conns don't match", 
            3, config.getActiveConnections());
            
        assertEquals("handler size don't match", 
            5, config.getHandlerQueueSize());
            
        assertEquals("server port don't match", 
            7, config.getServerPort());
            
        assertEquals("socket queue don't match", 
            11, config.getSocketQueueSize());
            
        assertEquals("socket timeout don't match", 
            13, config.getSocketTimeout());
        
        assertEquals("socket handlers don't match", 
            "toolbox.util.net.test.NullConnectionHandler", 
            config.getConnectionHandlerType());
            
        logger_.info(config);
    }
}
