package toolbox.workspace.host;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.SequencedHashMap;
import org.apache.log4j.Logger;

import toolbox.workspace.IPlugin;

/**
 * Abstract implementation of an PluginHost that captures behavior common to 
 * all PluginHost concrete implementations. 
 */
public abstract class AbstractPluginHost implements PluginHost
{
    private static final Logger logger_ = 
        Logger.getLogger(AbstractPluginHost.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Map of initialization parameters passed to each plugin on startup.
     */
    private Map init_;
    
    /**
     * Maps a plugin's FQCN -> IPlugin instance.
     */
    private Map plugins_;

    //--------------------------------------------------------------------------
    // PluginHost Interface
    //--------------------------------------------------------------------------
    
    /**
     * Saves a copy of the initialization props that will be passed to each
     * plugin on startup().
     * 
     * @see toolbox.workspace.host.PluginHost#startup(java.util.Map)
     */
    public void startup(Map props)
    {
        logger_.debug("startup");
        
        plugins_ = new SequencedHashMap();
        init_ = props;
    }

    
    /**
     * Adds the plugin to the registry and calls startup()
     * 
     * @see toolbox.workspace.host.PluginHost#addPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void addPlugin(IPlugin plugin)
    {
        logger_.debug("addPlugin: " + plugin.getClass().getName());
        
        plugin.startup(init_);
        importPlugin(plugin);
    }

    
    /**
     * Removes the plugin from the registry and called shutdown()
     * 
     * @see toolbox.workspace.host.PluginHost#removePlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void removePlugin(IPlugin plugin)
    {
        logger_.debug("removePlugin: " + plugin.getClass().getName());
        
        exportPlugin(plugin);
        plugin.shutdown();
    }

    
    /**
     * @see toolbox.workspace.host.PluginHost#importPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void importPlugin(IPlugin plugin)
    {
        logger_.debug("importPlugin: " + plugin.getClass().getName());
        
        plugins_.put(plugin.getClass().getName(), plugin);
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#exportPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void exportPlugin(IPlugin plugin)
    {
        logger_.debug("exportPlugin: " + plugin.getClass().getName());
        
        plugins_.remove(plugin.getClass().getName());        
    }    

    
    /**
     * Retrieves the plugin from the registry.
     * 
     * @see toolbox.workspace.host.PluginHost#getPlugin(java.lang.String)
     */
    public IPlugin getPlugin(String pluginClass)
    {
        for (Iterator i = plugins_.values().iterator(); i.hasNext(); )
        {
            IPlugin plugin = (IPlugin) i.next();
            if (plugin.getClass().getName().equals(pluginClass))
                return plugin;
        }        
        
        return null;
    }

    
    /**
     * Retrieves the plugins. 
     * 
     * @see toolbox.workspace.host.PluginHost#getPlugins()
     */
    public IPlugin[] getPlugins()
    {
        return (IPlugin[]) plugins_.values().toArray(new IPlugin[0]);
    }

    
    /**
     * Checks to see of the plugin exists in the registry.
     * 
     * @see toolbox.workspace.host.PluginHost#hasPlugin(java.lang.String)
     */
    public boolean hasPlugin(String pluginClass)
    {
        for (Iterator i = plugins_.values().iterator(); i.hasNext(); )
            if (i.next().getClass().getName().equals(pluginClass))
                return true;
        
        return false;                
    }

    
    /**
     * @see toolbox.workspace.host.PluginHost#getStartupConfig()
     */
    public Map getStartupConfig()
    {
        return init_;
    }
    
    
    /**
     * Nulls out the plugin registry.
     * 
     * @see toolbox.workspace.host.PluginHost#shutdown()
     */
    public void shutdown()
    {
        logger_.debug("shutdown");
        
        plugins_.clear();
        plugins_ = null;
    }
}