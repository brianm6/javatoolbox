package toolbox.workspace.host;

import java.awt.Component;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.ui.tabbedpane.JSmartTabbedPane;
import toolbox.util.ui.tabbedpane.SmartTabbedPaneListener;
import toolbox.workspace.IPlugin;
import toolbox.workspace.PluginWorkspace;

/**
 * Plugin host that arranges plugins in a JTabbedPane. One plugin per tab.
 * 
 * @see toolbox.workspace.host.DesktopPluginHost
 */
public class TabbedPluginHost extends AbstractPluginHost
{
    private static final Logger logger_ = 
        Logger.getLogger(TabbedPluginHost.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Tab panel that contains one plugin per tab.
     */
    private JSmartTabbedPane tabPanel_;
    
    /**
     * Maps the UI component of a plugin to its associated IPlugin. The UI 
     * component is the actual component that is added to the tab panel.
     */
    private BidiMap uiComponentMap_;
    
    /**
     * Maps an IPlugin to its associated UI component.
     */
    private BidiMap pluginMap_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a TabbedPluginHost.
     */
    public TabbedPluginHost()
    {
        setName("Tabbed Panel");
    }
    
    //--------------------------------------------------------------------------
    // Initializable Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map props)
    {
        super.initialize(props);
        setWorkspace((PluginWorkspace)props.get(PluginWorkspace.KEY_WORKSPACE));
        
        // Bidirectional hash maps
        uiComponentMap_ = new DualHashBidiMap();
        pluginMap_ = uiComponentMap_.inverseBidiMap();

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

    //--------------------------------------------------------------------------
    // PluginHost Interface
    //--------------------------------------------------------------------------

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
        
        JComponent comp = plugin.getView();
        tabPanel_.addTab(plugin.getPluginName(), comp);
        uiComponentMap_.put(comp, plugin);
        setSelectedPlugin(plugin);
    }    

    
    /**
     * @see toolbox.workspace.host.AbstractPluginHost#exportPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void exportPlugin(IPlugin plugin)
    {
        // make sure component still exists in the tab panel. it could already
        // have been removed by clicking the X on the tab.
        
        Component c = (Component) pluginMap_.get(plugin);
        int i = tabPanel_.indexOfComponent(c); 
        
        if (i >= 0)
            tabPanel_.remove(i);
        else
            logger_.info("Tab component already removed.");
        
        uiComponentMap_.remove(c);
        super.exportPlugin(plugin);
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#setSelectedPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void setSelectedPlugin(IPlugin plugin)
    {
        tabPanel_.setSelectedComponent((Component) pluginMap_.get(plugin));
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#getSelectedPlugin()
     */
    public IPlugin getSelectedPlugin()
    {
        return (IPlugin) uiComponentMap_.get(tabPanel_.getSelectedComponent());
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#getView()
     */
    public JComponent getView()
    {
        return tabPanel_;
    }
    
    //--------------------------------------------------------------------------
    // Destroyable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy()
    {
        tabPanel_.removeAll();
        tabPanel_ = null;
        
        uiComponentMap_.clear();
        uiComponentMap_ = null;
        
        super.destroy();
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
            
            IPlugin plugin = (IPlugin) uiComponentMap_.get(component);
             
            try
            {
                getWorkspace().deregisterPlugin(
                    plugin.getClass().getName(), false);
            }
            catch (Exception e)
            {
                ExceptionUtil.handleUI(e, logger_);
            }
        }
    }
}