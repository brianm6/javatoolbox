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
    /** Logger **/
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
        String method = "[marshl] ";
        
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
        logger_.debug(method + 
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
        String method = "[unmars] ";
        
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
            // TODO: set default location
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
            // TODO: set default size
        }
        
        // Read optional directory
        String dir = jtailNode.getAttr(ATTR_DIR);
        
        if (dir != null)   
        {
            jtailConfig.setDirectory(dir);
        }
        else
        {
            // TODO: set default directory
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
                logger_.warn(method + 
                    "Expected XML node JTail->Defaults->Tail");
                    
                jtailConfig.setDefaultConfig(new TailPaneConfig());
            }
        }
        else
        {
            logger_.warn(method + "Expected XML node JTail->Defaults");
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
     * Sets the default tail pane configuration
     * 
     * @param defaultConfig  The default tail pane configuration
     */
    public void setDefaultConfig(ITailPaneConfig defaultConfig)
    {
        defaultConfig_ = defaultConfig;
    }

    
    /**
     * Returns the default tail pane configuration
     * 
     * @return  ITailPaneConfig
     */
    public ITailPaneConfig getDefaultConfig()
    {
        return defaultConfig_;
    }


    /**
     * Sets the location.
     * 
     * @param location The location to set
     */
    public void setLocation(Point location)
    {
        location_ = location;
    }


    /**
     * Returns the location.
     * 
     * @return Point
     */
    public Point getLocation()
    {
        return location_;
    }


    /**
     * Sets the size.
     * @param size The size to set
     */
    public void setSize(Dimension size)
    {
        size_ = size;
    }

    /**
     * Returns the size.
     * @return Dimension
     */
    public Dimension getSize()
    {
        return size_;
    }
    
    /**
     * Returns the tailPaneConfigs.
     * @return ITailPaneConfig[]
     */
    public ITailPaneConfig[] getTailConfigs()
    {
        return tailPaneConfigs_;
    }

    /**
     * Sets the tailPaneConfigs.
     * @param tailPaneConfigs The tailPaneConfigs to set
     */
    public void setTailConfigs(ITailPaneConfig[] tailPaneConfigs)
    {
        tailPaneConfigs_ = tailPaneConfigs;
    }
    

    /**
     * Returns the last directory selecting in the file explorer pane
     *
     * @return  String
     */
    public String getDirectory()
    {
        return directory_;
    }

    /**
     * Sets the last directory selected in the file explorer pane
     * 
     * @param directory  Directory selected
     */
    public void setDirectory(String directory)
    {
        directory_ = directory;
    }
}
