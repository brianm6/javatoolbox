package toolbox.util.net.test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

/**
 * Simple socket client for the EchoConnectionHandler
 */
public class EchoSocketClient
{
    /** Logger **/
    public static final Logger logger_ = 
        Logger.getLogger(EchoSocketClient.class);
    
    /** Wrapped socket **/
    private Socket socket;
    
    private PrintWriter writer;
    private LineNumberReader reader;
    
    /**
     * Create socket client on localhost with given port
     * 
     * @param   port    Socket port
     */
    public EchoSocketClient(int port) throws UnknownHostException
    {
        this(InetAddress.getLocalHost().getHostAddress(), port);
    }
    
    /**
     * Creates socket client to given host/port 
     *  
     * @param  hostname  Hostname of machine to connect to
     * @param  port      TCP port to connection to 
     */
    public EchoSocketClient(String hostname, int port)
    {
        try
        {
            socket = new Socket(hostname, port);
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new LineNumberReader(new InputStreamReader(socket.getInputStream()));
            
        }
        catch(Exception e)
        {
            logger_.error("While creating socket", e);
        }
    }
    
    /**
     * Sends a request to socket and reads the response
     * 
     * @param   request  Message to send
     * @return  Response from server
     * @throws  IOException
     */
    public String send(String request) throws IOException
    {
        writer.println(request);
        writer.flush();
        String response = reader.readLine();
        return response;
    }
    
    /**
     * Sends request x number of times 
     * 
     * @param   request  Message to send
     * @param   num      Number of times to send the message
     * @throws  IOException
     */
    public void sendMany(String request, int num) throws IOException 
    {
        for(int i=0; i<num; i++)
            send(request + i);  
    }
    
    /**
     * Termines the connection by sending the TERMINATE token
     */
    public void close() throws IOException
    {
        writer.println(EchoConnectionHandler.TOKEN_TERMINATE);
        writer.flush();
        socket.close();
    }       
}

