package toolbox.jtail.exml;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.io.IOException;

import org.apache.log4j.Category;
import toolbox.jtail.config.IJTailConfig;
import toolbox.jtail.config.ITailPaneConfig;
import toolbox.util.SwingUtil;

import electric.util.ArrayUtil;
import electric.xml.Element;
import electric.xml.Elements;

/**
 * Electric XML implemenation of IJTailConfig interface that marshals
 * configuration information to/from XML
 */
public class JTailConfig implements IJTailConfig, XMLConstants
{ 
    /** Logger **/
    private static final Category logger_ = 
        Category.getInstance(JTailConfig.class);
        
    
    private Font      defaultFont_;
    private boolean   defaultAutoScroll_;
    private boolean   defaultShowLineNumbers_;
    private String    defaultFilter_;
    private Point     location_;
    private Dimension size_;
    private String    directory_;
    private ITailPaneConfig[] tailPaneConfigs_;
    private ITailPaneConfig   defaultConfig_;
    
    /**
     * Constructor for JTailConfig.
     */
    public JTailConfig()
    {
        defaultConfig_ = new TailPaneConfig();
        tailPaneConfigs_ = new TailPaneConfig[0];
    }


    /**
     * Marshals from IJTailConfig -> XML
     * 
     * @return  JTail XML node
     * @throws  IOExcetion on IO error
     */
    public Element marshal()  throws IOException 
    {
        String method = "[marshl] ";
        
        // Root config node
        Element jtailNode = new Element(ELEMENT_JTAIL);
        
        // Size
        jtailNode.setAttribute(ATTR_HEIGHT, getSize().height + "");
        jtailNode.setAttribute(ATTR_WIDTH, getSize().width + "");
        
        // Location
        jtailNode.setAttribute(ATTR_X, getLocation().x + "");
        jtailNode.setAttribute(ATTR_Y, getLocation().y + "");

        // Directory
        if (getDirectory() != null)
            jtailNode.setAttribute(ATTR_DIR, getDirectory());

        // Defaults
        Element defaultTailNode = ((TailPaneConfig) defaultConfig_).marshal();        
        Element defaultsNode = new Element(ELEMENT_DEFAULTS);
        defaultsNode.addElement(defaultTailNode);
        
        jtailNode.addElement(defaultsNode);
        
        // Save child ITailPaneConfigs
        logger_.debug(method + 
            "Saving " + tailPaneConfigs_.length + " configurations");

        for (int i=0; i<tailPaneConfigs_.length;
            jtailNode.addElement((
                (TailPaneConfig)tailPaneConfigs_[i++]).marshal()));                
            
        return jtailNode;
    }    
    

    /**
     * Converts XML -> IJTailConfig
     * 
     * @param   tail  Element representing a IJTailConfig
     * @return  Fully populated IJTailConfig
     * @throws  IOException on IO error
     */
    public static IJTailConfig unmarshal(Element jtailNode) throws IOException 
    {
        String method = "[unmars] ";
        
        IJTailConfig jtailConfig = new JTailConfig();
        
        // Read optional window location
        if ((jtailNode.getAttribute(ATTR_X) != null) &&
            (jtailNode.getAttribute(ATTR_Y) != null))
        {
            Point location = new Point();
            
            location.x = 
                Integer.parseInt(jtailNode.getAttribute(ATTR_X));
                
            location.y = 
                Integer.parseInt(jtailNode.getAttribute(ATTR_Y));
                
            jtailConfig.setLocation(location);
        }
        else
        {
            // TODO: set default location
        }
        
        // Read optional window size
        if ((jtailNode.getAttribute(ATTR_HEIGHT) != null) &&
            (jtailNode.getAttribute(ATTR_WIDTH)!= null))
        {
            
            Dimension size = new Dimension();
            
            size.height = Integer.parseInt(
                jtailNode.getAttribute(ATTR_HEIGHT));
                
            size.width  = Integer.parseInt(
                jtailNode.getAttribute(ATTR_WIDTH));
                
            jtailConfig.setSize(size);
        }
        else
        {
            // TODO: set default size
        }
        
        // Read optional directory
        if (jtailNode.getAttribute(ATTR_DIR) != null)   
        {
            jtailConfig.setDirectory(jtailNode.getAttribute(ATTR_DIR));
        }
        else
        {
            // TODO: set default directory
        }
        
        Element defaultsNode = jtailNode.getElement(ELEMENT_DEFAULTS);
        
        if (defaultsNode != null)
        {
            Element defaultTailNode = defaultsNode.getElement(ELEMENT_TAIL);
            
            if (defaultTailNode != null)
            {
                jtailConfig.setDefaultConfig(
                    TailPaneConfig.unmarshal(defaultTailNode));
            }
            else
            {
                logger_.warn(method + "Expected XML node JTail->Defaults->Tail");
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
        
        for (Elements tails = 
             jtailNode.getElements(TailPaneConfig.ELEMENT_TAIL); 
             tails.hasMoreElements();)
        {
            Element tail = tails.next();
            ITailPaneConfig tailPaneConfig = TailPaneConfig.unmarshal(tail);                    
            
            tailPaneConfigs = (ITailPaneConfig[])
                ArrayUtil.addElement(tailPaneConfigs, tailPaneConfig);
        }

        jtailConfig.setTailConfigs(tailPaneConfigs);
        
        return jtailConfig;
    }
    


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