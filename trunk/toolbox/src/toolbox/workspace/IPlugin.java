package toolbox.util.ui.plugin;

import java.awt.Component;
import java.util.Properties;

import javax.swing.JMenuBar;

/**
 * IPlugin defines the interface for any plugins that can hosted by the 
 * PluginWorkspace.
 * <p>
 * Plugin writing guidelines:
 * <ul>
 * <li>Make sure your plugins default constructor does absolutely nothing; 
 *     Instead, move that functionality to the init() method. This is 
 *     necessary so that plugins can be found and identified via 
 *     Class.forname().newInstance() but not necessary loaded into the 
 *     host environment.
 * </ul>
 * 
 * @see PluginWorkspace
 */
public interface IPlugin
{
    /*
     * TODO: Add getDescription()
     * TODO: Remove getMenuBar()
     */
    
    /**
     * Friendly name of the plugin used for identification in workspace.
     * 
     * @return Name of the plugin (tab text)
     */
    public String getName();
    
    /**
     * Component that represents the graphical view of the plugin
     * 
     * @return GUI component of the plugin
     */
    public Component getComponent();
    
    /**
     * Optional menu bar associated with the plugin on the workspace
     *
     * @return  Plugin specific menu bar
     */
    public JMenuBar getMenuBar();
    
    /**
     * Initializes the plugin
     */
    public void init();
    
    /**
     * Provides an opportunity from the plugin to save its persistent
     * preferences in a set of Properties hosted by the plugin host.
     * 
     * @param  prefs  Properties object to save plugin specific preferences to.
     */
    public void savePrefs(Properties prefs);
    
    /**
     * The plugin can read saved persistent properties to initialize itself
     * once the plugin host is materialized.
     * 
     * @param  prefs  Properties object from which to read plugin specific
     *                properties.
     */
    public void applyPrefs(Properties prefs);
    
    /**
     * Sets the status bar. The plugin can use the passed object as a
     * callback into the hosts environment for setting/getting status messages
     * 
     * @param  statusBar  Interface exposing methods to set status text
     */
    public void setStatusBar(IStatusBar statusBar);
    
    /**
     * Cleans up resources before the plugin is shutdown
     */
    public void shutdown();
}
