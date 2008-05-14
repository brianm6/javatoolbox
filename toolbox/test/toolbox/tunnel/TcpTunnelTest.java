package toolbox.tunnel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import toolbox.util.RandomUtil;
import toolbox.util.SocketUtil;
import toolbox.util.StringUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.formatter.XMLFormatter;
import toolbox.util.io.MonitoredOutputStream;
import toolbox.util.io.StringOutputStream;
import toolbox.util.io.throughput.DefaultThroughputMonitor;
import toolbox.util.io.throughput.ThroughputEvent;
import toolbox.util.io.throughput.ThroughputListener;
import toolbox.util.io.throughput.ThroughputMonitor;
import toolbox.util.net.AsyncConnectionHandler;
import toolbox.util.net.DefaultSocketServerListener;
import toolbox.util.net.EchoConnectionHandler;
import toolbox.util.net.SocketServer;
import toolbox.util.net.SocketServerConfig;

/**
 * Unit test for {@link toolbox.tunnel.TcpTunnel}.
 */
public class TcpTunnelTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(TcpTunnelTest.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Redirected stdio.
     */
    private PrintStream os_;
    
    /**
     * Redirected stderr.
     */
    private PrintStream es_;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    public static void main(String[] args)
    {
        TestRunner.run(TcpTunnelTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Setup/Teardown
    //--------------------------------------------------------------------------
    
    protected void setUp() throws Exception
    {
        os_ = System.out;
        es_ = System.err;
    }
    
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
     */
    public void testTcpTunnel() throws Exception 
    {   
        logger_.info("Running testTcpTunnel...");
        
        // Setup server
        SocketServerConfig serverConfig = new SocketServerConfig();
        
        serverConfig.setConnectionHandlerType(EchoConnectionHandler.class.getName());
        serverConfig.setName("TcpTunnelServer");
        serverConfig.setServerPort(SocketUtil.getFreePort());
        SocketServer server = new SocketServer(serverConfig);
        
        DefaultSocketServerListener serverListener = new DefaultSocketServerListener();
            
        server.addSocketServerListener(serverListener);
        server.start();
        serverListener.waitForStart();        
        
        logger_.debug("Socket server started!");    

        // Setup tunnel
        int tunnelPort = SocketUtil.getFreePort();
        
        TcpTunnel tunnel = new TcpTunnel(tunnelPort, "localhost", serverConfig.getServerPort());
        
        OutputStream ab = new StringOutputStream();
        tunnel.setIncomingSink(ab);
        
        OutputStream ba = new StringOutputStream();
        tunnel.setOutgoingSink(ba);
        
        DefaultTcpTunnelListener tunnelListener = new DefaultTcpTunnelListener();
        
        tunnel.addTcpTunnelListener(tunnelListener);
        tunnel.start();
        tunnelListener.waitForStarted();
    
        logger_.debug("Tunnel started!");
        
        // Setup client
        Socket socket = new Socket("localhost", tunnelPort);
        
        logger_.debug(socket);
        
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String message = RandomStringUtils.randomAlphanumeric(100); //"Hello";
        
        // Send some data
        write(pw, message);
        logger_.debug("wrote to socket..");
        
        // Send of data should trigger tunnel to connect to server
        serverListener.waitForAccept();
        logger_.debug("server accepted...");
        
        // Read result
        String response = read(br);
        logger_.debug("read response...");
        
        assertEquals(message, response);
        
        // Tear down
        write(pw, "terminate");

        //logger_.debug(StringUtil.addBars("a->b:\n" + ab));
        //logger_.debug(StringUtil.addBars("b->a:\n" + ba));
        
        tunnel.stop();
        server.stop();
        
        // Make sure count of bytes send/received adds up
        int r = tunnelListener.getTotalBytesRead();
        int w = tunnelListener.getTotalBytesWritten();
        
        logger_.debug("Bytes read   : " + r);
        logger_.debug("Bytes written: " + w);
        
        int len = message.length() + "terminate".length() + System.getProperty("line.separator").length() * 2;
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
    public void xxxtestSuppressBinary() throws Exception 
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
        
        logger_.debug("Socket server started!");    

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
    
        logger_.debug("Tunnel started!");
        
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

    
    /**
     * Stress tests throughput of the tunnel.
     */
    public void testTcpTunnel_StressTest() throws Exception 
    {   
        logger_.info("Running testTcpTunnel_StressTest...");
        
        if (!("true".equals(System.getProperty("stress", "false")))) {
        	logger_.debug("Skipping test_TcpTunnel_StressTest...");
        	return;
        }
        
        // Socket Server =======================================================
        
        // Setup server config...
        SocketServerConfig serverConfig = 
            new SocketServerConfig(
                "TcpTunnelServer",
                SocketUtil.getFreePort(),
                EchoConnectionHandler.class.getName());

        // Create server listener...
        DefaultSocketServerListener serverListener = 
            new DefaultSocketServerListener();

        // Create server and start...
        SocketServer server = new SocketServer(serverConfig);
        
        // Mute the handler...
        AsyncConnectionHandler async = (AsyncConnectionHandler)
            server.getConnectionHandler();
        
        EchoConnectionHandler handler = (EchoConnectionHandler) 
            async.getConnectionHandler();
        
        handler.setQuiet(true);
        
        server.addSocketServerListener(serverListener);
        server.start();
        serverListener.waitForStart();        
        
        logger_.debug("Socket server started!");    

        // Tunnel ==============================================================
        
        // Setup tunnel to server...
        int tunnelPort = SocketUtil.getFreePort();
        
        TcpTunnel tunnel = 
            new TcpTunnel(
                tunnelPort, 
                "localhost", 
                serverConfig.getServerPort());
        
        OutputStream ab = new NullOutputStream();
        tunnel.setIncomingSink(ab);
        
        MonitoredOutputStream ba = 
            new MonitoredOutputStream(new NullOutputStream());
        
        tunnel.setOutgoingSink(ba);
        ThroughputMonitor monitor = new DefaultThroughputMonitor();
        
        monitor.addThroughputListener(new ThroughputListener()
        {
            public void currentThroughput(ThroughputEvent event)
            {
                logger_.debug("Throughput: " + event.getThroughput());
            }
        });
        
        monitor.setMonitoringThroughput(true);
        ba.setThroughputMonitor(monitor);
        
        DefaultTcpTunnelListener tunnelListener =
            new DefaultTcpTunnelListener();
        
        tunnel.addTcpTunnelListener(tunnelListener);
        tunnel.start();
        tunnelListener.waitForStarted();
    
        logger_.debug("Tunnel started!");

        // Client ==============================================================
        
        // Setup client
        Socket socket = new Socket("localhost", tunnelPort);
        
        final PrintWriter pw = 
            new PrintWriter(
                new BufferedWriter(
                new OutputStreamWriter(
                    socket.getOutputStream())));
            
        final BufferedReader br = 
            new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()));

        final String message = RandomUtil.nextString(100000);
        
        // Send some data
        write(pw, message);
        
        // Send of data should trigger tunnel to connect to server
        serverListener.waitForAccept();
        
        // Read result
        String response = read(br);
        assertEquals(message, response);
        
        // Writer ==============================================================
        
        class WriterThread implements Runnable {
            
            public void run()
            {
                for (int i = 1; i < 100; i++) 
                {
                    writeStress(pw, message);
                }
                
                logger_.debug("Writer thread done.");
            }
        }

        // Reader ==============================================================
        
        class ReaderThread implements Runnable {
            
            public void run()
            {
                try
                {
                    String response = null;
                    while ( (response = read(br)) != null);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally 
                {
                    logger_.debug("Reader thread done.");
                }
                
            }
        }

        // All Systems Go ======================================================
        
        Thread w = new Thread(new WriterThread());
        Thread r = new Thread(new ReaderThread());
        
        w.start();
        r.start();
        w.join();
        write(pw, "terminate");
        r.join();
        
        monitor.setMonitoringThroughput(false);
        
        // Tear down
        tunnel.stop();
        server.stop();
        
        logger_.debug("Bytes read   : " + tunnelListener.getTotalBytesRead());
        logger_.debug("Bytes written: " + tunnelListener.getTotalBytesWritten());
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
        //logger_.debug("a->b " + msg);
        pw.println(msg);
        pw.flush();
        ThreadUtil.sleep(1000);
    }
    
    
    /**
     * Write without delay used by stress test.
     * 
     * @param pw Destination writer.
     * @param msg Message.
     */
    private void writeStress(PrintWriter pw, String msg)
    {
        pw.println(msg);
        pw.flush();
    }
    
    
    /**
     * Convenience to write a msg to the server.
     * 
     * @param pw Writer.
     * @param msg Message.
     */
    private void write(OutputStream os, String msg) throws IOException
    {
        //logger_.debug("a->b " + msg.length() + " - " + msg);
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
        //logger_.debug("a<-b " + msg);
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

        logger_.debug("a<-b " + msg.length() + " - " + msg);
        return msg.toString();
    }    
}