package toolbox.jtail.config.xom;

import java.io.IOException;

import org.apache.log4j.Logger;

import nu.xom.Element;
import nu.xom.Elements;

import toolbox.jtail.config.IJTailConfig;
import toolbox.jtail.config.ITailPaneConfig;
import toolbox.util.ArrayUtil;

/**
 * Tiny XML implemenation of IJTailConfig interface that marshals configuration 
 * information to and from XML.
 */
public class JTailConfig implements IJTailConfig, XMLConstants
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
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * Marshals from IJTailConfig -> XML
     * 
     * @return  JTail XML node
     * @throws  IOException on IO error
     */
    public Element marshal() throws IOException 
    {
        // Root config node
        Element jtailNode = new Element(ELEMENT_JTAIL);

        // Defaults
        Element defaultTailNode = ((TailPaneConfig) defaultConfig_).marshal();
        Element defaultsNode = new Element(ELEMENT_DEFAULTS);
        defaultsNode.appendChild(defaultTailNode);
        
        jtailNode.appendChild(defaultsNode);
        
        // Save child ITailPaneConfigs
        logger_.debug(
            "Saving " + tailPaneConfigs_.length + " configurations");

        for (int i=0; i<tailPaneConfigs_.length; i++)
        {
            TailPaneConfig tpc = (TailPaneConfig) tailPaneConfigs_[i];
            jtailNode.appendChild(tpc.marshal());                
        }
            
        return jtailNode;
    }    

    /**
     * Converts XML -> IJTailConfig
     * 
     * @param   jtailNode  Element representing a IJTailConfig
     * @return  Fully populated IJTailConfig
     * @throws  IOException on IO error
     */
    public static IJTailConfig unmarshal(Element jtailNode) throws IOException 
    {
        IJTailConfig jtailConfig = new JTailConfig();
        
        Element defaultsNode = jtailNode.getFirstChildElement(ELEMENT_DEFAULTS);
        
        if (defaultsNode != null)
        {
            Element defaultTailNode = defaultsNode.getFirstChildElement(ELEMENT_TAIL);
            
            if (defaultTailNode != null)
            {
                jtailConfig.setDefaultConfig(
                    TailPaneConfig.unmarshal(defaultTailNode));
            }
            else
            {
                logger_.warn(
                    "Expected XML node JTail->Defaults->Tail");
                    
                jtailConfig.setDefaultConfig(new TailPaneConfig());
            }
        }
        else
        {
            logger_.warn("Expected XML node JTail->Defaults");
            jtailConfig.setDefaultConfig(new TailPaneConfig());            
        }
         
                        
        // Iterate through each "tail" element and delegate the hydration to 
        // the TailPaneConfig object
        ITailPaneConfig[] tailPaneConfigs = new ITailPaneConfig[0];
        
        Elements tails = jtailNode.getChildElements(ELEMENT_TAIL);
        
        for (int i=0; i<tails.size(); i++)
        {
            Element tail = tails.get(i);
            
            //if (tail.getTagName().equals(TailPaneConfig.ELEMENT_TAIL))
            //{
            
            ITailPaneConfig tailPaneConfig = TailPaneConfig.unmarshal(tail);
            
            tailPaneConfigs = (ITailPaneConfig[])
                ArrayUtil.add(tailPaneConfigs, tailPaneConfig);
                    
                    
            //}
        }

        jtailConfig.setTailConfigs(tailPaneConfigs);
        
        return jtailConfig;
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
}
