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
 * enclosing_type
 */
public class JTailConfig implements IJTailConfig
{
    private static final Category logger_ = 
        Category.getInstance(JTailConfig.class);
        
    private static final String ELEMENT_JTAIL = "JTail";
    private static final String ATTR_HEIGHT   = "height";
    private static final String ATTR_WIDTH    = "width";
    private static final String ATTR_X        = "x";
    private static final String ATTR_Y        = "y";
    

    
    
    private Font defaultFont_;
    private Point location_;
    private Dimension size_;
    private ITailPaneConfig[] tailPaneConfigs_;
    
    /**
     * Constructor for JTailConfig.
     */
    public JTailConfig()
    {
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
     * Marshals from IJTailConfig -> XML
     * 
     * @return  Tail XML node
     * @throws  IOExcetion on IO error
     */
    public Element marshal()  throws IOException 
    {
        // Root config node
        Element jtailNode = new Element(ELEMENT_JTAIL);
        
        // Size
        jtailNode.setAttribute(ATTR_HEIGHT, getSize().height + "");
        jtailNode.setAttribute(ATTR_WIDTH, getSize().width + "");
        
        // Location
        jtailNode.setAttribute(ATTR_X, getLocation().x + "");
        jtailNode.setAttribute(ATTR_Y, getLocation().y + "");

        // Default font
        Element fontNode = new Element(TailPaneConfig.ELEMENT_FONT);
        fontNode.setAttribute(
            TailPaneConfig.ATTR_FAMILY, getDefaultFont().getFamily());
        fontNode.setAttribute(
            TailPaneConfig.ATTR_STYLE, getDefaultFont().getStyle() + "");
        fontNode.setAttribute(
            TailPaneConfig.ATTR_SIZE, getDefaultFont().getSize() + "");            
        jtailNode.addElement(fontNode);

        
        // Save child ITailPaneConfigs
        logger_.debug("Saving "+ tailPaneConfigs_.length+" configurations");

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
}
