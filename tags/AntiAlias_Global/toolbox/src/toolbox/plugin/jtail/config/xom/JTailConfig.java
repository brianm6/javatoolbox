package toolbox.jtail.config.xom;

import org.apache.log4j.Logger;

import nu.xom.Element;
import nu.xom.Elements;

import toolbox.jtail.config.IJTailConfig;
import toolbox.jtail.config.ITailPaneConfig;
import toolbox.util.ArrayUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.plugin.IPreferenced;

/**
 * Tiny XML implemenation of IJTailConfig interface that marshals configuration 
 * information to and from XML.
 */
public class JTailConfig implements IJTailConfig, XMLConstants, IPreferenced
{ 
    private static final Logger logger_ = 
        Logger.getLogger(JTailConfig.class);
    
    private ITailPaneConfig[]   tailPaneConfigs_;
    private ITailPaneConfig     defaultConfig_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor
     */
    public JTailConfig()
    {
        defaultConfig_ = new TailPaneConfig();
        tailPaneConfigs_ = new TailPaneConfig[0];
    }

    //--------------------------------------------------------------------------
    //  IJTailConfig Interface
    //--------------------------------------------------------------------------

    public void setDefaultConfig(ITailPaneConfig defaultConfig)
    {
        defaultConfig_ = defaultConfig;
    }
    
    public ITailPaneConfig getDefaultConfig()
    {
        return defaultConfig_;
    }

    public ITailPaneConfig[] getTailConfigs()
    {
        return tailPaneConfigs_;
    }

    public void setTailConfigs(ITailPaneConfig[] tailPaneConfigs)
    {
        tailPaneConfigs_ = tailPaneConfigs;
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plugin.IPreferenced#applyPrefs(nu.xom.Element)
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
            
            for (int i=0; i<tails.size(); i++)
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
     * @see toolbox.util.ui.plugin.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_JTAIL);

        Element defaultsNode = new Element(NODE_DEFAULTS);
        root.appendChild(defaultsNode);
        defaultConfig_.savePrefs(defaultsNode);

        for (int i=0; i<tailPaneConfigs_.length; i++)
        { 
            Element tail = new Element(NODE_TAIL) ;
            tailPaneConfigs_[i].savePrefs(tail);
            root.appendChild(tail);
        }
        
        XOMUtil.insertOrReplace(prefs, root);
    }
}