package toolbox.workspace;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;

/**
 * PluginMeta info used to populate the active/inactive lists.
 */
public class PluginMeta
{
    private static final Logger logger_ = Logger.getLogger(PluginMeta.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Name of the plugin class.
     */
    private String className_;
    
    /**
     * Reference to the IPlugin instance.
     */
    private IPlugin plugin_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a PluginMeta.
     * 
     * @param pluginClass Plugin class name.
     */
    public PluginMeta(String pluginClass)
    {
        className_ = pluginClass;
    }
    
    
    /**
     * Creates a PluginMeta.
     * 
     * @param plugin Plugin class.
     */
    public PluginMeta(IPlugin plugin)
    {
        plugin_ = plugin;
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the plugin class name.
     * 
     * @return String
     */
    public String getClassName()
    {
        if (className_ == null && plugin_ != null)
            className_ = plugin_.getClass().getName();
            
        return className_;
    }
    
    
    /**
     * Returns the plugin friendly name.
     * 
     * @return String
     */
    public String getName()
    {
        return getPlugin().getPluginName();    
    }
    
    
    /**
     * Returns the plugin instance.
     * 
     * @return IPlugin.
     */
    public IPlugin getPlugin()
    {
        try
        {
            if (plugin_ == null)
            {
                if (className_ != null)
                {
                    plugin_ = (IPlugin) 
                        Class.forName(className_).newInstance();    
                }
                else
                    throw new IllegalArgumentException(
                        "Classname not provided"); 
            }
        }
        catch (Exception e)
        {
            ExceptionUtil.handleUI(e, logger_);    
        }
        
        return plugin_;
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getName();
    }
}