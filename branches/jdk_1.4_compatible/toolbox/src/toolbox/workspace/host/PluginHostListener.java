package toolbox.workspace.host;

import toolbox.workspace.IPlugin;

/**
 * Defines the interface for notification when a plugin is added or removed
 * from a PluginHost.
 *
 * @see toolbox.workspace.host.PluginHost
 * @see toolbox.workspace.IPlugin
 */
public interface PluginHostListener
{
    /**
     * Notification that a plugin was added to the plugin host.
     *
     * @param pluginHost Plugin host that the plugin was added to.
     * @param plugin Plugin that was added.
     */
    void pluginAdded(PluginHost pluginHost, IPlugin plugin);


    /**
     * Notification that a plugin was removed from a plugin host.
     *
     * @param pluginHost Plugin host that the plugin was removed from.
     * @param plugin Plugin that was removed.
     */
    void pluginRemoved(PluginHost pluginHost, IPlugin plugin);
}