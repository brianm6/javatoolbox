package toolbox.workspace.host;

import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import toolbox.util.ui.tabbedpane.JSmartTabbedPane;
import toolbox.util.ui.tabbedpane.SmartTabbedPaneListener;
import toolbox.workspace.IPlugin;

/**
 * Plugin host that arranges plugins in a tab panel. One plugin per tab.
 */
public class TabbedPluginHost extends AbstractPluginHost
{
    private static final Logger logger_ = 
        Logger.getLogger(TabbedPluginHost.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * One plugin per tab.
     */
    private JSmartTabbedPane tabPanel_;
    
    //--------------------------------------------------------------------------
    // PluginHost Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.PluginHost#startup(java.util.Map)
     */
    public void startup(Map props)
    {
        super.startup(props);
        tabPanel_ = new JSmartTabbedPane(true);
        tabPanel_.addSmartTabbedPaneListener(new TabPanelListener());
        
        SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                if (tabPanel_.getTabCount() > 0)
                    tabPanel_.setSelectedIndex(0);
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
        //      if (removeTab)

        super.removePlugin(plugin);
    }
    
    
    /**
     * @see toolbox.workspace.host.AbstractPluginHost#importPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void importPlugin(IPlugin plugin)
    {
        super.importPlugin(plugin);
        tabPanel_.addTab(plugin.getPluginName(), plugin.getComponent());
    }    

    
    /**
     * @see toolbox.workspace.host.AbstractPluginHost#exportPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void exportPlugin(IPlugin plugin)
    {
        tabPanel_.remove(tabPanel_.indexOfTab(plugin.getPluginName()));        
        super.exportPlugin(plugin);
    }
    
    
    /**
     * @see toolbox.workspace.PluginHost#shutdown()
     */
    public void shutdown()
    {
        tabPanel_.removeAll();
        tabPanel_ = null;
        super.shutdown();
    }

    
    /**
     * @see toolbox.workspace.PluginHost#getComponent()
     */
    public JComponent getComponent()
    {
        return tabPanel_;
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#getName()
     */
    public String getName()
    {
        return "Tabbed Panel";
    }
    
    //--------------------------------------------------------------------------
    // TabPanelListener
    //--------------------------------------------------------------------------
    
    /**
     * Listens for tab closing events, and deregisters the plugin for that 
     * given tab.
     */
    class TabPanelListener implements SmartTabbedPaneListener
    {
        /**
         * @see toolbox.util.ui.tabbedpane.SmartTabbedPaneListener#tabClosing(
         *      toolbox.util.ui.tabbedpane.JSmartTabbedPane, int)
         */
        public void tabClosing(JSmartTabbedPane tabbedPane, int tabIndex)
        {
	
            // TODO: Fix tab closing reference cleanup.

            //            String name = tabbedPane.getTitleAt(tabIndex);
            //            //IPlugin plugin = (IPlugin) plugins_.get(name);
            //            IPlugin plugin = (IPlugin) pluginHost_.getPlugin(name);
            //            String clazz = plugin.getClass().getName(); 
            //            
            //            try
            //            {
            //                deregisterPlugin(clazz, false);
            //            }
            //            catch (Exception e)
            //            {
            //                ExceptionUtil.handleUI(e, logger_);
            //            }
        }
    }
}