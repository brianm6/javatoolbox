package toolbox.jtail.config.xom;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;

import toolbox.jtail.config.IJTailConfig;
import toolbox.jtail.config.ITailPaneConfig;
import toolbox.util.ArrayUtil;
import toolbox.util.XOMUtil;
import toolbox.workspace.IPreferenced;

/**
 * Tiny XML implemenation of IJTailConfig interface that marshals configuration 
 * information to and from XML.
 */
public class JTailConfig implements IJTailConfig, XMLConstants, IPreferenced
{ 
    private static final Logger logger_ = 
        Logger.getLogger(JTailConfig.class);
    
    private ITailPaneConfig[] tailPaneConfigs_;
    private ITailPaneConfig defaultConfig_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JTailConfig.
     */
    public JTailConfig()
    {
        defaultConfig_ = new TailPaneConfig();
        tailPaneConfigs_ = new TailPaneConfig[0];
    }

    //--------------------------------------------------------------------------
    //  IJTailConfig Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.jtail.config.IJTailConfig#setDefaultConfig(
     *      toolbox.jtail.config.ITailPaneConfig)
     */
    public void setDefaultConfig(ITailPaneConfig defaultConfig)
    {
        defaultConfig_ = defaultConfig;
    }
    
    /**
     * @see toolbox.jtail.config.IJTailConfig#getDefaultConfig()
     */
    public ITailPaneConfig getDefaultConfig()
    {
        return defaultConfig_;
    }

    /**
     * @see toolbox.jtail.config.IJTailConfig#getTailConfigs()
     */
    public ITailPaneConfig[] getTailConfigs()
    {
        return tailPaneConfigs_;
    }

    /**
     * @see toolbox.jtail.config.IJTailConfig#setTailConfigs(
     *      toolbox.jtail.config.ITailPaneConfig[])
     */
    public void setTailConfigs(ITailPaneConfig[] tailPaneConfigs)
    {
        tailPaneConfigs_ = tailPaneConfigs;
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = prefs.getFirstChildElement(NODE_JTAIL);
        
        if (root != null)
        {
            // Defaults
            Element defaultsNode = root.getFirstChildElement(NODE_DEFAULTS);
            TailPaneConfig defaultTail = new TailPaneConfig();
            defaultTail.applyPrefs(defaultsNode);
            setDefaultConfig(defaultTail);
    
            // List of tails                        
            ITailPaneConfig[] tailPaneConfigs = new ITailPaneConfig[0];
            Elements tails = root.getChildElements(NODE_TAIL);
            
            for (int i = 0; i < tails.size(); i++)
            {
                TailPaneConfig tpc = new TailPaneConfig();
                Element tail = tails.get(i);
                tpc.applyPrefs(tail);
                
                tailPaneConfigs = (ITailPaneConfig[])
                    ArrayUtil.add(tailPaneConfigs, tpc);
            }
    
            setTailConfigs(tailPaneConfigs);
        }
    }
    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_JTAIL);

        Element defaultsNode = new Element(NODE_DEFAULTS);
        root.appendChild(defaultsNode);
        defaultConfig_.savePrefs(defaultsNode);

        for (int i = 0; i < tailPaneConfigs_.length; i++)
        { 
            Element tail = new Element(NODE_TAIL);
            tailPaneConfigs_[i].savePrefs(tail);
            root.appendChild(tail);
        }
        
        XOMUtil.insertOrReplace(prefs, root);
    }
}