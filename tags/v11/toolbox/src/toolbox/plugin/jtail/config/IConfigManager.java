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
}