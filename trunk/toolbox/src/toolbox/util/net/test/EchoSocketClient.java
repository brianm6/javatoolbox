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
    
    /** 
     * Wrapped socket 
     */
    private Socket socket_;
    
    private PrintWriter writer_;
    
    private LineNumberReader reader_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Create socket client on localhost with given port
     * 
     * @param   port    Socket port
     * @throws  UnknownHostException when host not found
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
            socket_ = new Socket(hostname, port);
            
            writer_ = 
                new PrintWriter(
                    new OutputStreamWriter(socket_.getOutputStream()));
            
            reader_ = 
                new LineNumberReader(
                    new InputStreamReader(socket_.getInputStream()));
            
        }
        catch(Exception e)
        {
            logger_.error("While creating socket", e);
        }
    }
    
    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * Sends a request to socket and reads the response
     * 
     * @param   request  Message to send
     * @return  Response from server
     * @throws  IOException on IO error
     */
    public String send(String request) throws IOException
    {
        writer_.println(request);
        writer_.flush();
        String response = reader_.readLine();
        return response;
    }
    
    /**
     * Sends request x number of times 
     * 
     * @param   request  Message to send
     * @param   num      Number of times to send the message
     * @throws  IOException on IO error
     */
    public void sendMany(String request, int num) throws IOException 
    {
        for(int i=0; i<num; i++)
            send(request + i);  
    }
    
    /**
     * Termines the connection by sending the TERMINATE token
     * 
     * @throws IOException on IO error
     */
    public void close() throws IOException
    {
        writer_.println(EchoConnectionHandler.TOKEN_TERMINATE);
        writer_.flush();
        socket_.close();
    }       
}

