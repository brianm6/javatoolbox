package toolbox.jtail.config;

import toolbox.util.ui.plugin.IPreferenced;

/**
 * Interface used for the persistence of the JTail application's preferences.
 */
public interface IJTailConfig extends IPreferenced
{
    /**
     * Returns the default tail pane configuration
     * 
     * @return  Default configuration
     */
    public ITailPaneConfig getDefaultConfig();

    /**
     * Sets the default tail pane configuration
     * 
     * @param config  Default tail pane configuration
     */
    public void setDefaultConfig(ITailPaneConfig config);

    /**
     * Returns the tailPaneConfigs.
     * 
     * @return  Array of tail pain configurations
     */
    public ITailPaneConfig[] getTailConfigs();

    /**
     * Sets the list of tail pane configurations
     * 
     * @param tailPaneConfigs  Tail pane configurations
     */
    public void setTailConfigs(ITailPaneConfig[] tailPaneConfigs);
}
