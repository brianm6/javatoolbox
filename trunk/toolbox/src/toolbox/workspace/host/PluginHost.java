package toolbox.workspace.host;

import java.util.Map;

import javax.swing.JComponent;

import toolbox.workspace.IPlugin;

/**
 * PluginHost is an interface used to manage and host IPlugins with a slant
 * towards presentation/rendering specialization. PluginHost implementors 
 * should be interchangeable at runtime so that the presentation framework or
 * strategy can be changed dynamically on the fly.
 * 
 * @see PluginHostManager
 */
public interface PluginHost
{
    /**
     * The actual GUI component that will host the plugins as child components.
     * 
     * @return JComponent
     */
    JComponent getComponent();
    
    
    /**
     * Returns a UI friendly name of the plugin host.
     * 
     * @return String
     */
    String getName();
    
    
    /**
     * Initializes the plugin host.
     * 
     * @param props Map of initialization properties 
     */
    void startup(Map props);
    
    
    /**
     * Adds a plugin to be hosted by an PluginHost implementor.
     * 
     * @param plugin Plugin to add
     */
    void addPlugin(IPlugin plugin);
    
    
    /**
     * Removes a plugin from PluginHost implementor.
     * 
     * @param plugin Plugin to remove
     */
    void removePlugin(IPlugin plugin);
    
    
    /**
     * Imports an existing plugin from another host.
     * 
     * @param plugin Plugin to import
     */
    void importPlugin(IPlugin plugin);
    
    
    /**
     * Exports an existing plugin so that the plugin can be transferred to
     * another PluginHost via importPlugin()
     * 
     * @param plugin Plugin to export
     */
    void exportPlugin(IPlugin plugin);
    
    
    /**
     * Checks if a given plugin is already being hosted.
     * 
     * @param pluginClass FQCN of the plugin to check
     * @return True if the plugin exists, false otherwise
     */
    boolean hasPlugin(String pluginClass);
    
    
    /**
     * Retrieves a plugin given its FQCN
     * 
     * @param pluginClass FCQN of the plugin to retrieve
     * @return Plugin if found, null otherwise
     */
    IPlugin getPlugin(String pluginClass);
    
    
    /**
     * Retrieves all of the plugins.
     * 
     * @return An iterator which can be used to traverse over the plugins.
     */
    IPlugin[] getPlugins();
    
    
    /**
     * Necessary for transfering the contents of one host to another
     * 
     * @return Map containing the startup parameters
     */
    Map getStartupConfig();
    
    
    /**
     * Shuts the plugin host down. A plugin host should be reusable by
     * simply calling startup() after shutdown().
     */
    void shutdown();
}
