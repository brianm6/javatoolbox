package toolbox.util.net.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ThreadUtil;
import toolbox.util.concurrent.BlockingQueue;
import toolbox.util.net.ISocketServerListener;
import toolbox.util.net.SocketConnection;
import toolbox.util.net.SocketServer;
import toolbox.util.net.SocketServerConfig;

/**
 * Unit test for SocketServer
 */
public class SocketServerTest extends TestCase
{
    public static final Logger logger_ = 
        Logger.getLogger(SocketServerTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entry point
     * 
     * @param  args  None
     */
    public static void main(String[] args)
    {
        TestRunner.run(SocketServerTest.class);
    }
    
    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests simple ping pong between client and server
     * 
     * @throws  Exception on error
     */
    public void testSocketServerPingPong() throws Exception
    {
        logger_.info("Running testSocketServerPingPong...");
        
        // Config server
        SocketServerConfig config = new SocketServerConfig();
        
        // Set handler
        config.setConnectionHandlerType(
            "toolbox.util.net.test.PingConnectionHandler");
        
        // Start server
        SocketServer server = new SocketServer(config);
        server.start();

        // Send server some messages
        int port = server.getServerPort();
        Socket socket = new Socket("localhost", port);
        SocketConnection sc = new SocketConnection(socket);

        // Send request
        PrintWriter pw = new PrintWriter(sc.getOutputStream());
        logger_.info("Client sent ping...");
        pw.println("ping");
        pw.flush();
        
        // Read response
        BufferedReader br = new BufferedReader(
            new InputStreamReader(sc.getInputStream()));
            
        String response = br.readLine();
        logger_.info("Client received " + response);

        // Cleanup        
        sc.close();
        server.stop();
    }
    
    /**
     * Tests SocketServer lifecycle state transitions
     * 
     * @throws  Exception on error
     */
    public void testSocketServerLifeCycle() throws Exception
    { 
        logger_.info("Running testSocketServerLifeCycle...");
        
        SocketServer ss = new SocketServer(new SocketServerConfig());
        
        // Start/stop immediately
        ss.start();
        ss.stop();  
        
        // Start/stop with a small delay
        ss.start();
        ThreadUtil.sleep(500);
        ss.stop();  
        
        // Start/stop with a longer delay
        ss.start();
        ThreadUtil.sleep(5000);
        ss.stop();
    }
    
    /**
     * Stress tests start/stop of socket server 
     * 
     * @throws  Exception on error
     */
    public void testSocketServerLifecycleStress() throws Exception
    {
        logger_.info("Running testSocketServerLifeCycleStress...");
        
        SocketServer server = new SocketServer(new SocketServerConfig());
        
        for(int i=0; i<20; i++)
        {
            server.start();
            server.stop();  
        }
    }
     
    /**
     * Tests socket server with many clients 
     * 
     * @throws  Exception on error
     */
    public void testSocketServerManyClients() throws Exception
    {
        logger_.info("Running testSocketServerManyClients...");
        
        SocketServerConfig config = new SocketServerConfig();
        
        config.setConnectionHandlerType(
            "toolbox.util.net.test.EchoConnectionHandler");
            
        config.setActiveConnections(10);
        SocketServer ss = new SocketServer(config);
        ss.start();
        
        final int port = ss.getServerPort();
        
        class Task extends Thread
        {
            String prefix = " ";
                        
            public Task(int taskNbr)
            {
                for(int i=0; i<taskNbr; i++)
                    prefix += "  ";
            }
            
            public void run()
            {
                try 
                {
                    EchoSocketClient client = new EchoSocketClient(port);
                    int x = 100;
                
                    client.sendMany(this + prefix + "msg ", x);
                    client.send(EchoConnectionHandler.TOKEN_TERMINATE);
                    client.close();
                }
                catch (Exception e)
                {
                    logger_.error("Task.run", e);
                }
            }   
        }
        
        int max = 10;
        Task tasks[] = new Task[max];
        
        // Spawn off a whole bunch of threads
        for(int i=0; i< max; i++)
        { 
            tasks[i] = new Task(i);
            tasks[i].start();
        }
        
        // Wait for each thread to end
        for(int j=0; j<max; j++)
            tasks[j].join();
            
        ss.stop();
    }
    
    /**
     * Tests firing of notification events exposed by ISocketServerListener
     * 
     * @throws  Exception on error
     */
    public void testFireNotification() throws Exception
    {
        logger_.info("Running testFireNotifcation...");
                
        // Config server
        SocketServerConfig config = new SocketServerConfig();
        
        // Set handler
        config.setConnectionHandlerType(
            "toolbox.util.net.test.NullConnectionHandler");
        
        // Start server and attach listener
        SocketServer server = new SocketServer(config);
        TestListener listener = new TestListener();
        server.addSocketServerListener(listener);
        server.start();

        // Connect to server
        int port = server.getServerPort();
        Socket socket = new Socket("localhost", port);
        SocketConnection sc = new SocketConnection(socket);
        sc.close();
        
        // Wait for accept notification
        listener.waitForAccept();
        
        // Race condition between accept() and handle()
        ThreadUtil.sleep(2000);
        
        logger_.info(server.toString());
        
        // Cleanup
        server.removeSocketServerListener(listener);
        server.stop();
    }
    
    //--------------------------------------------------------------------------
    // Helper Classes
    //--------------------------------------------------------------------------
    
    /**
     * Listener used to verify that notifications are being generated for
     * socket server events
     */        
    class TestListener implements ISocketServerListener
    {
        BlockingQueue accepted_ = new BlockingQueue();
            
        public void socketAccepted(Socket socket)
        {
            logger_.info("Listener notified of accept on socket " + socket);
            
            try
            {
                accepted_.push("accepted");
            }
            catch (InterruptedException e)
            {
                logger_.error("socketAccepted", e);
            }
        }
        
        public void waitForAccept() throws InterruptedException
        {
            accepted_.pull();
        }
    }
}
