package toolbox.plugin.jtail.config;

import toolbox.workspace.IPreferenced;

/**
 * Interface used for the persistence of the JTail application's preferences.
 */
public interface IJTailConfig extends IPreferenced
{
    /**
     * Returns the default tail view configuration.
     * 
     * @return ITailViewConfig
     */
    ITailViewConfig getDefaultConfig();

    
    /**
     * Sets the default tail view configuration.
     * 
     * @param config Default tail view configuration.
     */
    void setDefaultConfig(ITailViewConfig config);

    
    /**
     * Returns the array of tail view configurations.
     * 
     * @return ITailViewConfig[]
     */
    ITailViewConfig[] getTailConfigs();

    
    /**
     * Sets the list of tail view configurations.
     * 
     * @param tailViewConfigs Tail view configurations.
     */
    void setTailConfigs(ITailViewConfig[] tailViewConfigs);
}