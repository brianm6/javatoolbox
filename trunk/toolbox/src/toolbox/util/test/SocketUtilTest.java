package toolbox.util.test;

import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.SocketUtil;
import toolbox.util.ThreadUtil;

/**
 * Unit test for SocketUtil
 */
public class SocketUtilTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(SocketUtilTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint
     *
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(SocketUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests isReasonAcceptTimeout()
     */
    public void testIsReasonAcceptTimeout() 
    {
        logger_.info("Running testIsReasonAcceptTimeout...");
        
        InterruptedIOException iioe = 
            new InterruptedIOException(SocketUtil.MSG_ACCEPT_TIMEOUT);
            
        assertTrue(SocketUtil.isReasonAcceptTimeout(iioe));
    }
    
    /**
     * Tests isReasonSocketClosed()
     */
    public void testIsReasonSocketClosed() 
    {
        logger_.info("Running testIsReasonSocketClosed...");
        
        SocketException se  = 
            new SocketException(SocketUtil.MSG_SOCKET_CLOSED_13);
            
        assertTrue(SocketUtil.isReasonSocketClosed(se));
        
        SocketException se2  = 
            new SocketException(SocketUtil.MSG_SOCKET_CLOSED_14);
            
        assertTrue(SocketUtil.isReasonSocketClosed(se2));
    }
    
    /**
     * Tests connectWithRetry() for failure scenario
     * 
     * @throws Exception on error
     */
    public void testConnectWithRetryFailure() throws Exception
    {
        logger_.info("Running testConnectWithRetryFailure...");
        
        // try to connect to non-existant socket
        SocketUtil.connectWithRetry(
            InetAddress.getLocalHost().getHostAddress(), 
            55555, 1, 5);
    }
    
    /**
     * Tests connectWithRetry() for success scenario
     * 
     * @throws Exception on error
     */
    public void testConnectWithRetrySuccess() throws Exception
    {
        logger_.info("Running testConnectWithRetrySuccess...");
        
        // start socket server
        ServerSocket ss = new ServerSocket(0);
        
        ThreadUtil.run(ss,"accept", new Object[0]);
        
        // wait for server to init
        ThreadUtil.sleep(2000);
        
        // connect with client
        Socket socket = SocketUtil.connectWithRetry(
            InetAddress.getLocalHost().getHostAddress(), 
            ss.getLocalPort(), 1, 5);
             
        // cleanup
        SocketUtil.close(socket);
    }
    
    /**
     * Tests getFreePort()
     * 
     * @throws Exception on error
     */ 
    public void testGetFreePort() throws Exception
    {
        logger_.info("Running testGetFreePort...");
        
        for (int i=0; i<10; i++)
        {
            int freePort = SocketUtil.getFreePort();
            logger_.info("Freeport = " + freePort);
            assertTrue("Free port cannot be zero", freePort > 0);
        }
    }
    
    /**
     * Tests close(Socket)
     * 
     * @throws Exception on error
     */
    public void testCloseSocket() throws Exception
    {
        logger_.info("Running testCloseSocket...");
        
        int port = SocketUtil.getFreePort();
        ServerSocket ss = new ServerSocket(port);
        Thread serverThread = ThreadUtil.run(ss, "accept", new Object[0]);

        // Revisit
        // ThreadUtil.sleep(1000);

        // Close connected socket                
        Socket socket = new Socket("localhost", port);
        SocketUtil.close(socket);
        
        // Close null socket
        socket = null;
        SocketUtil.close(socket);
        
        serverThread.join();
    }
    
    /**
     * Tests close(ServerSocket)
     * 
     * @throws Exception on error
     */
    public void testCloseServerSocket() throws Exception
    {
        // TODO: Complete this unit test: testCloseServerSocket()
        logger_.info("Running testCloseServerSocket...");
        
//        int port = SocketUtil.getFreePort();
//        ServerSocket ss = new ServerSocket(port);
//        Thread serverThread = ThreadUtil.run(ss, "accept", new Object[0]);
//
//        //Revisit
//        ThreadUtil.sleep(5000);
//
//        SocketUtil.close(ss);
        
//
//        // Close connected socket                
//        Socket socket = new Socket("localhost", port);
//        SocketUtil.close(socket);
//        
//        // Close null socket
//        socket = null;
//        SocketUtil.close(socket);
        
        //serverThread.join();
    }
    
    
    
}