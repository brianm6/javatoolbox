package toolbox.tunnel.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.tunnel.DefaultTcpTunnelListener;
import toolbox.tunnel.TcpTunnel;
import toolbox.util.SocketUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.net.DefaultSocketServerListener;
import toolbox.util.net.SocketServer;
import toolbox.util.net.SocketServerConfig;

/**
 * Unit test for TcpTunnel.
 */
public class TcpTunnelTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(TcpTunnelTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(TcpTunnelTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests the TcpTunnel for an end to end scenario of sending/receiving
     * data though the tunnel.
     * 
     * @throws Exception on error
     */
    public void testTcpTunnel() throws Exception 
    {    
        // Setup server
        SocketServerConfig serverConfig = new SocketServerConfig();
        
        serverConfig.setConnectionHandlerType(
            "toolbox.util.net.test.EchoConnectionHandler");
            
        serverConfig.setName("testTcpTunnel");
        serverConfig.setServerPort(SocketUtil.getFreePort());
        SocketServer server = new SocketServer(serverConfig);
        
        DefaultSocketServerListener serverListener = 
            new DefaultSocketServerListener();
            
        server.addSocketServerListener(serverListener);
        server.start();
        serverListener.waitForStart();        
        
        logger_.info("Socket server started!");    

        // Setup tunnel
        int tunnelPort = SocketUtil.getFreePort();
        
        TcpTunnel tunnel = new TcpTunnel(
            tunnelPort, "localhost", serverConfig.getServerPort());
            
        DefaultTcpTunnelListener tunnelListener =
            new DefaultTcpTunnelListener();
        
        tunnel.addTcpTunnelListener(tunnelListener);
        ThreadUtil.run(tunnel, "start", null);
        tunnelListener.waitForStarted();
    
        // Setup client
        Socket socket = new Socket("localhost", tunnelPort);
        
        PrintWriter pw = new PrintWriter(
            new OutputStreamWriter(socket.getOutputStream()));
            
        BufferedReader br = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));

        // Send some data
        String s = "Hello";
        logger_.info("out: " + s);
        pw.println(s);
        pw.flush();
        
        // Send of data should trigger tunnel to connect to server
        serverListener.waitForAccept();
        
        // Read result
        String echo = br.readLine();
        logger_.info("in: " + echo);
        assertEquals(s, echo);
        
        // Tear down
        pw.println("terminate");
        pw.flush();
        socket.close();
        tunnel.stop();
        server.stop();
    }
}