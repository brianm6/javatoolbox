package toolbox.plugin.netmeter;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.text.NumberFormat;

import org.apache.log4j.Logger;

import toolbox.util.ThreadUtil;
import toolbox.util.io.MonitoredOutputStream;
import toolbox.util.io.throughput.DefaultThroughputMonitor;
import toolbox.util.io.throughput.ThroughputEvent;
import toolbox.util.io.throughput.ThroughputListener;
import toolbox.util.io.throughput.ThroughputMonitor;
import toolbox.util.net.SocketConnection;
import toolbox.util.service.AbstractService;
import toolbox.util.service.ServiceException;

/**
 * Client is a non-UI component that behaves as a Service. Its sole purpose is
 * to connect to well known Servers and initiate data transfer in order to 
 * measure throughput. Interested listeners can be notified of the statistics
 * as they are gathered.
 * 
 * @see toolbox.plugin.netmeter.Server
 */
public class Client extends AbstractService
{
    private static final Logger logger_ = Logger.getLogger(Client.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Client side socket connection to the server.
     */
    private SocketConnection conn_;
    
    /**
     * Hostname of the server we intend to connect to.
     */
    private String hostname_;
    
    /**
     * Port on the server we intend to connect to.
     */
    private int port_;
    
    /**
     * Stream that can measure data throughput.
     */
    private MonitoredOutputStream mos_;
    
    private ThroughputMonitor monitor_;
    
    /**
     * Internal flag used to terminate the connection.
     */
    private boolean stopped_;

    /**
     * Thread that client request is spawned off on in order for start()
     * to return immediately.
     */
    private Thread clientThread_;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Launches the standalone console based client.<br>
     * Example:<br> 
     * <code>
     * java toolbox.plugin.netmeter.Client localhost 9999
     * </code>
     * 
     * @param args Server hostname or IP address. 
     *             Second arg is the port number.
     * @throws Exception on error.
     */
    public static void main(String args[]) throws Exception
    {
        String hostname;
        int port;
        
        switch (args.length)
        {
            case 2  : hostname = args[0];
                      port = Integer.parseInt(args[1]); 
                      break;

            default : hostname = "127.0.0.1"; 
                      port = NetMeterPlugin.DEFAULT_PORT; 
        }
        
        Client c = new Client(hostname, port);
        c.start();
        
        ThreadUtil.sleep(10000);
        
        c.stop();
        
    }
    
    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Creates a loopback Client attached to NetMeter.DEFAULT_PORT.
     */
    public Client()
    {
        this("127.0.0.1", NetMeterPlugin.DEFAULT_PORT);
    }

    
    /**
     * Creates a Client.
     * 
     * @param hostname Server hostname.
     * @param port Server port.
     */
    public Client(String hostname, int port)
    {
        setHostname(hostname);
        setPort(port);
        monitor_ = new DefaultThroughputMonitor();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Adds a stats listener to the existing list.
     * 
     * @param listener Listener to add.
     */
    public void addThroughputListener(ThroughputListener listener)
    {
        monitor_.addThroughputListener(listener); 
    }
    
    
    /**
     * Returns the hostname.
     * 
     * @return String
     */
    public String getHostname()
    {
        return hostname_;
    }

    
    /**
     * Sets the hostname.
     * 
     * @param hostname The hostname to set.
     */
    public void setHostname(String hostname)
    {
        hostname_ = hostname;
    }

    
    /**
     * Returns the port.
     * 
     * @return int
     */
    public int getPort()
    {
        return port_;
    }

    
    /**
     * Sets the port.
     * 
     * @param port The port to set.
     */
    public void setPort(int port)
    {
        port_ = port;
    }

    //--------------------------------------------------------------------------
    // Service Interface 
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Service#start()
     */
    public void start() throws ServiceException
    {
        clientThread_ = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    conn_ = new SocketConnection(hostname_, port_);
            
                    mos_ = new MonitoredOutputStream(
                            "Client", 
                            new BufferedOutputStream(conn_.getOutputStream()));
                    
                    mos_.setThroughputMonitor(monitor_);
                    mos_.getThroughputMonitor().setSampleInterval(1000);
                    mos_.getThroughputMonitor().setMonitoringThroughput(true);

//                    mos_.getThroughputMonitor().addThroughputListener(
//                        new ThroughputCollector());
                    
                    byte[] b = "abcdefghijklmnopqrstuvwxyz123456789".getBytes();
            
                    while (!stopped_)
                        mos_.write(b);
            
                    mos_.flush();
                    conn_.close();
                }
                catch (IOException ioe)
                {
                    logger_.error(ioe);
                }
            }
        });
        
        clientThread_.start();
        super.start();
    }


    /**
     * @see toolbox.util.service.Service#stop()
     */
    public void stop() throws ServiceException
    {
        monitor_.setMonitoringThroughput(false);
        stopped_ = true;
        ThreadUtil.join(clientThread_);
        super.stop();
    }


    /**
     * @see toolbox.util.service.Service#pause()
     */
    public void pause() throws ServiceException
    {
        throw new IllegalArgumentException("Pause not supported");
    }


    /**
     * @see toolbox.util.service.Service#resume()
     */
    public void resume() throws ServiceException
    {
        throw new IllegalArgumentException("Resume not supported");
    }


    /**
     * @see toolbox.util.service.Service#isRunning()
     */
    public boolean isRunning()
    {
        return !stopped_;
    }


    /**
     * @see toolbox.util.service.Service#isPaused()
     */
    public boolean isPaused()
    {
        throw new IllegalArgumentException("Pause not supported");
    }
    
    //--------------------------------------------------------------------------
    // ThroughputCollector
    //--------------------------------------------------------------------------
    
    /**
     * ThroughputCollector is attached to the timer and collects throughput
     * statistics every second. 
     */
    class ThroughputCollector implements ThroughputListener
    {
        NumberFormat nf = NumberFormat.getIntegerInstance();
        
        /**
         * @see toolbox.util.io.throughput.ThroughputListener#currentThroughput(
         *      toolbox.util.io.throughput.ThroughputEvent)
         */
        public void currentThroughput(ThroughputEvent event)
        {
            logger_.info(
                "Client thruput: " + nf.format(event.getThroughput()) + " bytes/s");
        }
    }
}