package toolbox.tunnel;

import java.awt.Component;
import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import toolbox.util.ui.plugin.IPlugin;
import toolbox.util.ui.plugin.IStatusBar;

/**
 * Plugin wrapper for JTcpTunnel
 */
public class JTcpTunnelPlugin implements IPlugin
{
    /**
     * Delegate
     */
    private JTcpTunnelPane jtcpTunnelPane_;
    
    //--------------------------------------------------------------------------
    // Interface IPlugin
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plugin.IPlugin#getName()
     */
    public String getName()
    {
        return "TCP Tunnel";
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getComponent()
     */
    public Component getComponent()
    {
        return jtcpTunnelPane_;
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getMenuBar()
     */
    public JMenuBar getMenuBar()
    {
        JMenu menu = new JMenu(getName());
        menu.add(new JMenuItem(jtcpTunnelPane_.new StartTunnelAction()));
        menu.add(new JMenuItem(jtcpTunnelPane_.new StopTunnelAction()));
        menu.add(new JMenuItem(jtcpTunnelPane_.new ClearAction()));
        
		JMenuBar jmb = new JMenuBar();
		jmb.add(menu);

        return jmb;
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#init()
     */
    public void init()
    {
        jtcpTunnelPane_ = new JTcpTunnelPane();
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#savePrefs(Properties)
     */
    public void savePrefs(Properties prefs)
    {
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#applyPrefs(Properties)
     */
    public void applyPrefs(Properties prefs)
    {
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#setStatusBar(IStatusBar)
     */
    public void setStatusBar(IStatusBar statusBar)
    {
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#shutdown()
     */
    public void shutdown()
    {
    }
}