package toolbox.jtail.config.tinyxml;

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.util.Enumeration;

import org.apache.log4j.Logger;

import toolbox.jtail.config.IJTailConfig;
import toolbox.jtail.config.ITailPaneConfig;
import toolbox.util.ArrayUtil;
import toolbox.util.xml.XMLNode;

/**
 * Tiny XML implemenation of IJTailConfig interface that marshals configuration 
 * information to and from XML.
 */
public class JTailConfig implements IJTailConfig, XMLConstants
{ 
    private static final Logger logger_ = 
        Logger.getLogger(JTailConfig.class);
    
    private Point               location_;
    private Dimension           size_;
    private String              directory_;
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
    public XMLNode marshal()  throws IOException 
    {
        // Root config node
        XMLNode jtailNode = new XMLNode(ELEMENT_JTAIL);

        // Defaults
        XMLNode defaultTailNode = ((TailPaneConfig) defaultConfig_).marshal();
        XMLNode defaultsNode = new XMLNode(ELEMENT_DEFAULTS);
        defaultsNode.addNode(defaultTailNode);
        
        jtailNode.addNode(defaultsNode);
        
        // Save child ITailPaneConfigs
        logger_.debug(
            "Saving " + tailPaneConfigs_.length + " configurations");

        for (int i=0; i<tailPaneConfigs_.length; i++)
        {
            TailPaneConfig tpc = (TailPaneConfig) tailPaneConfigs_[i];
            jtailNode.addNode(tpc.marshal());                
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
    public static IJTailConfig unmarshal(XMLNode jtailNode) throws IOException 
    {
        IJTailConfig jtailConfig = new JTailConfig();
        
        XMLNode defaultsNode = jtailNode.getNode(ELEMENT_DEFAULTS);
        
        if (defaultsNode != null)
        {
            XMLNode defaultTailNode = defaultsNode.getNode(ELEMENT_TAIL);
            
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
        
        for (Enumeration tails = jtailNode.enumerateNode();
             tails.hasMoreElements();)
        {
            XMLNode tail = (XMLNode) tails.nextElement();
            
            if (tail.getTagName().equals(TailPaneConfig.ELEMENT_TAIL))
            {
                ITailPaneConfig tailPaneConfig = TailPaneConfig.unmarshal(tail);
                
                tailPaneConfigs = (ITailPaneConfig[])
                    ArrayUtil.add(tailPaneConfigs, tailPaneConfig);
            }
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

    public void setLocation(Point location)
    {
        location_ = location;
    }

    public Point getLocation()
    {
        return location_;
    }

    public void setSize(Dimension size)
    {
        size_ = size;
    }

    public Dimension getSize()
    {
        return size_;
    }
    
    public ITailPaneConfig[] getTailConfigs()
    {
        return tailPaneConfigs_;
    }

    public void setTailConfigs(ITailPaneConfig[] tailPaneConfigs)
    {
        tailPaneConfigs_ = tailPaneConfigs;
    }

    public String getDirectory()
    {
        return directory_;
    }

    public void setDirectory(String directory)
    {
        directory_ = directory;
    }
}