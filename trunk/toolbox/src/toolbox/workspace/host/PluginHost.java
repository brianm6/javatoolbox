package toolbox.workspace.host;

import java.util.Map;

import javax.swing.JComponent;

import toolbox.util.service.Destroyable;
import toolbox.util.service.Initializable;
import toolbox.util.service.Nameable;
import toolbox.workspace.IPlugin;
import toolbox.workspace.IPreferenced;

/**
 * PluginHost is an interface used to manage and host IPlugins with a slant
 * towards presentation/rendering specialization. PluginHost implementors
 * should be interchangeable at runtime so that the presentation framework or
 * strategy can be changed dynamically on the fly.
 * 
 * @see PluginHostManager
 */
public interface PluginHost extends IPreferenced, Nameable, Destroyable, 
    Initializable
{
    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------
    
    public static final String NODE_PLUGINHOST = "PluginHost";
    public static final String ATTR_CLASS = "class";
    public static final String NODE_FRAME = "PluginFrame";
    public static final String ATTR_PLUGIN_NAME = "name";

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * The actual GUI component that will host the plugins as child components.
     * 
     * @return JComponent
     */
    JComponent getComponent();

    
    /**
     * Adds a plugin to be hosted by an PluginHost implementor.
     * 
     * @param plugin Plugin to add.
     */
    void addPlugin(IPlugin plugin);
    
    
    /**
     * Removes a plugin from PluginHost implementor.
     * 
     * @param plugin Plugin to remove.
     */
    void removePlugin(IPlugin plugin);
    
    
    /**
     * Imports an existing plugin from another host.
     * 
     * @param plugin Plugin to import.
     */
    void importPlugin(IPlugin plugin);
    
    
    /**
     * Exports an existing plugin so that the plugin can be transferred to
     * another PluginHost via importPlugin()
     * 
     * @param plugin Plugin to export.
     */
    void exportPlugin(IPlugin plugin);
    
    
    /**
     * Checks if a given plugin is already being hosted.
     * 
     * @param pluginClass FQCN of the plugin to check.
     * @return True if the plugin exists, false otherwise.
     */
    boolean hasPlugin(String pluginClass);
    
    
    /**
     * Retrieves a plugin given its FQCN
     * 
     * @param pluginClass FCQN of the plugin to retrieve.
     * @return Plugin if found, null otherwise.
     */
    IPlugin getPlugin(String pluginClass);
    
    
    /**
     * Retrieves all of the plugins.
     * 
     * @return An iterator which can be used to traverse over the plugins.
     */
    IPlugin[] getPlugins();
    
    
    /**
     * Selects the given plugin. The interpretation of "select" is left to the
     * PluginHost implementor.
     * 
     * @param plugin Plugin to select.
     */
    void setSelectedPlugin(IPlugin plugin);
    
    
    /**
     * Returns the currently selected plugin.
     * 
     * @return IPlugin
     */
    IPlugin getSelectedPlugin();
    
    
    /**
     * Necessary for transfering the contents of one host to another.
     * 
     * @return Map containing the startup parameters.
     */
    Map getStartupConfig();
    
    //--------------------------------------------------------------------------
    // Event Notification
    //--------------------------------------------------------------------------
    
    /**
     * Adds a PluginHostListener.
     *
     * @param listener Listener to add.
     */
    public void addPluginHostListener(PluginHostListener listener);
    

    /**
     * Removes a PluginHostListener.
     *
     * @param listener Listener to remove.
     */
    public void removePluginHostListener(PluginHostListener listener);

    
    /**
     * Retrieves a list of the  PluginHostListeners.
     *
     * @return PluginHostListener[]
     */
    public PluginHostListener[] getPluginHostListeners();
}