package toolbox.tunnel;

import java.util.Map;

import javax.swing.JComponent;

import nu.xom.Element;

import toolbox.util.ui.plugin.IPlugin;
import toolbox.util.ui.plugin.IStatusBar;
import toolbox.util.ui.plugin.PluginWorkspace;

/**
 * Plugin wrapper for JTcpTunnel
 */
public class JTcpTunnelPlugin implements IPlugin
{
    /** 
     * Delegate 
     */
    private JTcpTunnelPane delegate_;

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

        delegate_ = new JTcpTunnelPane();
        delegate_.setStatusBar(statusBar);    
    }
    
    /**
     * @see toolbox.util.ui.plugin.IPlugin#getPluginName()
     */
    public String getPluginName()
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
        return delegate_;
    }

    /**
     * @see toolbox.util.ui.plugin.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        delegate_.applyPrefs(prefs);        
    }

    /**
     * @see toolbox.util.ui.plugin.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        delegate_.savePrefs(prefs);
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#shutdown()
     */
    public void shutdown()
    {
        delegate_ = null;
    }
}