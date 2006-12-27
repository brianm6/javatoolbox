package toolbox.plugin.netmeter;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.io.MonitoredInputStream;
import toolbox.util.io.throughput.ThroughputMonitor;
import toolbox.util.net.IConnection;
import toolbox.util.net.IConnectionHandler;

/**
 * ServerConnectionHandler is a server side connection handler for incoming
 * client requests. 
 * 
 * @see toolbox.plugin.netmeter.Server
 */
public class ServerConnectionHandler implements IConnectionHandler
{
    private static final Logger logger_ = 
        Logger.getLogger(ServerConnectionHandler.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * UI component for the server.
     */
    private ServerView serverView_;

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the server view.
     * 
     * @param serverView ServerView
     */
    public void setServerView(ServerView serverView) 
    {
        logger_.debug(StringUtil.banner("setServerView called"));
        serverView_ = serverView;
    }
    
    
    /**
     * Returns the ServerView.
     * 
     * @return ServerView
     */
    public ServerView getServerView() 
    {
        return serverView_;
    }
    
    //--------------------------------------------------------------------------
    // IConnectionHandler Interface 
    //--------------------------------------------------------------------------
        
    /*
     * @see toolbox.util.net.IConnectionHandler#handle(toolbox.util.net.IConnection)
     */
    public Object handle(IConnection conn)
    {
        MonitoredInputStream mis = null;
        ThroughputMonitor monitor = null;
        
        try
        {
            mis = new MonitoredInputStream(conn.getInputStream());
            monitor = mis.getThroughputMonitor();
            
            //logger_.debug(StringUtil.banner("getServerViewCalled"));
        
            synchronized(this) 
            {
                wait();
            }
            
            monitor.addThroughputListener(getServerView());
            monitor.setMonitoringThroughput(true);
            
            while (mis.read() != -1) 
            {
                ThreadUtil.sleep(0);
            }
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
        finally 
        {
            // Clean up
            
            if (monitor != null) 
            {
                monitor.setMonitoringThroughput(false);
                monitor.removeThroughputListener(getServerView());
            }
            
            IOUtils.closeQuietly(mis);
        }
        
        return null;
    }
}