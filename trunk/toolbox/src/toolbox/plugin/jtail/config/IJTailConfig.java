package toolbox.jtail.config;

import toolbox.workspace.IPreferenced;

/**
 * Interface used for the persistence of the JTail application's preferences.
 */
public interface IJTailConfig extends IPreferenced
{
    /**
     * Returns the default tail pane configuration.
     * 
     * @return Default configuration
     */
    ITailPaneConfig getDefaultConfig();

    
    /**
     * Sets the default tail pane configuration.
     * 
     * @param config Default tail pane configuration
     */
    void setDefaultConfig(ITailPaneConfig config);

    
    /**
     * Returns the tailPaneConfigs.
     * 
     * @return Array of tail pain configurations
     */
    ITailPaneConfig[] getTailConfigs();

    
    /**
     * Sets the list of tail pane configurations.
     * 
     * @param tailPaneConfigs Tail pane configurations
     */
    void setTailConfigs(ITailPaneConfig[] tailPaneConfigs);
}