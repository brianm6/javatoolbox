package toolbox.workspace.host;

import java.awt.BorderLayout;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;


import toolbox.util.SwingUtil;
import toolbox.workspace.IPlugin;

/**
 * Plugin host that associates each plugin with a JInternalFrame. Multiple
 * plugins are accessible in an MDI manner because of the parent JDesktopPane. 
 */
public class DesktopPluginHost extends AbstractPluginHost
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Desktop
     */
    private JDesktopPane desktop_;
    
    /**
     * Maps IPlugin -> JInternalFrame
     */
    private Map pluginToFrameMap_;
    
    //--------------------------------------------------------------------------
    // PluginHost Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.PluginHost#startup(java.util.Map)
     */
    public void startup(Map props)
    {
        super.startup(props);
        
        desktop_ = new JDesktopPane();
        pluginToFrameMap_ = new HashMap();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SwingUtil.tile(desktop_);
            }
        });
    }

    
    /**
     * @see toolbox.workspace.PluginHost#addPlugin(toolbox.workspace.IPlugin)
     */
    public void addPlugin(IPlugin plugin)
    {
        super.addPlugin(plugin);
    }

    
    /**
     * @see toolbox.workspace.PluginHost#removePlugin(toolbox.workspace.IPlugin)
     */
    public void removePlugin(IPlugin plugin)
    {
        super.removePlugin(plugin);
    }

    
    /**
     * @see toolbox.workspace.host.AbstractPluginHost#importPlugin(toolbox.workspace.IPlugin)
     */
    public void importPlugin(IPlugin plugin)
    {
        super.importPlugin(plugin);
        
        JInternalFrame frame = new JInternalFrame(
                                    plugin.getPluginName(),
                                    true,
                                    true,
                                    true,
                                    true);
        
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(plugin.getComponent(), BorderLayout.CENTER);
        frame.setVisible(true);
        desktop_.add(frame);
        frame.pack();
        pluginToFrameMap_.put(plugin, frame);
        
        try
        {
            frame.setSelected(true);    
        }
        catch (PropertyVetoException e)
        {
        }
    }

    
    /**
     * @see toolbox.workspace.host.AbstractPluginHost#exportPlugin(toolbox.workspace.IPlugin)
     */
    public void exportPlugin(IPlugin plugin)
    {
        desktop_.remove((JComponent)pluginToFrameMap_.get(plugin));
        pluginToFrameMap_.remove(plugin);
        
        super.exportPlugin(plugin);
    }    
    
    
    /**
     * @see toolbox.workspace.PluginHost#shutdown()
     */
    public void shutdown()
    {
        desktop_.removeAll();
        desktop_ = null;
        
        pluginToFrameMap_.clear();
        pluginToFrameMap_ = null;
        
        super.shutdown();
    }

    
    /**
     * @see toolbox.workspace.host.PluginHost#getComponent()
     */
    public JComponent getComponent()
    {
        return desktop_;
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#getName()
     */
    public String getName()
    {
        return "Desktop";
    }
}