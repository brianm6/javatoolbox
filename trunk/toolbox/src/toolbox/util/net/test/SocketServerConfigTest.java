package toolbox.util.net.test;

import java.util.Properties;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.net.SocketServerConfig;

/**
 * Unit test for SocketServerConfig
 */
public class SocketServerConfigTest extends TestCase
{
    /** Logger **/
    public static final Logger logger_ =
        Logger.getLogger(SocketServerConfigTest.class);

    /**
     * Entrypoint 
     */
    public static void main(String[] args)
    {
        TestRunner.run(SocketServerConfigTest.class);
    }
        
    /**
     * Constructor for SocketServerConfigTest
     */
    public SocketServerConfigTest(String arg0)
    {
        super(arg0);
    }
    
    /**
     * Tests the load(File) method 
     */
    public void testLoadByFile() throws Exception
    {
        /* TODO: generate props file on the fly */
        SocketServerConfig config = new SocketServerConfig();
        config.load("SocketServer.properties");
        assertEquals("ports don't match", 0, config.getServerPort());
        logger_.info(config);
    }
    
    /**
     * Tests the load(Properties) method 
     */
    public void testLoadByProps() throws Exception
    {
        Properties props = new Properties();
        props.put(SocketServerConfig.PROP_ACTIVE_CONNECTIONS, "3");
        props.put(SocketServerConfig.PROP_HANDLER_QUEUE_SIZE, "5");
        props.put(SocketServerConfig.PROP_SERVER_PORT, "7");
        props.put(SocketServerConfig.PROP_SOCKET_QUEUE_SIZE, "11");
        props.put(SocketServerConfig.PROP_SOCKET_TIMEOUT, "13");
        props.put(SocketServerConfig.PROP_CONNECTION_HANDLER, 
            "com.swa.turbo.util.comm.test.NullConnectionHandler");
        
        SocketServerConfig config = new SocketServerConfig();
        config.load(props);
        
        assertEquals("active conns don't match", 3, config.getActiveConnections());
        assertEquals("handler size don't match", 5, config.getHandlerQueueSize());
        assertEquals("server port don't match", 7, config.getServerPort());
        assertEquals("socket queue don't match", 11, config.getSocketQueueSize());
        assertEquals("socket timeout don't match", 13, config.getSocketTimeout());
        
        assertEquals("socket handlers don't match", 
            "com.swa.turbo.util.comm.test.NullConnectionHandler", 
            config.getConnectionHandlerType());
            
        logger_.info(config);
    }
}