package toolbox.util.net.test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.apache.log4j.Category;

import toolbox.util.ThreadUtil;
import toolbox.util.net.SocketConnection;

/**
 * Unit test for SocketConnection
 */
public class SocketConnectionTest extends TestCase
{
    /** Logger **/
    private static final Category logger = 
        Category.getInstance(SocketConnectionTest.class);
    
    /**
     * Entry point
     */
    public static void main(String[] args)
    {
        TestRunner.run(SocketConnectionTest.class);
    }

    /**
     * Constructor for SocketConnectionTest
     */
    public SocketConnectionTest(String arg)
    {
        super(arg);
    }
    
    /**
     * Tests the getInputStream() method
     */
    public void testGetInputStream() throws Exception
    {
        Server s = new Server();
        s.start();
        
        // wait for server to enter accept()
        // TODO: add notification mechanism instead of sleeping     
        ThreadUtil.sleep(2000);
        
        int port = s.getPort();
        
        Socket socket = new Socket("localhost", port);
        SocketConnection connection = new SocketConnection(socket);
        assertNotNull("input stream is null", connection.getInputStream());
        socket.close();
        s.stop();
    }

    /**
     * Tests the getOutputStream() method
     */
    public void testGetOutputStream() throws Exception
    {
        Server s = new Server();
        s.start();

        // wait for server to enter accept()
        // TODO: add notification mechanism instead of sleeping
        ThreadUtil.sleep(2000);
        
        int port = s.getPort();
        
        Socket socket = new Socket("localhost", port);
        SocketConnection connection = new SocketConnection(socket);

        assertNotNull("output stream is null", connection.getOutputStream());    
        
        socket.close();
        s.stop();
    }

    /**
     * Internal socket server
     */
    class Server implements Runnable
    {
        ServerSocket socket;

        /**
         * Constructor 
         */
        public Server()
        {
        }

        /**
         * Starts the socket server
         */
        public void start() throws IOException
        {
            socket = new ServerSocket(0);
            
            Thread t = new Thread(this);
            t.start();
        }

        /**
         * Stops the socket server
         */
        public void stop() throws IOException
        {
            socket.close();
        }
        
        /**
         * Runs server on thread
         */
        public void run()
        {
            try
            {
                Socket sock = socket.accept();                          
            }
            catch(IOException e)
            {
                logger.error("run", e);
            }
        }
            
        /**
         * Retrieves the server socket port
         */
        public int getPort() 
        {
            return socket.getLocalPort();
        }           
    }
}