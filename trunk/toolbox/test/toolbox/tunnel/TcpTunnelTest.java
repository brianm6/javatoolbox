package toolbox.tunnel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.SocketUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.io.StringOutputStream;
import toolbox.util.net.DefaultSocketServerListener;
import toolbox.util.net.EchoConnectionHandler;
import toolbox.util.net.SocketServer;
import toolbox.util.net.SocketServerConfig;

/**
 * Unit test for TcpTunnel.
 */
public class TcpTunnelTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(TcpTunnelTest.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private PrintStream os_;
    private PrintStream es_;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(TcpTunnelTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Overrides TestCase
    //--------------------------------------------------------------------------
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        os_ = System.out;
        es_ = System.err;
    }
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        System.setOut(os_);
        System.setErr(es_);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests the TcpTunnel for an end to end scenario of sending/receiving
     * data though the tunnel.
     * 
     * @throws Exception on error.
     */
    public void testTcpTunnel() throws Exception 
    {    
        // Setup server
        SocketServerConfig serverConfig = new SocketServerConfig();
        
        serverConfig.setConnectionHandlerType(
            EchoConnectionHandler.class.getName());
            
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
        
        TcpTunnel tunnel = 
            new TcpTunnel(
                tunnelPort, 
                "localhost", 
                serverConfig.getServerPort());

        OutputStream ab = new StringOutputStream();
        tunnel.setIncomingSink(ab);
        
        OutputStream ba = new StringOutputStream();
        tunnel.setOutgoingSink(ba);
        
        DefaultTcpTunnelListener tunnelListener =
            new DefaultTcpTunnelListener();
        
        tunnel.addTcpTunnelListener(tunnelListener);
        ThreadUtil.run(tunnel, "start", null);
        tunnelListener.waitForStarted();
    
        // Setup client
        Socket socket = new Socket("localhost", tunnelPort);
        
        PrintWriter pw = 
            new PrintWriter(
                new OutputStreamWriter(
                    socket.getOutputStream()));
            
        BufferedReader br = 
            new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()));

        // Send some data
        write(pw, "Hello");
        
        // Send of data should trigger tunnel to connect to server
        serverListener.waitForAccept();
        
        // Read result
        String response = read(br);
        assertEquals(response, response);
        
        // Tear down
        write(pw, "terminate");

        //logger_.info(StringUtil.addBars("a->b:\n" + ab));
        //logger_.info(StringUtil.addBars("b->a:\n" + ba));
        
        tunnel.stop();
        server.stop();
        
        // Make sure count of bytes send/received adds up
        int r = tunnelListener.getTotalBytesRead();
        int w = tunnelListener.getTotalBytesWritten();
        
        logger_.info("Bytes read   : " + r);
        logger_.info("Bytes written: " + w);
        
        int len = "Hello".length() + "terminate".length() + "\r\n".length() * 2;
        assertEquals(len, r);
        assertEquals(len, w);
    }
    
    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------
    
    /**
     * Convenience to write a msg to the server.
     * 
     * @param pw Writer.
     * @param msg Message.
     */
    private void write(PrintWriter pw, String msg)
    {
        logger_.info("a->b " + msg);
        pw.println(msg);
        pw.flush();
        ThreadUtil.sleep(1000);
    }
    
    
    /**
     * Convenience to read a msg from the server.
     * 
     * @param br Reader.
     * @return Message.
     * @throws Exception on error.
     */
    public String read(BufferedReader br) throws Exception
    {
        String msg = br.readLine();
        logger_.info("a<-b " + msg);
        return msg;
    }
}