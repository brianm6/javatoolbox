package toolbox.plugin.tunnel;

import java.util.Map;

import javax.swing.JComponent;

import nu.xom.Element;

import toolbox.workspace.IPlugin;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.PluginWorkspace;

/**
 * Plugin wrapper for JTcpTunnel
 */
public class TunnelPlugin implements IPlugin
{
    /** 
     * Delegate. 
     */
    private TunnelPane delegate_;

    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPlugin#startup(Map)
     */
    public void startup(Map params)
    {
        IStatusBar statusBar = null;
        
        if (params != null)
            statusBar = (IStatusBar) params.get(PluginWorkspace.PROP_STATUSBAR);

        delegate_ = new TunnelPane();
        delegate_.setStatusBar(statusBar);    
    }
    
    /**
     * @see toolbox.workspace.IPlugin#getPluginName()
     */
    public String getPluginName()
    {
        return "TCP Tunnel";
    }

    /**
     * @see toolbox.workspace.IPlugin#getDescription()
     */
    public String getDescription()
    {
        return "TCP Tunnel allows you to snoop on incoming/outgoing traffic " + 
               "by creating an intermediate 'tunnel proxy' between two TCP " +
               "connection endpoints.";
    }

    /**
     * @see toolbox.workspace.IPlugin#getComponent()
     */
    public JComponent getComponent()
    {
        return delegate_;
    }

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        delegate_.applyPrefs(prefs);        
    }

    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        delegate_.savePrefs(prefs);
    }

    /**
     * @see toolbox.workspace.IPlugin#shutdown()
     */
    public void shutdown()
    {
        delegate_ = null;
    }
}