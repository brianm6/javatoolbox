package toolbox.workspace;

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.collections.ObjectComparator;
import toolbox.util.ui.JSmartCheckBoxMenuItem;
import toolbox.util.ui.JSmartMenu;
import toolbox.workspace.host.PluginHost;
import toolbox.workspace.host.PluginHostListener;

/**
 * PluginMenu is an extension of JSmartMenu that houses the currently known
 * list of plugins. By selecting items on the menu, a plugin can be launched.
 * By selecting the item of an already active plugin on the menu, the plugin
 * is selected according to the implementation of the currently active
 * PluginHostManager.
 */
public class PluginMenu extends JSmartMenu
{
    private static final Logger logger_ = Logger.getLogger(PluginMenu.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
   
    /**
     * Maps a plugin's name to its corresponding MenuItem.
     */
    private Map nameMap_;
    
    /**
     * Reference to the parent workspace.
     */
    private PluginWorkspace workspace_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a PluginMenu.
     * 
     * @param workspace Reference to the parent workspace.
     */
    public PluginMenu(PluginWorkspace workspace)
    {
        super("Plugins");
        nameMap_ = new HashMap();
        workspace_ = workspace;
        buildView();
    }

    //--------------------------------------------------------------------------
    // Builds UI
    //--------------------------------------------------------------------------
    
    /**
     * Creates the Plugin menu.
     */
    protected void buildView()
    {
        setMnemonic('l');

        //
        // Get the list of plugins and sort it by plugin name
        //
        
        List plugins = PluginDialog.getPluginList();
        Collections.sort(plugins, new ObjectComparator("name"));
        
        //
        // Iterate over plugin list and create the menu
        //
        
        for (Iterator i = plugins.iterator(); i.hasNext();)
        {
            PluginMeta meta = (PluginMeta) i.next();

            JSmartCheckBoxMenuItem mi = 
                new JSmartCheckBoxMenuItem(
                    new LaunchPluginAction(meta.getName()));            

            mi.putClientProperty("pluginmeta", meta);
            mi.setToolTipText(meta.getPlugin().getDescription());
            nameMap_.put(meta.getName(), mi);
            add(mi);
        }

        //
        // As plugins are loaded/unloaded, the menu has to be updated to reflect
        // the currently loaded state.
        //
        workspace_.
            getPluginHost().
            addPluginHostListener(
                new PluginActivityListener());
    }
    
    //--------------------------------------------------------------------------
    // LaunchPluginAction
    //--------------------------------------------------------------------------
    
    /**
     * LaunchPluginAction is responsible for launching the currently selected
     * plugin and loading it into the workspace.
     */
    class LaunchPluginAction extends AbstractAction 
    {
        /**
         * Creates a LaunchPluginAction.
         * 
         * @param name Name of the action.
         */
        LaunchPluginAction(String name)
        {
            super(name);
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) e.getSource();
            PluginMeta meta = (PluginMeta) cbmi.getClientProperty("pluginmeta");

            if (cbmi.isSelected())
            {
                //
                // Load the plugin from scratch
                //
                
                try
                {
                    workspace_.registerPlugin(meta.getClassName());
                }
                catch (Exception ex)
                {
                    ExceptionUtil.handleUI(ex, logger_);
                }
            }
            else
            {
                //
                // Select the plugin if it is already loaded
                //
                
                IPlugin plugin = 
                    workspace_.getPluginByClass(meta.getClassName());
                
                workspace_.getPluginHost().setSelectedPlugin(plugin);
            }

            cbmi.setSelected(true);
        }
    }

    //--------------------------------------------------------------------------
    // PluginActivityListener
    //--------------------------------------------------------------------------
    
    /**
     * PluginActivityListener is responsible for keeping the state of the
     * plugins in the menu in sync with their loaded state. A check appears 
     * next to loaded plugins and is removed when/if the plugin is unloaded.
     */
    class PluginActivityListener implements PluginHostListener
    {
        /**
         * @see toolbox.workspace.host.PluginHostListener#pluginAdded(
         *      toolbox.workspace.host.PluginHost,
         *      toolbox.workspace.IPlugin)
         */
        public void pluginAdded(PluginHost pluginHost, IPlugin plugin)
        {
            JSmartCheckBoxMenuItem mi = (JSmartCheckBoxMenuItem) 
                nameMap_.get(plugin.getPluginName());

            mi.setSelected(true);
        }


        /**
         * @see toolbox.workspace.host.PluginHostListener#pluginRemoved(
         *      toolbox.workspace.host.PluginHost,
         *      toolbox.workspace.IPlugin)
         */
        public void pluginRemoved(PluginHost pluginHost, IPlugin plugin)
        {
            JCheckBoxMenuItem mi = (JCheckBoxMenuItem)
                nameMap_.get(plugin.getPluginName());

            mi.setSelected(false);
        }
    }
}