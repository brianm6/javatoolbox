package toolbox.workspace;

import javax.swing.JComponent;

import toolbox.util.service.Destroyable;
import toolbox.util.service.Initializable;
import toolbox.workspace.prefs.IConfigurator;

/**
 * IPlugin defines the basic interface for a workspace plugin. IPlugins are
 * managed by a concrete instance of a {@link toolbox.workspace.host.PluginHost}
 * and contained within the {@link toolbox.workspace.PluginWorkspace}.
 * <p>
 * @see toolbox.workspace.PluginWorkspace
 * @see toolbox.workspace.host.PluginHost
 */
public interface IPlugin extends Initializable, Destroyable, IPreferenced {

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
     * Returns the user interface component of this plugin.
     * 
     * @return JComponent
     */
    JComponent getView();


    /**
     * Returns a configurator for this plugin's preferences. A configurator is
     * usually a panel that the plugin host can add to an application wide
     * preferences dialog box so that preferences for this plugin can be viewed
     * and edited.
     * 
     * @return ICongifurator
     */
    IConfigurator getConfigurator();
}