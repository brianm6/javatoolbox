package toolbox.plugin.netmeter;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.io.EventOutputStream;
import toolbox.util.net.SocketConnection;

/**
 * NetMeter Client. 
 */
public class Client extends AbstractService
{
    private static final Logger logger_ = Logger.getLogger(Client.class);
    
    /**
     * Client socket connection to the server.
     */
    private SocketConnection conn_;
    
    /**
     * Hostname of the server.
     */
    private String hostname_;
    
    /**
     * Server port.
     */
    private int port_;
    
    /**
     * Stream that can meter the data throughput.
     */
    private EventOutputStream os_;
    
    /**
     * Flag for when the client is stopped.
     */
    private boolean stopped_;

    /**
     * Thread that client request is spawned off on in order for start()
     * to return immediately.
     */
    private Thread clientThread_;
    
    /**
     * Timer that gathers throughput statistics every second.
     */
    private Timer timer_;
    
    /**
     * Array of listeners interested in the collected throughput stats.
     */
    private StatsListener[] listeners_;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args First arg is the server hostname. Second arg is the port
     *        number.
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
     * Creates a loopback Client attached to Server.DEFAULT_PORT
     */
    public Client()
    {
        this("127.0.0.1", NetMeterPlugin.DEFAULT_PORT);
    }

    
    /**
     * Creates a Client.
     * 
     * @param hostname Server hostname
     * @param port Server port
     */
    public Client(String hostname, int port)
    {
        hostname_ = hostname;
        port_ = port;
        listeners_ = new StatsListener[0];
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Adds a stats listener to the existing list.
     * 
     * @param listener Listener to add.
     */
    public void addStatsListener(StatsListener listener)
    {
        listeners_ = (StatsListener[]) ArrayUtil.add(listeners_, listener); 
    }
    
    
    /**
     * @return Returns the hostname.
     */
    public String getHostname()
    {
        return hostname_;
    }

    
    /**
     * @param hostname The hostname to set.
     */
    public void setHostname(String hostname)
    {
        hostname_ = hostname;
    }

    
    /**
     * @return Returns the port.
     */
    public int getPort()
    {
        return port_;
    }

    
    /**
     * @param port The port to set.
     */
    public void setPort(int port)
    {
        port_ = port;
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Fires notification of a new throughput statistic.
     * 
     * @param throughput Throughput in kilobytes per second.
     */
    protected void fireThroughput(int throughput)
    {
        for (int i=0; 
             i<listeners_.length; 
             listeners_[i++].throughput(throughput));
    }
    
    //--------------------------------------------------------------------------
    // Service Interface 
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.plugin.netmeter.Service#start()
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
            
                    os_ = new EventOutputStream("Client", 
                              new BufferedOutputStream(
                                  conn_.getOutputStream()));
            
                    timer_ = new Timer();
                    timer_.schedule(new ThroughputCollector(), 1000, 1000);
            
                    byte[] b = "hoooooooooopppppppppppppppppptrtttttttttttttttt".getBytes();
            
                    while (!stopped_)
                        os_.write(b);
            
                    os_.flush();
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
     * @see toolbox.plugin.netmeter.Service#stop()
     */
    public void stop() throws ServiceException
    {
        timer_.cancel();    
        stopped_ = true;
        ThreadUtil.join(clientThread_);
        super.stop();
    }


    /**
     * @see toolbox.plugin.netmeter.Service#pause()
     */
    public void pause() throws ServiceException
    {
        super.pause();
    }


    /**
     * @see toolbox.plugin.netmeter.Service#resume()
     */
    public void resume() throws ServiceException
    {
        super.resume();
    }


    /**
     * @see toolbox.plugin.netmeter.Service#isRunning()
     */
    public boolean isRunning()
    {
        return false;
    }


    /**
     * @see toolbox.plugin.netmeter.Service#isPaused()
     */
    public boolean isPaused()
    {
        return false;
    }
    
    //--------------------------------------------------------------------------
    // ThroughputCollector
    //--------------------------------------------------------------------------
    
    /**
     * ThroughputCollector is attached to the timer and collects throughput
     * statistics every second. 
     */
    class ThroughputCollector extends TimerTask
    {
        int lastCount_ = 0;
        
        public void run()
        {
            int current = os_.getCount();
            int delta = current - lastCount_;
            lastCount_ = current;
            
            NumberFormat nf = NumberFormat.getIntegerInstance();
            logger_.info("Client thruput: " + nf.format(delta/1000) + " kb/s");
            fireThroughput(delta/1000);
        }
    }
}