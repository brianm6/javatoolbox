package toolbox.workspace.host;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ClassUtil;
import toolbox.workspace.IPlugin;
import toolbox.workspace.IPreferenced;

/**
 * Abstract implementation of an PluginHost that captures behavior common to 
 * all PluginHost concrete implementations. 
 */
public abstract class AbstractPluginHost implements PluginHost, IPreferenced
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

    /**
     * List of listeners.
     */
    private PluginHostListener[] pluginHostListeners_;
    
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
        logger_.debug("Starting up " + ClassUtil.stripPackage(getClass()));
        
        // Make sure map is ordered by insertion.
        plugins_ = new LinkedHashMap();
        init_ = props;
        pluginHostListeners_ = new PluginHostListener[0];
    }

    
    /**
     * Adds the plugin to the registry and calls startup()
     * 
     * @see toolbox.workspace.host.PluginHost#addPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void addPlugin(IPlugin plugin)
    {
        logger_.debug("Adding plugin " + 
            ClassUtil.stripPackage(plugin.getClass()));
        
        plugin.startup(init_);
        importPlugin(plugin);
        
        firePluginAdded(plugin);
    }

    
    /**
     * Removes the plugin from the registry and called shutdown()
     * 
     * @see toolbox.workspace.host.PluginHost#removePlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void removePlugin(IPlugin plugin)
    {
        logger_.debug("Removing plugin " + 
            ClassUtil.stripPackage(plugin.getClass()));
        
        exportPlugin(plugin);
        plugin.shutdown();
        
        firePluginRemoved(plugin);
    }

    
    /**
     * @see toolbox.workspace.host.PluginHost#importPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void importPlugin(IPlugin plugin)
    {
        logger_.debug("Importing plugin " + 
            ClassUtil.stripPackage(plugin.getClass()));
        
        plugins_.put(plugin.getClass().getName(), plugin);
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#exportPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void exportPlugin(IPlugin plugin)
    {
        logger_.debug("Exporting plugin " + 
            ClassUtil.stripPackage(plugin.getClass().getName()));
        
        plugins_.remove(plugin.getClass().getName());        
    }    

    
    /**
     * Retrieves the plugin from the registry.
     * 
     * @see toolbox.workspace.host.PluginHost#getPlugin(java.lang.String)
     */
    public IPlugin getPlugin(String pluginClass)
    {
        for (Iterator i = plugins_.values().iterator(); i.hasNext();)
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
        for (Iterator i = plugins_.values().iterator(); i.hasNext();)
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
        logger_.debug("Shutting down " + ClassUtil.stripPackage(getClass()));
        
        plugins_.clear();
        plugins_ = null;
    }
    
    //--------------------------------------------------------------------------
    // Event Notification
    //--------------------------------------------------------------------------
    
    /**
     * Adds a PluginHostListener.
     *
     * @param listener Listener to add.
     */
    public void addPluginHostListener(PluginHostListener listener)
    {
        pluginHostListeners_ = 
            (PluginHostListener[]) 
                ArrayUtil.add(pluginHostListeners_, listener);
    }


    /**
     * Removes a PluginHostListener.
     *
     * @param listener Listener to remove.
     */
    public void removePluginHostListener(PluginHostListener listener)
    {
        pluginHostListeners_ = 
            (PluginHostListener[]) 
                ArrayUtil.remove(pluginHostListeners_, listener);
    }


    /**
     * Fires an event when a plugin is added.
     * 
     * @param plugin Added plugin.
     */
    protected void firePluginAdded(IPlugin plugin)
    {
        for (int i = 0; i < pluginHostListeners_.length; 
            pluginHostListeners_[i++].pluginAdded(this, plugin));
    }


    /**
     * Fires an event when a plugin is removed.
     * 
     * @param plugin Removed plugin.
     */
    protected void firePluginRemoved(IPlugin plugin)
    {
        for (int i = 0; i < pluginHostListeners_.length;
            pluginHostListeners_[i++].pluginRemoved(this, plugin));
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#getPluginHostListeners()
     */
    public PluginHostListener[] getPluginHostListeners()
    {
        return pluginHostListeners_;
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
    }
    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
    }
}