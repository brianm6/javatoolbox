package toolbox.workspace.host;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

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
     * Desktop.
     */
    private JDesktopPane desktop_;
    
    /**
     * Maps IPlugin -> JInternalFrame.
     */
    private BidiMap pluginToFrameMap_;
    
    /**
     * Maps JInternalFrame -> IPlugin. 
     */
    private BidiMap frameToPluginMap_;
    
    //--------------------------------------------------------------------------
    // PluginHost Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.host.PluginHost#startup(java.util.Map)
     */
    public void startup(Map props)
    {
        super.startup(props);
        
        desktop_ = new JDesktopPane();
        pluginToFrameMap_ = new DualHashBidiMap();
        frameToPluginMap_ = pluginToFrameMap_.inverseBidiMap();
        
        SwingUtilities.invokeLater(new Runnable() 
        {
            public void run() 
            {
                SwingUtil.cascade(desktop_);
                JInternalFrame[] jifs = desktop_.getAllFrames();
                
                for (int i = 0; i < jifs.length; i++)
                {
                    jifs[i].pack();
                    
                    Dimension d = jifs[i].getSize();
                    
                    if (d.width > desktop_.getWidth())
                        d.width = ((int) (desktop_.getWidth() * 0.9));
                                
                    if (d.height > desktop_.getHeight())
                        d.height = ((int) (desktop_.getHeight() * 0.9));

                    jifs[i].setSize(d);
                }
            }
        });
    }

    
    /**
     * @see toolbox.workspace.host.PluginHost#addPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void addPlugin(IPlugin plugin)
    {
        super.addPlugin(plugin);
    }

    
    /**
     * @see toolbox.workspace.host.PluginHost#removePlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void removePlugin(IPlugin plugin)
    {
        super.removePlugin(plugin);
    }

    
    /**
     * @see toolbox.workspace.host.AbstractPluginHost#importPlugin(
     *      toolbox.workspace.IPlugin)
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
        desktop_.add(frame);
        
        if (frame.getSize().equals(new Dimension(0,0)))
            frame.pack();
        
        frame.setVisible(true);
        frame.moveToFront();
        pluginToFrameMap_.put(plugin, frame);
    }

    
    /**
     * @see toolbox.workspace.host.AbstractPluginHost#exportPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void exportPlugin(IPlugin plugin)
    {
        desktop_.remove((JComponent) pluginToFrameMap_.get(plugin));
        pluginToFrameMap_.remove(plugin);
        
        super.exportPlugin(plugin);
    }    
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#getComponent()
     */
    public JComponent getComponent()
    {
        return desktop_;
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#selectPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void setSelectedPlugin(IPlugin plugin)
    {
        JInternalFrame jif = (JInternalFrame) pluginToFrameMap_.get(plugin);
        jif.moveToFront();
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#getSelectedPlugin()
     */
    public IPlugin getSelectedPlugin()
    {
        return (IPlugin) frameToPluginMap_.get(desktop_.getSelectedFrame());
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#getName()
     */
    public String getName()
    {
        return "Desktop";
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#shutdown()
     */
    public void shutdown()
    {
        desktop_.removeAll();
        desktop_ = null;
        
        pluginToFrameMap_.clear();
        pluginToFrameMap_ = null;
        
        super.shutdown();
    }
}