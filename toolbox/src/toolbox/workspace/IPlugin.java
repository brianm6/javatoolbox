package toolbox.util.ui.plugin;

import java.awt.Component;
import java.util.Properties;

import javax.swing.JMenu;

/**
 * GUI component plugin interface for the PluginWorkspace
 * 
 * @see PluginWorkspace
 */
public interface IPlugin
{
    
    /**
     * Name of the plugin
     * 
     * @return Name of the plugin (tab text)
     */
    public String getName();

    
    /**
     * GUI component of the plugin
     * 
     * @return GUI component of the plugin
     */
    public Component getComponent();

    
    /**
     * Optional menu items
     * 
     * @return Plugin specific menu that is added to the application menubar
     */
    public JMenu getMenu();

    
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
