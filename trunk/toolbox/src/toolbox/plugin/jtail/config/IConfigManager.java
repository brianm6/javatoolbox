package toolbox.jtail.config;
 
/**
 * Configuration manager interface to load and save application specific
 * properties from one instance to another.
 */
public interface IConfigManager
{
    /**
     * Saves the configuration
     *
     * @param  jtailConfig  Configuration to save
     */
    public void save(IJTailConfig jtailConfig);
 
    
    /**
     * Loads the configuration
     * 
     * @return  IJTailConfig
     */
    public IJTailConfig load();
    
    
    /**
     * Creates concrete implemenation of an ITailPaneConfig. Used when a new
     * tail is created and that tails config settings need to be sent to the 
     * ConfigManager for saving
     * 
     * @return  Tail pane configuration
     */
    public ITailPaneConfig createTailPaneConfig();
}