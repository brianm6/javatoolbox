package toolbox.plugin.jtail.config.xom;

import nu.xom.Element;
import nu.xom.Elements;

import toolbox.plugin.jtail.config.IJTailConfig;
import toolbox.plugin.jtail.config.ITailViewConfig;
import toolbox.util.ArrayUtil;
import toolbox.util.XOMUtil;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.PreferencedException;

/**
 * XOM based implemenation of the IJTailConfig interface that marshals 
 * configuration information to and from XML.
 */
public class JTailConfig implements IJTailConfig, XMLConstants, IPreferenced
{ 
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Tail view configurations.
     */
    private ITailViewConfig[] tailViewConfigs_;
    
    /**
     * Default tail view configuration.
     */
    private ITailViewConfig defaultConfig_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    public JTailConfig()
    {
        defaultConfig_ = new TailViewConfig();
        tailViewConfigs_ = new TailViewConfig[0];
    }

    //--------------------------------------------------------------------------
    //  IJTailConfig Interface
    //--------------------------------------------------------------------------

    /*
     * @see toolbox.plugin.jtail.config.IJTailConfig#setDefaultConfig(toolbox.plugin.jtail.config.ITailViewConfig)
     */
    public void setDefaultConfig(ITailViewConfig defaultConfig)
    {
        defaultConfig_ = defaultConfig;
    }

    
    /*
     * @see toolbox.plugin.jtail.config.IJTailConfig#getDefaultConfig()
     */
    public ITailViewConfig getDefaultConfig()
    {
        return defaultConfig_;
    }

    
    /*
     * @see toolbox.plugin.jtail.config.IJTailConfig#getTailConfigs()
     */
    public ITailViewConfig[] getTailConfigs()
    {
        return tailViewConfigs_;
    }

    
    /*
     * @see toolbox.plugin.jtail.config.IJTailConfig#setTailConfigs(toolbox.plugin.jtail.config.ITailViewConfig[])
     */
    public void setTailConfigs(ITailViewConfig[] tailViewConfigs)
    {
        tailViewConfigs_ = tailViewConfigs;
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws PreferencedException
    {
        Element root = prefs.getFirstChildElement(NODE_JTAIL);
        
        if (root != null)
        {
            // Defaults
            Element defaultsNode = root.getFirstChildElement(NODE_DEFAULTS);
            TailViewConfig defaultTail = new TailViewConfig();
            defaultTail.applyPrefs(defaultsNode);
            setDefaultConfig(defaultTail);
    
            // List of tails                        
            ITailViewConfig[] tailViewConfigs = new ITailViewConfig[0];
            Elements tails = root.getChildElements(NODE_TAIL);
            
            for (int i = 0; i < tails.size(); i++)
            {
                TailViewConfig tvc = new TailViewConfig();
                Element tail = tails.get(i);
                tvc.applyPrefs(tail);
                
                tailViewConfigs = (ITailViewConfig[])
                    ArrayUtil.add(tailViewConfigs, tvc);
            }
    
            setTailConfigs(tailViewConfigs);
        }
    }
    
    
    /*
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
        Element root = new Element(NODE_JTAIL);

        Element defaultsNode = new Element(NODE_DEFAULTS);
        root.appendChild(defaultsNode);
        defaultConfig_.savePrefs(defaultsNode);

        for (int i = 0; i < tailViewConfigs_.length; i++)
        { 
            Element tail = new Element(NODE_TAIL);
            tailViewConfigs_[i].savePrefs(tail);
            root.appendChild(tail);
        }
        
        XOMUtil.insertOrReplace(prefs, root);
    }
}