package toolbox.tunnel;

import java.util.Map;
import java.util.Properties;

import javax.swing.JComponent;

import toolbox.util.ui.plugin.IPlugin;
import toolbox.util.ui.plugin.IStatusBar;
import toolbox.util.ui.plugin.PluginWorkspace;

/**
 * Plugin wrapper for JTcpTunnel
 */
public class JTcpTunnelPlugin implements IPlugin
{
    /** Delegate */
    private JTcpTunnelPane jtcpTunnelPane_;

    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.ui.plugin.IPlugin#startup(Map)
     */
    public void startup(Map params)
    {
        IStatusBar statusBar = null;
        
        if (params != null)
            statusBar = (IStatusBar) params.get(PluginWorkspace.PROP_STATUSBAR);

        jtcpTunnelPane_ = new JTcpTunnelPane();
        jtcpTunnelPane_.setStatusBar(statusBar);    
    }
    
    /**
     * @see toolbox.util.ui.plugin.IPlugin#getName()
     */
    public String getName()
    {
        return "TCP Tunnel";
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getDescription()
     */
    public String getDescription()
    {
        return "TCP Tunnel allows you to snoop on incoming/outgoing traffic " + 
               "by creating an intermediate 'tunnel proxy' between two TCP " +
               "connection endpoints.";
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getComponent()
     */
    public JComponent getComponent()
    {
        return jtcpTunnelPane_;
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#applyPrefs(Properties)
     */
    public void applyPrefs(Properties prefs)
    {
        jtcpTunnelPane_.applyPrefs(prefs);
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#savePrefs(Properties)
     */
    public void savePrefs(Properties prefs)
    {
        jtcpTunnelPane_.savePrefs(prefs);
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#shutdown()
     */
    public void shutdown()
    {
        jtcpTunnelPane_ = null;
    }
}