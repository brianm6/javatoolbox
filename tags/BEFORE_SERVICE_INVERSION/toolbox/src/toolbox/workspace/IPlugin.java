package toolbox.workspace;

import javax.swing.JComponent;

import toolbox.util.service.Destroyable;
import toolbox.util.service.Initializable;

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
 * @see toolbox.workspace.PluginWorkspace
 */
public interface IPlugin extends Initializable, Destroyable, IPreferenced
{
    /**
     * Friendly name of the plugin used for identification in workspace.
     * 
     * @return Name of the plugin.
     */
    String getPluginName();

    
    /**
     * Returns a short description of the plugin.
     * 
     * @return Short description of the plugin.
     */
    String getDescription();

    
    /**
     * Component that represents the graphical view of the plugin.
     * 
     * @return GUI component of the plugin.
     */
    JComponent getComponent();
}