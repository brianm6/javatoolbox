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
    
    /**
     * Constructor for JTailConfig.
     */
    public JTailConfig()
    {
    }

    /**
     * Marshals from IJTailConfig -> XML
     * 
     * @return  Tail XML node
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
        jtailNode.setAttribute(ATTR_DIR, getDirectory());

        // Tail element
        Element tail = new Element(ELEMENT_TAIL);
        tail.setAttribute(ATTR_AUTOSCROLL, 
            new Boolean(getDefaultAutoScroll()).toString());
        tail.setAttribute(ATTR_LINENUMBERS, 
            new Boolean(getDefaultShowLineNumbers()).toString());
        
        // Font element    
        Element font = new Element(ELEMENT_FONT);
        font.setAttribute(ATTR_FAMILY, getDefaultFont().getFamily());
        font.setAttribute(ATTR_STYLE, getDefaultFont().getStyle() + "");
        font.setAttribute(ATTR_SIZE, getDefaultFont().getSize() + "");            
        
        // Filter element
        Element filter = new Element(ELEMENT_FILTER);
        filter.setText(getDefaultFilter());
        
        // Add child nodes to tail
        tail.addElement(font);
        tail.addElement(filter);

        // Defaults
        Element defaultsNode = new Element(ELEMENT_DEFAULTS);
        defaultsNode.addElement(tail);
        
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
        
        // Handle optional default font element    
        Element fontNode = 
            jtailNode.getElement(TailPaneConfig.ELEMENT_FONT);
        
        if (fontNode != null)
        {
            String family = 
                fontNode.getAttribute(TailPaneConfig.ATTR_FAMILY);
                
            int style = Integer.parseInt(
                fontNode.getAttribute(TailPaneConfig.ATTR_STYLE));
                
            int size = Integer.parseInt(
                fontNode.getAttribute(TailPaneConfig.ATTR_SIZE));
                
            jtailConfig.setDefaultFont(new Font(family, style, size));
        }
        else
        {
            jtailConfig.setDefaultFont(SwingUtil.getPreferredMonoFont());
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

        jtailConfig.setTailPaneConfigs(tailPaneConfigs);
        
        return jtailConfig;
    }
    
    /**
     * Returns the defaultFont.
     * @return Font
     */
    public Font getDefaultFont()
    {
        return defaultFont_;
    }

    /**
     * Returns the location.
     * @return Point
     */
    public Point getLocation()
    {
        return location_;
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
     * Sets the defaultFont.
     * @param defaultFont The defaultFont to set
     */
    public void setDefaultFont(Font defaultFont)
    {
        defaultFont_ = defaultFont;
    }

    /**
     * Sets the location.
     * @param location The location to set
     */
    public void setLocation(Point location)
    {
        location_ = location;
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
     * Returns the tailPaneConfigs.
     * @return ITailPaneConfig[]
     */
    public ITailPaneConfig[] getTailPaneConfigs()
    {
        return tailPaneConfigs_;
    }

    /**
     * Sets the tailPaneConfigs.
     * @param tailPaneConfigs The tailPaneConfigs to set
     */
    public void setTailPaneConfigs(ITailPaneConfig[] tailPaneConfigs)
    {
        tailPaneConfigs_ = tailPaneConfigs;
    }
    
    /**
     * Returns the defaultAutoScroll.
     * @return boolean
     */
    public boolean getDefaultAutoScroll()
    {
        return defaultAutoScroll_;
    }

    /**
     * Returns the defaultFilter.
     * @return String
     */
    public String getDefaultFilter()
    {
        return defaultFilter_;
    }

    /**
     * Returns the defaultShowLineNumbers.
     * @return boolean
     */
    public boolean getDefaultShowLineNumbers()
    {
        return defaultShowLineNumbers_;
    }

    /**
     * Sets the defaultAutoScroll.
     * @param defaultAutoScroll The defaultAutoScroll to set
     */
    public void setDefaultAutoScroll(boolean defaultAutoScroll)
    {
        defaultAutoScroll_ = defaultAutoScroll;
    }

    /**
     * Sets the defaultFilter.
     * @param defaultFilter The defaultFilter to set
     */
    public void setDefaultFilter(String defaultFilter)
    {
        defaultFilter_ = defaultFilter;
    }

    /**
     * Sets the defaultShowLineNumbers.
     * @param defaultShowLineNumbers The defaultShowLineNumbers to set
     */
    public void setDefaultShowLineNumbers(boolean defaultShowLineNumbers)
    {
        defaultShowLineNumbers_ = defaultShowLineNumbers;
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
