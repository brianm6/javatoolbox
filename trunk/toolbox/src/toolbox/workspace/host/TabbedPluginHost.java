package toolbox.workspace.host;

import java.awt.Component;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.apache.commons.collections.bidimap.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.ui.tabbedpane.JSmartTabbedPane;
import toolbox.util.ui.tabbedpane.SmartTabbedPaneListener;
import toolbox.workspace.IPlugin;
import toolbox.workspace.PluginWorkspace;

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
    
    /**
     * Reference to the workspace. Big no no..
     */
    private PluginWorkspace workspace_;
    
    /**
     * Maps the UI component of a plugin to its associated IPlugin. The UI 
     * component is the actual component that is added to the tab panel.
     */
    private BidiMap comp2plugin_;
    
    /**
     * Bidirectional map that maps an IPlugin to its associated UI component.
     */
    private BidiMap plugin2comp_;
    
    //--------------------------------------------------------------------------
    // PluginHost Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.host.PluginHost#startup(java.util.Map)
     */
    public void startup(Map props)
    {
        super.startup(props);
        
        workspace_ = 
            (PluginWorkspace) props.get(PluginWorkspace.PROP_WORKSPACE);
        
        // Bidirectional hash maps
        comp2plugin_ = new DualHashBidiMap();
        plugin2comp_ = comp2plugin_.inverseBidiMap();

        // Create tab panel and add listener so we know when one of the tabs 
        // is closed via the user clicking the X on the tab.
        tabPanel_ = new JSmartTabbedPane(true);
        tabPanel_.addSmartTabbedPaneListener(new TabPanelListener());
        
        SwingUtilities.invokeLater(new Runnable() 
        {
            public void run() 
            {
                if (tabPanel_.getTabCount() > 0)
                    tabPanel_.setSelectedIndex(0);
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
        
        JComponent comp = plugin.getComponent();
        tabPanel_.addTab(plugin.getPluginName(), comp);
        comp2plugin_.put(comp, plugin);
    }    

    
    /**
     * @see toolbox.workspace.host.AbstractPluginHost#exportPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void exportPlugin(IPlugin plugin)
    {
        // make sure component still exists in the tab panel. it could already
        // have been removed by clicking the X on the tab.
        
        Component c = (Component) plugin2comp_.get(plugin);
        int i = tabPanel_.indexOfComponent(c); 
        
        if (i >= 0)
            tabPanel_.remove(i);
        else
            logger_.info("Tab component already removed.");
        
        comp2plugin_.remove(c);
        super.exportPlugin(plugin);
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#shutdown()
     */
    public void shutdown()
    {
        tabPanel_.removeAll();
        tabPanel_ = null;
        
        comp2plugin_.clear();
        comp2plugin_ = null;
        
        super.shutdown();
    }

    
    /**
     * @see toolbox.workspace.host.PluginHost#getComponent()
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
            JComponent component = 
                (JComponent) tabPanel_.getComponentAt(tabIndex);
            
            IPlugin plugin = (IPlugin) comp2plugin_.get(component);
             
            try
            {
                workspace_.deregisterPlugin(plugin.getClass().getName(), false);
            }
            catch (Exception e)
            {
                ExceptionUtil.handleUI(e, logger_);
            }
        }
    }
}