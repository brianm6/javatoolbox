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
 * Tiny XML implemenation of IJTailConfig interface that marshals
 * configuration information to/from XML
 */
public class JTailConfig implements IJTailConfig, XMLConstants
{ 
    /** Logger */
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
     * Constructor for JTailConfig.
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
        
        // Size
        jtailNode.addAttr(ATTR_HEIGHT, getSize().height + "");
        jtailNode.addAttr(ATTR_WIDTH, getSize().width + "");
        
        // Location
        jtailNode.addAttr(ATTR_X, getLocation().x + "");
        jtailNode.addAttr(ATTR_Y, getLocation().y + "");

        // Directory
        if (getDirectory() != null)
            jtailNode.addAttr(ATTR_DIR, getDirectory());

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
        
        // Read optional window location
        String x = jtailNode.getAttr(ATTR_X);
        String y = jtailNode.getAttr(ATTR_Y);
        
        if (x != null && y != null)
        {
            jtailConfig.setLocation(
                new Point(Integer.parseInt(x), Integer.parseInt(y)));
        }
        else
        {
            ; // TODO: set default location
        }
        
        // Read optional window size
        String height = jtailNode.getAttr(ATTR_HEIGHT);
        String width  = jtailNode.getAttr(ATTR_WIDTH);
        
        if (height != null && width != null)
        {
            jtailConfig.setSize(new Dimension(
                Integer.parseInt(width),Integer.parseInt(height)));
        }
        else
        {
            ; // TODO: set default size
        }
        
        // Read optional directory
        String dir = jtailNode.getAttr(ATTR_DIR);
        
        if (dir != null)   
        {
            jtailConfig.setDirectory(dir);
        }
        else
        {
            ; // TODO: set default directory
        }
        
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
         
                        
        // Iterate through each "tail" element and delegate the 
        // hydration to the TailPaneConfig object
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

    /**
     * @see toolbox.jtail.config.IJTailConfig#
     *          setDefaultConfig(toolbox.jtail.config.ITailPaneConfig)
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
     * @see toolbox.jtail.config.IJTailConfig#setLocation(java.awt.Point)
     */
    public void setLocation(Point location)
    {
        location_ = location;
    }

    /**
     * @see toolbox.jtail.config.IJTailConfig#getLocation()
     */
    public Point getLocation()
    {
        return location_;
    }

    /**
     * @see toolbox.jtail.config.IJTailConfig#setSize(java.awt.Dimension)
     */
    public void setSize(Dimension size)
    {
        size_ = size;
    }

    /**
     * @see toolbox.jtail.config.IJTailConfig#getSize()
     */
    public Dimension getSize()
    {
        return size_;
    }
    
    /**
     * @see toolbox.jtail.config.IJTailConfig#getTailConfigs()
     */
    public ITailPaneConfig[] getTailConfigs()
    {
        return tailPaneConfigs_;
    }

    /**
     * @see toolbox.jtail.config.IJTailConfig#
     *          setTailConfigs(toolbox.jtail.config.ITailPaneConfig[])
     */
    public void setTailConfigs(ITailPaneConfig[] tailPaneConfigs)
    {
        tailPaneConfigs_ = tailPaneConfigs;
    }

    /**
     * @see toolbox.jtail.config.IJTailConfig#getDirectory()
     */
    public String getDirectory()
    {
        return directory_;
    }

    /**
     * @see toolbox.jtail.config.IJTailConfig#setDirectory(java.lang.String)
     */
    public void setDirectory(String directory)
    {
        directory_ = directory;
    }
}
