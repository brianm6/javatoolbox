package toolbox.rtelnet;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.io.MonitoredOutputStream;
import toolbox.util.io.StringOutputStream;
import toolbox.util.io.transferred.TransferredEvent;
import toolbox.util.io.transferred.TransferredListener;
import toolbox.util.io.transferred.TransferredMonitor;

/**
 * RemoteTelnetClient is a specialization of the commons.net.TelnetClient 
 * with additional behavior to enable the easy submission of telnet commands 
 * and listening for certain keywords in the telnet command's response.
 * 
 * @see toolbox.rtelnet.RemoteTelnet
 * @see toolbox.rtelnet.RemoteTelnetInfo
 */
public class RemoteTelnetClient extends TelnetClient
{
    private static final Logger logger_ = 
        Logger.getLogger(RemoteTelnetClient.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Telnet responses are dumped here so that they can be searched. 
     */
    private StringOutputStream responseStream_;
   
    /**
     * Current position into the response read from the telnets input stream.
     */
    private int responseIndex_;

    /**
     * Monitors bytes received from telnet server so response can be parsed via
     * event notification instead of polling.
     */
    private MonitoredOutputStream mosStream_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a RemoteTelnetClient. 
     */
    public RemoteTelnetClient()
    {
        responseIndex_ = 0;
        responseStream_ = new StringOutputStream();
        mosStream_ = new MonitoredOutputStream(responseStream_);
        TransferredMonitor monitor = mosStream_.getTransferredMonitor();
        monitor.setSampleLength(1);
        monitor.addTransferredListener(new MyTransferredListener());
    }
    
    //--------------------------------------------------------------------------
    // Public 
    //--------------------------------------------------------------------------
    
    /**
     * Wait for a given string in the telnet output stream to be received.
     * 
     * @param searchString String to search for in the response.
     */    
    public void waitFor(String searchString)
    {
        while (true)
        {
            StringBuffer buffer = responseStream_.getBuffer();
            int foundAt = buffer.indexOf(searchString, responseIndex_);

            if (foundAt >= 0)
            {
                // skip over the found string so subsequent waitFor() will 
                // skip over everything we've already searched
                responseIndex_ = foundAt + searchString.length();
                return;
            }
            else
            {
                try
                {
                    synchronized (responseStream_)
                    {
                        responseStream_.wait(1000);
                    }
                }
                catch (InterruptedException e)
                {
                    logger_.error(e);
                }
            }

        }
    }

    
    /**
     * Sends a command to the telnet host to be executed.
     * 
     * @param command Command to execute.
     * @throws IOException on I/O error.
     */    
    public void sendCommand(String command) throws IOException
    {
        int responseBegin = responseStream_.getBuffer().length() - 1;
        getOutputStream().write((command + "\n").getBytes());
        getOutputStream().flush();
        
        // TODO: We need to sleep only long enough to make sure that all the
        //       output for the submitted command has been written back to 
        //       us, the telnet client. Research RFC on how to do this.
        //  
        //   OR: Add ability to sense when the stream is dormant over a given
        //       period of time.
        
        ThreadUtil.sleep(1000);
        
        StringBuffer allResponses = responseStream_.getBuffer();
        int responseEnd = allResponses.length() - 1;
        String response = allResponses.substring(responseBegin, responseEnd);
        
        logger_.debug(StringUtil.banner(
            "Request: " + command + "\nResponse: " + response));
    }

    //--------------------------------------------------------------------------
    // Overrides org.apache.commons.net.telnet.TelnetClient 
    //--------------------------------------------------------------------------
    
    /**
     * Hook into the connect method to start up the output stream reader.
     * 
     * @param hostname Hostname to connect to.
     * @param port Telnet port on host.
     * @throws SocketException on socket error.
     * @throws IOException on I/O error.
     */
    public void connect(String hostname, int port)
        throws SocketException, IOException
    {
        registerSpyStream(responseStream_);
        super.connect(hostname, port);
     
        ThreadUtil.sleep(1000);
        
        logger_.debug(StringUtil.banner(
            "Connect response: \n" 
            + responseStream_.toString()));
    }
    
    
    /**
     * @see org.apache.commons.net.telnet.TelnetClient#disconnect()
     */
    public void disconnect() throws IOException
    {
        super.disconnect();
    }

    //--------------------------------------------------------------------------
    // MyTransferredListener
    //--------------------------------------------------------------------------
    
    /**
     * MyTransferredListener is responsible for notifying the response stream
     * whenever bytes are transferred. 
     */
    class MyTransferredListener implements TransferredListener
    {
        /**
         * @see toolbox.util.io.transferred.TransferredListener#
         *      bytesTransferred(toolbox.util.io.transferred.TransferredEvent)
         */
        public void bytesTransferred(TransferredEvent event)
        {
            synchronized(responseStream_)
            {
                responseStream_.notify();
            }
        }
    }
}