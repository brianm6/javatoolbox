package toolbox.workspace;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;

/**
 * PluginMeta contains meta-information about IPlugin instances. This includes
 * classname, display name, and plugin instance. Used primarily by 
 * {@link toolbox.workspace.PluginMenu} and 
 * {@link toolbox.workspace.PluginDialog} to manage plugin information at
 * runtime.
 */
public class PluginMeta
{
    private static final Logger logger_ = Logger.getLogger(PluginMeta.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Fully qualified class name of the plugin.
     */
    private String className_;
    
    /**
     * Reference to an IPlugin instance.
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
     * @param plugin IPlugin instance.
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
     * Returns the display friendly plugin name.
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
                    plugin_ = (IPlugin) Class.forName(className_).newInstance();    
                else
                    throw new IllegalArgumentException(
                        "Classname not provided");
            }
        }
        catch (Exception e)
        {
            logger_.debug("Classname = " + className_);
            ExceptionUtil.handleUI(e, logger_);    
        }
        
        return plugin_;
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Returns the plugins display name.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getName();
    }
}