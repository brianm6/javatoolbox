package toolbox.plugin.netmeter;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.Map;

import org.apache.log4j.Logger;

import toolbox.util.ThreadUtil;
import toolbox.util.io.Bandwidth;
import toolbox.util.io.MonitoredOutputStream;
import toolbox.util.io.ThrottledOutputStream;
import toolbox.util.io.throughput.ThroughputEvent;
import toolbox.util.io.throughput.ThroughputListener;
import toolbox.util.net.SocketConnection;
import toolbox.util.service.Destroyable;
import toolbox.util.service.Initializable;
import toolbox.util.service.ObservableService;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceListener;
import toolbox.util.service.ServiceNotifier;
import toolbox.util.service.ServiceState;
import toolbox.util.service.ServiceTransition;
import toolbox.util.service.ServiceUtil;
import toolbox.util.service.Startable;
import toolbox.util.statemachine.StateMachine;

/**
 * Client is a non-UI component that behaves as a Service. Its sole purpose is
 * to connect to well known Servers and initiate data transfer in order to 
 * measure throughput. Interested listeners can be notified of the statistics
 * as they are gathered.
 * 
 * @see toolbox.plugin.netmeter.Server
 */
public class Client implements Initializable, Startable, Destroyable, 
    ObservableService
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
    
    /**
     * Throttles the monitoredoutputstream.
     */
    private ThrottledOutputStream tos_;
    
    /**
     * Bandwidth for the throttled output stream.
     */
    private Bandwidth bandwidth_;
    
    /**
     * Thread that client request is spawned off on in order for start() to 
     * return immediately.
     */
    private Thread clientThread_;
    
    /**
     * State machine for this client.
     */
    private StateMachine machine_;
    
    /**
     * Notifier for service related events.
     */
    private ServiceNotifier notifier_;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Launches the standalone console based client.<p>
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
     * 
     * @throws ServiceException on error.
     */
    public Client() throws ServiceException
    {
        this("127.0.0.1", NetMeterPlugin.DEFAULT_PORT);
    }

    
    /**
     * Creates a Client.
     * 
     * @param hostname Server hostname.
     * @param port Server port.
     * @throws ServiceException on error.
     */
    public Client(String hostname, int port) throws ServiceException
    {
        machine_ = ServiceUtil.createStateMachine(this);
        notifier_ = new ServiceNotifier(this);
        
        setHostname(hostname);
        setPort(port);
        setBandwidth(new Bandwidth(500, 500, Bandwidth.TYPE_BOTH));
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
        mos_.getThroughputMonitor().addThroughputListener(listener);
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

    
    /**
     * Returns the bandwidth allocated for this client.
     * 
     * @return Bandwidth
     */
    public Bandwidth getBandwidth()
    {
        
        if (tos_ ==  null)
            return bandwidth_;
        else
            return tos_.getBandwidth(); 
    }
    
    
    /**
     * Sets the bandwidth.
     * 
     * @param bandwidth Bandwidth to set.
     */
    public void setBandwidth(Bandwidth bandwidth)
    {
        bandwidth_ = bandwidth;
        if (tos_ != null)
            tos_.setBandwidth(bandwidth);
    }

    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState()
    {
        return (ServiceState) machine_.getState();
    }
    
    //--------------------------------------------------------------------------
    // Initializable Interface 
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map configuration) throws ServiceException
    {
        machine_.checkTransition(ServiceTransition.INITIALIZE);
        machine_.transition(ServiceTransition.INITIALIZE);
    }
    
    //--------------------------------------------------------------------------
    // Startable Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.Startable#start()
     */
    public void start() throws ServiceException
    {
        machine_.checkTransition(ServiceTransition.START);
        
        clientThread_ = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    mos_ = new MonitoredOutputStream(
                            "Client", 
                            new BufferedOutputStream(conn_.getOutputStream()));
                    
                    mos_.getThroughputMonitor().setSampleInterval(1000);
                    mos_.getThroughputMonitor().setMonitoringThroughput(true);
                    
                    tos_ = new ThrottledOutputStream(mos_);
                    tos_.setBandwidth(bandwidth_);
                    
                    byte[] b = "abcdefghijklmnopqrstuvwxyz123456789".getBytes();
            
                    while (isRunning()) 
                    {
                        tos_.write(b);
                        ThreadUtil.sleep(0);
                    }
            
                    tos_.flush();
                    conn_.close();
                }
                catch (IOException ioe)
                {
                    logger_.error(ioe);
                }
            }
        });

        // Connect before the thread is spawned so success/failure can be
        // handled.
        
        try
        {
            conn_ = new SocketConnection(hostname_, port_);
        }
        catch (UnknownHostException uhe)
        {
            throw new ServiceException(uhe);
        }
        catch (IOException ioe)
        {
            throw new ServiceException(ioe);
        }
        
        clientThread_.start();
        machine_.transition(ServiceTransition.START);
    }


    /*
     * @see toolbox.util.service.Startable#stop()
     */
    public void stop() throws ServiceException
    {
        machine_.checkTransition(ServiceTransition.STOP);
        machine_.transition(ServiceTransition.STOP);
        mos_.getThroughputMonitor().setMonitoringThroughput(false);
        ThreadUtil.join(clientThread_);
    }

    
    /*
     * @see toolbox.util.service.Startable#isRunning()
     */
    public boolean isRunning()
    {
        return getState() == ServiceState.RUNNING;
    }
    
    //--------------------------------------------------------------------------
    // Destroyable Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy() throws ServiceException
    {
        machine_.checkTransition(ServiceTransition.DESTROY);
        machine_.transition(ServiceTransition.DESTROY);
    }
    
    /*
     * @see toolbox.util.service.Destroyable#isDestroyed()
     */
    public boolean isDestroyed() {
        return getState() == ServiceState.DESTROYED;
    }
    
    //--------------------------------------------------------------------------
    // ObservableService Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.ObservableService#addServiceListener(toolbox.util.service.ServiceListener)
     */
    public void addServiceListener(ServiceListener listener)
    {
        notifier_.addServiceListener(listener);
    }


    /*
     * @see toolbox.util.service.ObservableService#removeServiceListener(toolbox.util.service.ServiceListener)
     */
    public void removeServiceListener(ServiceListener listener)
    {
        notifier_.removeServiceListener(listener);
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
        
        /*
         * @see toolbox.util.io.throughput.ThroughputListener#currentThroughput(toolbox.util.io.throughput.ThroughputEvent)
         */
        public void currentThroughput(ThroughputEvent event)
        {
            logger_.info(
                "Client thruput: " 
                + nf.format(event.getThroughput()) 
                + " bytes/s");
        }
    }
}