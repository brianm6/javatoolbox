package toolbox.plugin.netmeter;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import toolbox.util.ThreadUtil;
import toolbox.util.io.EventOutputStream;
import toolbox.util.net.SocketConnection;

/**
 * NetMeter Client. 
 */
public class Client implements Service
{
    private static final Logger logger_ = 
        Logger.getLogger(Client.class);
        
    private SocketConnection conn_;
    private String hostname_;
    private int port_;
    private EventOutputStream os_;
    private boolean stopped_;

    private Thread clientThread_;
    private Timer timer_;
    
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
                      port = Server.DEFAULT_PORT; 
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
     * Creates a Client.
     */
    public Client()
    {
        this("127.0.0.1", Server.DEFAULT_PORT);
    }

    /**
     * Creates a Client.
     */
    public Client(String hostname, int port)
    {
        hostname_ = hostname;
        port_ = port;
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
            
                    //ElapsedTime time = new ElapsedTime();
            
                    byte[] b = "hoooooooooopppppppppppppppppptrtttttttttttttttt".getBytes();
            
                    //for(int i=0; i<500000; i++)
                    //    os_.write(b);
            
                    while (!stopped_)
                    {
                        os_.write(b);
                    }
            
                    os_.flush();
                    conn_.close();
                    
                    //time.setEndTime();
            
                    //double seconds = time.getSeconds() +  (time.getMillis()/(double)1000);
                    //double thruput = (os_.getCount() / (double) seconds);            
            
                    //NumberFormat nf = NumberFormat.getIntegerInstance();
            
                    //logger_.info(
                    //    "Client thruput: " + 
                    //    nf.format(os_.getCount()) + "/" + nf.format(seconds) + " ==> " + 
                    //    nf.format(thruput/1000) + " kb/s");
            
            
                }
                catch (IOException ioe)
                {
                    logger_.error(ioe);
                }
                
            }
        });
        
        clientThread_.start();
    }


    /**
     * @see toolbox.plugin.netmeter.Service#stop()
     */
    public void stop() throws ServiceException
    {
        timer_.cancel();    
        stopped_ = true;
        ThreadUtil.join(clientThread_);
    }


    /**
     * @see toolbox.plugin.netmeter.Service#pause()
     */
    public void pause() throws ServiceException
    {
    }


    /**
     * @see toolbox.plugin.netmeter.Service#resume()
     */
    public void resume() throws ServiceException
    {
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
        }
    }
}