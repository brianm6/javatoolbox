package toolbox.tunnel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import nu.xom.Element;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import toolbox.util.SocketUtil;
import toolbox.util.StringUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.formatter.XMLFormatter;
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
        logger_.info("Running testTcpTunnel...");
        
        // Setup server
        SocketServerConfig serverConfig = new SocketServerConfig();
        
        serverConfig.setConnectionHandlerType(
            EchoConnectionHandler.class.getName());
            
        serverConfig.setName("TcpTunnelServer");
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
        tunnel.start();
        tunnelListener.waitForStarted();
    
        logger_.info("Tunnel started!");
        
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

        String message = RandomStringUtils.randomAlphanumeric(100); //"Hello";
        
        // Send some data
        write(pw, message);
        
        // Send of data should trigger tunnel to connect to server
        serverListener.waitForAccept();
        
        // Read result
        String response = read(br);
        assertEquals(message, response);
        
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
        
        int len = message.length() + "terminate".length() + "\r\n".length() * 2;
        assertEquals(len, r);
        assertEquals(len, w);
    }
    
    
    /**
     * Tests the IPreferenced interface implementation.
     */
    public void testPreferenced() throws Exception
    {
        logger_.info("Running testPreferenced...");
        
        int localPort = SocketUtil.getFreePort();
        int remotePort = SocketUtil.getFreePort();
        String remoteHost= "localhost";
        boolean suppressBinary = RandomUtils.nextBoolean();
        
        // savePrefs
        TcpTunnel tunnel = new TcpTunnel(localPort, remoteHost, remotePort);
        tunnel.setSuppressBinary(suppressBinary);
        
        Element prefs = new Element("root");
        tunnel.savePrefs(prefs);
        
        logger_.debug(StringUtil.banner(
            new XMLFormatter().format(prefs.toXML())));
        
        // applyPrefs
        TcpTunnel tunnel2 = new TcpTunnel();
        tunnel2.applyPrefs(prefs);
        
        assertEquals(localPort, tunnel2.getLocalPort());
        assertEquals(remoteHost, tunnel2.getRemoteHost());
        assertEquals(remotePort, tunnel2.getRemotePort());
        assertEquals(suppressBinary, tunnel2.isSuppressBinary());
    }


    /**
     * Tests suppressing of binary data.
     */
    public void testSuppressBinary() throws Exception 
    {   
        logger_.info("Running testSuppressBinary...");
        
        // Setup server
        SocketServerConfig serverConfig = new SocketServerConfig();
        
        serverConfig.setConnectionHandlerType(
            MockConnectionHandler.class.getName());
            
        serverConfig.setName("TcpTunnelServer");
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
        
        tunnel.setSuppressBinary(true);
        
        OutputStream ab = new StringOutputStream();
        tunnel.setIncomingSink(ab);
        
        OutputStream ba = new StringOutputStream();
        tunnel.setOutgoingSink(ba);
        
        DefaultTcpTunnelListener tunnelListener =
            new DefaultTcpTunnelListener();
        
        tunnel.addTcpTunnelListener(tunnelListener);
        tunnel.start();
        tunnelListener.waitForStarted();
    
        logger_.info("Tunnel started!");
        
        // Setup client
        Socket socket = new Socket("localhost", tunnelPort);
        PrintStream os = new PrintStream(socket.getOutputStream());
        InputStream is = socket.getInputStream();

        char[] chars = { 'a','b','c', 192 };
        String message = RandomStringUtils.random(50, chars) + "\n";
        
        // Send some data
        write(os, message);
        
        // Send of data should trigger tunnel to connect to server
        serverListener.waitForAccept();
        
        // Read result
        String response = read(is);
        
        assertEquals(
            message.length() + " " + response.length(), message, response);
        
        // Tear down
        write(os, EchoConnectionHandler.TOKEN_TERMINATE + "\n");
        
        tunnel.stop();
        server.stop();
        
        logger_.debug("tunnel outgoing sink: " + ba.toString());
        
        assertTrue(ba.toString().indexOf(192) == -1);
        
        String[] lines = 
            StringUtil.tokenize(ba.toString().replace('.', (char) 192), "\n");
        
        assertEquals(StringUtils.chomp(message), lines[0]);
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
     * Convenience to write a msg to the server.
     * 
     * @param pw Writer.
     * @param msg Message.
     */
    private void write(OutputStream os, String msg) throws IOException
    {
        logger_.info("a->b " + msg.length() + " - " + msg);
        os.write(msg.getBytes());
        os.flush();
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

    
    /**
     * Convenience to read a msg from the server.
     * 
     * @param br Reader.
     * @return Message.
     * @throws Exception on error.
     */
    public String read(InputStream br) throws Exception
    {
       StringBuffer msg = new StringBuffer();

        int i;
        
        while ( (i = br.read()) != -1)
        {
            //System.out.print("/");
            char c = (char) i; //br.read();
            System.out.print(c);
            msg.append(c);

            if (c == '\n')
                break;
        }

        logger_.info("a<-b " + msg.length() + " - " + msg);
        return msg.toString();
    }    
}