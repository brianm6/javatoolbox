package toolbox.plugin.netmeter;

import java.io.BufferedOutputStream;
import java.io.IOException;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import toolbox.util.ElapsedTime;
import toolbox.util.io.EventOutputStream;
import toolbox.util.net.SocketConnection;

/**
 * NetMeter Client 
 */
public class Client extends JPanel implements Service
{
    private static final Logger logger_ = 
        Logger.getLogger(Client.class);
        
    private SocketConnection conn_;
    
    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Creates a Client.
     */
    public Client()
    {
    }
    
    //--------------------------------------------------------------------------
    // Service Interface 
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.plugin.netmeter.Service#start()
     */
    public void start() throws ServiceException
    {
        try
        {
            conn_ = new SocketConnection("localhost", 19999);
            //conn_.connect();
            EventOutputStream os = 
                new EventOutputStream("Client", 
                    new BufferedOutputStream(conn_.getOutputStream()));
            
            ElapsedTime time = new ElapsedTime();
            
            byte[] b = "hoooooooooopppppppppppppppppptrtttttttttttttttt".getBytes();
            for(int i=0; i<500000; i++)
                os.write(b);
            
            os.flush();
            conn_.close();
                            
            time.setEndTime();
            double thruput = os.getCount()/time.getSeconds();            
            
            logger_.info("Client thruput: " + thruput + "/" + time.getSeconds() + "==>"  + thruput + "KBytes/sec");
            
            
        }
        catch (IOException ioe)
        {
            throw new ServiceException(ioe);
        }
    }


    /**
     * @see toolbox.plugin.netmeter.Service#stop()
     */
    public void stop() throws ServiceException
    {
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
}