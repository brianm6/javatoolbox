package toolbox.workspace;

import java.util.Map;

import javax.swing.JComponent;

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
public interface IPlugin extends IPreferenced
{
    /**
     * Initializes the plugin.
     * 
     * @param props Initialization properties and parameters
     */
    public void startup(Map props);

    
    /**
     * Friendly name of the plugin used for identification in workspace.
     * 
     * @return Name of the plugin
     */
    public String getPluginName();

    
    /**
     * Returns a short description of the plugin.
     * 
     * @return Short description of the plugin
     */
    public String getDescription();

    
    /**
     * Component that represents the graphical view of the plugin.
     * 
     * @return GUI component of the plugin
     */
    public JComponent getComponent();
    
    
    /**
     * Cleans up resources before the plugin is discarded.
     */
    public void shutdown();
}
