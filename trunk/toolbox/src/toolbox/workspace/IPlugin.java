package toolbox.workspace;

import javax.swing.JComponent;

import toolbox.util.service.Destroyable;
import toolbox.util.service.Initializable;
import toolbox.workspace.prefs.IConfigurator;

/**
 * IPlugin defines the interface for any plugins that can hosted by the 
 * PluginWorkspace.
 * <p>
 * Plugin writing guidelines:
 * <ul>
 *   <li>Make sure your plugins default constructor does absolutely nothing; 
 *       Instead, move that functionality to the init() method. This is 
 *       necessary so that plugins can be found and identified via 
 *       Class.forname().newInstance() but not necessary loaded into the 
 *       host environment.
 * </ul>
 * 
 * @see toolbox.workspace.PluginWorkspace
 */
public interface IPlugin extends Initializable, Destroyable, IPreferenced
{
    /**
     * Friendly name of the plugin used for identification in the workspace.
     * 
     * @return String
     */
    String getPluginName();

    
    /**
     * Returns a short description of this plugin.
     * 
     * @return String
     */
    String getDescription();

    
    /**
     * User interface component of this plugin.
     * 
     * @return JComponent
     */
    JComponent getView();
    
    
    /**
     * Returns the configurator for this plugin preferences.  
     */
    IConfigurator getConfigurator();
}