/**
 * Copyright 2002, Southwest Airlines
 * All Rights Reserved
 */
package toolbox.util.net.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

import org.apache.log4j.Category;

import toolbox.util.ThreadUtil;
import toolbox.util.net.SocketConnection;
import toolbox.util.net.SocketServer;
import toolbox.util.net.SocketServerConfig;


/**
 * Unit test for SocketServer
 */
public class SocketServerTest extends TestCase
{
    /** Logger **/
    public static final Category logger_ = 
        Category.getInstance(SocketServerTest.class);
    
    /**
     * Entry point
     */
    public static void main(String[] args)
    {
        TestRunner.run(SocketServerTest.class);
    }

    /**
     * Constructor for SocketServerTest
     */
    public SocketServerTest(String arg)
    {
        super(arg);
    }

    /**
     * Tests simple ping pong between client and server
     */
    public void testSocketServerPingPong() throws Exception
    {
        /* TODO: try/catch/finally */
        
        /* config server */
        SocketServerConfig config = new SocketServerConfig();
        
        /* set handler */
        config.setConnectionHandlerType(
            "toolbox.util.net.test.PingConnectionHandler");
        
        /* start server */
        SocketServer server = new SocketServer(config);
        server.start();

        /* send server some messages */
        int port = server.getServerPort();
        Socket socket = new Socket("localhost", port);
        SocketConnection sc = new SocketConnection(socket);

        /* send request */      
        PrintWriter pw = new PrintWriter(sc.getOutputStream());
        logger_.info("Client sent ping...");
        pw.println("ping");
        pw.flush();
        
        /* read response */
        BufferedReader br = new BufferedReader(
            new InputStreamReader(sc.getInputStream()));
            
        String response = br.readLine();
        logger_.info("Client received " + response);
        
        sc.close();

        server.stop();
    }
    
    /**
     * Tests SocketServer lifecycle state transitions
     */
    public void testSocketServerLifeCycle() throws Exception
    { 
        SocketServer ss = new SocketServer(new SocketServerConfig());
        
        /* start/stop immediately */
        ss.start();
        ss.stop();  
        
        /* start/stop with a small delay */
        ss.start();
        ThreadUtil.sleep(500);
        ss.stop();  
        
        /* start/stop with a longder delay */
        ss.start();
        ThreadUtil.sleep(5000);
        ss.stop();
    }
    
    /**
     * Stress tests start/stop of socket server 
     */
    public void testSocketServerLifecycleStress() throws Exception
    {
        SocketServer server = new SocketServer(new SocketServerConfig());
        
        for(int i=0; i<20; i++)
        {
            server.start();
            server.stop();  
        }
    }
     
    /**
     * Tests socket server with many clients 
     */
    public void testSocketServerManyClients() throws Exception
    {
        /* TODO: try/catch/finally */
        
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
                catch(Exception e)
                {
                    logger_.error("In Task.run()", e);
                }
            }   
        }
        
        int max = 10;
        Task tasks[] = new Task[max];
        
        /* spawn off a whole bunch of threads */
        for(int i=0; i< max; i++)
        { 
            tasks[i] = new Task(i);
            tasks[i].start();
        }
        
        /* wait for each thread to end */
        for(int j=0; j<max; j++)
            tasks[j].join();
            
        ss.stop();
    }
}