package toolbox.rtelnet;

import java.io.IOException;
import java.io.LineNumberReader;
import java.net.SocketException;

import org.apache.commons.net.telnet.TelnetClient;

import toolbox.util.ThreadUtil;

/**
 * RemoteTelnetClient is a specialization of the commons.net.TelnetClient 
 * with additional behavior to enable the easy submission of telnet commands 
 * and listening for certain keywords in the telnet command's response.
 */
public class RemoteTelnetClient extends TelnetClient implements Runnable
{
    /** Telnet response read from here */
    private LineNumberReader lnr_;
    
    /** Telnet respones are buffered here so that they can be searched */
    private StringBuffer outputBuffer_ = new StringBuffer();

    //--------------------------------------------------------------------------
    // Public 
    //--------------------------------------------------------------------------
    
    /**
     * Wait for a given string in the telnet output stream to be received
     * 
     * @param    searchString  String to search for in the response
     */    
    public synchronized void waitFor(String searchString)
    {
        while (true)
        {
            if (outputBuffer_.indexOf(searchString) >= 0)
            {
                outputBuffer_.delete(0, outputBuffer_.length()-1);
                return;
            }
            else
                ThreadUtil.sleep(1);
        }
    }

    /**
     * Sends a command to the telnet host to be executed
     * 
     * @param   command  Command to execute
     * @throws  IOException on IO error
     */    
    public void sendCommand(String command) throws IOException
    {
        getOutputStream().write((command+"\n").getBytes());
        getOutputStream().flush();
        
        // Sleep to avoid race condition
        ThreadUtil.sleep(1000);
    }

    //--------------------------------------------------------------------------
    // Runnable Interface
    //--------------------------------------------------------------------------
    
    /** 
     * Reads from telnet connections output stream and populates the
     * outputBuffer
     */    
    public void run()
    {
        try
        {
            while (true)
            {
                int c = getInputStream().read();
                char x = (char)c;
                System.out.print(x); 
                outputBuffer_.append(x);                    
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    //--------------------------------------------------------------------------
    // Overridden from org.apache.commons.net.telnet.TelnetClient 
    //--------------------------------------------------------------------------
    
    /**
     * Hook into connect method to start up the output stream reader
     * 
     * @param  hostname  Hostname to connect to
     * @param  port      Telnet port on host
     * @throws SocketException on socket error
     * @throws IOException on IO error
     */
    public void connect(String hostname, int port)
        throws SocketException, IOException
    {
        super.connect(hostname, port);
        Thread t = new Thread(this);
        t.start();
    }
}