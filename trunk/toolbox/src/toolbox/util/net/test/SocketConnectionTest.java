package toolbox.util.net.test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.SocketUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.net.IConnection;
import toolbox.util.net.IConnectionListener;
import toolbox.util.net.SocketConnection;

/**
 * Unit test for SocketConnection
 */
public class SocketConnectionTest extends TestCase
{
    /** Logger */
    private static final Logger logger_ = 
        Logger.getLogger(SocketConnectionTest.class);
    
    /**
     * Entry point
     * 
     * @param  args  None
     */
    public static void main(String[] args)
    {
        TestRunner.run(SocketConnectionTest.class);
    }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for SocketConnectionTest
     * 
     * @param  arg  Name
     */
    public SocketConnectionTest(String arg)
    {
        super(arg);
    }
    
    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the default constructor
     * 
     * @throws Exception on error
     */
    public void testDefaultConstructor() throws Exception
    {
        logger_.info("Running testDefaultConstructor...");
        
        Server server = new Server(false);
        SocketConnection sc = new SocketConnection();

        server.start();        
        sc.setHost("localhost");
        sc.setPort(server.getPort());
        sc.connect();
        sc.close();
    }
        
    /**
     * Tests the (host,port) constructor
     * 
     * @throws  Exception on error
     */    
    public void testHostPortConstructor() throws Exception
    {
        logger_.info("Running testHostPortConstructor...");
        
        Server server = new Server(false);
        server.start();        
        
        SocketConnection sc = 
            new SocketConnection("localhost", server.getPort());
            
        sc.connect();
        sc.close();
    }
        
    /**
     * Tests the force connect constructor
     * 
     * @throws  Exception on error
     */    
    public void testForceConnectConstructor() throws Exception
    {
        logger_.info("Running testForceConnectConstructor...");
        
        final int port = SocketUtil.getFreePort();
        Server server = new Server(port,false);
        
        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    SocketConnection sc = 
                        new SocketConnection("localhost", port, true, 2);
                        
                    logger_.info("Connected after retry!");
                    sc.connect();
                    sc.close();
                }
                catch(Exception e)
                {
                    // Ignore
                }
            }
        }).start();
        
        ThreadUtil.sleep(10000);
        
        server.start();
    }        
   
    /**
     * Tests the getInputStream() method
     * 
     * @throws  Exception on error
     */
    public void testGetInputStream() throws Exception
    {
        logger_.info("Running testGetInputStream...");
        
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
     * 
     * @throws  Exception on error
     */
    public void testGetOutputStream() throws Exception
    {
        logger_.info("Running testGetOutputStream...");
        
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
     * Tests the notifications genereated by IConnectionListener
     * 
     * @throws  Exception on error
     */
    public void testConnectionListener() throws Exception
    {
        logger_.info("Running testConnectionListener...");
        
        Server s = new Server();
        s.start();
        
        ThreadUtil.sleep(2000);
        
        SocketConnection connection = new SocketConnection();
        connection.addConnectionListener(new Listener());
        connection.setHost("localhost");
        connection.setPort(s.getPort());
        connection.connect();
        connection.close();
        s.stop();
    }

    /**
     * Tests SocketConnection lifecycle
     * 
     * @throws  Exception on error
     */
    public void xtestConnectionLifeCycle() throws Exception
    {
        logger_.info("Running testConnectionLifeCycle...");
        
        Server s = new Server(true);
        s.start();
        
        ThreadUtil.sleep(2000);
        
        SocketConnection connection = new SocketConnection();
        connection.addConnectionListener(new Listener());
        connection.setHost("localhost");
        connection.setPort(s.getPort());
        
        for (int i=0; i<500; i++)
        {
            logger_.info("Connection " + i);
            
            try
            {
                connection.connect();
                connection.close();
            }
            catch(ConnectException ce)
            {
                logger_.info("Failed to connect to server");
            }
            finally
            {
                ThreadUtil.sleep(1);
            }
        }
        
        s.stop();
    }

    /**
     * Tests the isConnected() method
     * 
     * @throws  Exception on error
     */
    public void testIsConnected() throws Exception
    {
        logger_.info("Running testIsConnected...");
        
        Server server = new Server(true);
        server.start();        
        
        SocketConnection sc = 
            new SocketConnection("localhost", server.getPort());
            
        assertTrue(sc.isConnected());
        sc.close();
        assertTrue(!sc.isConnected());
        sc.connect();
        assertTrue(sc.isConnected());
        sc.close();

        server.stop();
    }


    //--------------------------------------------------------------------------
    //  Test helper classes
    //--------------------------------------------------------------------------

    /**
     * Dummy connection listener
     */
    class Listener implements IConnectionListener
    {
        public void connectionClosed(IConnection connection)
        {
            logger_.info("Notification: Connection closed " + connection);
        }
        
        public void connectionClosing(IConnection connection)
        {
            logger_.info("Notification: Connection closing " + connection);
        }
        
        public void connectionInterrupted(IConnection connection)
        {
            logger_.info("Notification: Connection interrupted" + connection);
        }
        
        public void connectionStarted(IConnection connection)
        {
            logger_.info("Notification: Connection started" + connection);
        }

    }


    /**
     * Internal socket server
     */
    class Server implements Runnable
    {
        private ServerSocket socket_;
        private boolean longLived_ = false;
        private int port_;
        private boolean keepGoing_ = true;
                    
        /**
         * Constructor 
         */
        public Server()
        {
            this(0,false);
        }

        /**
         * Arg constructor
         */
        public Server(boolean longLived)
        {
            this(0, longLived);
        }

        public Server(int port, boolean longLived)
        {
            port_ = port;
            longLived_ = longLived;
        }

        /**
         * Starts the socket server
         */
        public void start() throws IOException
        {
            socket_ = new ServerSocket(port_);
            
            Thread t = new Thread(this);
            t.start();
        }

        /**
         * Stops the socket server
         */
        public void stop() throws IOException
        {
            keepGoing_ = false;
            socket_.close();
        }
        
        /**
         * Runs server on thread
         */
        public void run()
        {
            while(keepGoing_)
            {
                try
                {
                    logger_.info("Server: Waiting to accept...");
                    socket_.accept();
                    keepGoing_ = longLived_;
                    logger_.info("Server: After accept..");
                }
                catch(IOException e)
                {
                    logger_.info(e.toString());
                }
            }
        }
            
        /**
         * Retrieves the server socket port
         */
        public int getPort() 
        {
            return socket_.getLocalPort();
        }           
    }
}
