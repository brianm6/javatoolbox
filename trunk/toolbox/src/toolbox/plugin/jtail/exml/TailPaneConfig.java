package toolbox.jtail.exml;

import java.awt.Font;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Category;

import toolbox.jtail.config.ITailPaneConfig;
import toolbox.util.collections.AsMap;
import toolbox.util.collections.ObjectMap;

import electric.xml.Attribute;
import electric.xml.Attributes;
import electric.xml.Element;

/**
 * TailConfig is a data object that captures the configuration of a
 * given tail instance with the ability to marshal itself to and from 
 * XML format. 
 */
public class TailPaneConfig implements ITailPaneConfig
{
    /** Logger **/
    private static final Category logger_ =
        Category.getInstance(TailPaneConfig.class);
    
    // Tail XML element
    public  static final String ELEMENT_TAIL     = "Tail";
    private static final String ATTR_FILE        = "file";
    private static final String ATTR_AUTOSCROLL  = "autoScroll";
    private static final String ATTR_LINENUMBERS = "showLineNumbers";
    
    // Font XML element
    public static final String ELEMENT_FONT     = "Font";
    public static final String ATTR_FAMILY      = "family";
    public static final String ATTR_STYLE       = "style";        
    public static final String ATTR_SIZE        = "size";

    // Filter XML element
    private static final String ELEMENT_FILTER = "Filter";
    private static final String ATTR_NEGATE    = "negate";
    private static final String ATTR_MATCH_CASE = "matchCase";

    private String  filename_;
    private boolean autoScroll_;
    private boolean showLineNumbers_;
    private Font    font_;
    private String  filter_;

    //
    //  CONSTRUCTORS
    //

    /**
     * Default constructor
     */
    public TailPaneConfig()
    {
    }


    /**
     * Creates TailConfig with given parameters
     * 
     * @param  file             File to tail
     * @param  autoScroll       Turn on autoscroll
     * @param  showLineNumbers  Shows line numbers in output
     * @param  font             Font of display text area
     * @param  filter           Optional filter (regular expression) 
     *                            for weeding out junk            
     */
    public TailPaneConfig(String file, boolean autoScroll, boolean showLineNumbers,
        Font font, String filter)
    {
        setFilename(file);
        setAutoScroll(autoScroll);
        setShowLineNumbers(showLineNumbers);
        setFont(font);
        setFilter(filter);
    }

    //
    //  MEATY STUFF
    //
    
    /**
     * Unmarshals an XML element representing a TailConfig object
     * 
     * @param   tail  Element representing a TailConfigs
     * @return  Fully populated TailConfig
     * @throws  IOException on IO error
     */
    public static TailPaneConfig unmarshal(Element tail) throws IOException 
    {
        
        // DEBUG
        
        Attributes attribs = tail.getAttributeObjects();
        
        while (attribs.hasMoreElements())
        {
            Attribute attrib = attribs.next();
            
            logger_.debug("[unmars] " + attrib.getName() + " : " + 
                attrib.getValue());
        }
        
        // REAL 
        
        TailPaneConfig config = new TailPaneConfig();
        
        // Handle tail element
        config.setFilename(tail.getAttribute(ATTR_FILE));
        
        // Optional autoscroll
        if (tail.getAttribute(ATTR_AUTOSCROLL) != null)
            config.setAutoScroll(new Boolean(
                tail.getAttribute(ATTR_AUTOSCROLL)).booleanValue());
        else
            config.setAutoScroll(DEFAULT_AUTOSCROLL);
                
        // Optional show line numbers
        if (tail.getAttribute(ATTR_LINENUMBERS) != null)
            config.setShowLineNumbers(new Boolean(
                tail.getAttribute(ATTR_LINENUMBERS)).booleanValue());
        else
            config.setShowLineNumbers(DEFAULT_LINENUMBERS);
        
        // Handle optional font element    
        Element fontNode = tail.getElement(ELEMENT_FONT);
        
        if (fontNode != null)
        {
            String family = fontNode.getAttribute(ATTR_FAMILY);
            int style = Integer.parseInt(fontNode.getAttribute(ATTR_STYLE));
            int size = Integer.parseInt(fontNode.getAttribute(ATTR_SIZE));
            config.setFont(new Font(family, style, size));
        }
        else
        {
            // TODO: what is the default font?
            config.setFont(null);    
        }
        
        // Handle optional filter element
        Element filterNode = tail.getElement(ELEMENT_FILTER);
        
        if (filterNode != null)
        {
            config.setFilter(filterNode.getTextString());

            // TODO: support negate and case
        }
        else
        {
            config.setFilter(DEFAULT_FILTER);
        }
            
        return config;
    }


    /**
     * Marshals from Java object representation to XML representation
     * 
     * @return  Tail XML node
     * @throws  IOExcetion on IO error
     */
    public Element marshal()  throws IOException 
    {
        // Tail element
        Element tail = new Element(ELEMENT_TAIL);
        tail.setAttribute(ATTR_FILE, getFilename());
        tail.setAttribute(ATTR_AUTOSCROLL, 
            new Boolean(isAutoScroll()).toString());
        tail.setAttribute(ATTR_LINENUMBERS, 
            new Boolean(isShowLineNumbers()).toString());
        
        // Font element    
        Element font = new Element(ELEMENT_FONT);
        font.setAttribute(ATTR_FAMILY, getFont().getFamily());
        font.setAttribute(ATTR_STYLE, getFont().getStyle() + "");
        font.setAttribute(ATTR_SIZE, getFont().getSize() + "");            
        
        // Filter element
        Element filter = new Element(ELEMENT_FILTER);
        filter.setText(getFilter());
        
        // Add child nodes to tail
        tail.addElement(font);
        tail.addElement(filter);
        
        return tail;
    }


    /**
     * @return String representation
     */
    public String toString()
    {
        return AsMap.of(this).toString();
    }


    //
    //  ACCESSORS/MUTATORS
    //


    /**
     * Returns the autoScroll.
     * 
     * @return boolean
     */
    public boolean isAutoScroll()
    {
        return autoScroll_;
    }


    /**
     * Returns the filename.
     * 
     * @return String
     */
    public String getFilename()
    {
        return filename_;
    }
    

    /**
     * Returns the showLineNumbers.
     * 
     * @return boolean
     */
    public boolean isShowLineNumbers()
    {
        return showLineNumbers_;
    }


    /**
     * Sets the autoScroll.
     * 
     * @param autoScroll The autoScroll to set
     */
    public void setAutoScroll(boolean autoScroll)
    {
        autoScroll_ = autoScroll;
    }


    /**
     * Sets the filename.
     * 
     * @param filename The filename to set
     */
    public void setFilename(String filename)
    {
        filename_ = filename;
    }


    /**
     * Sets the showLineNumbers.
     * 
     * @param showLineNumbers The showLineNumbers to set
     */
    public void setShowLineNumbers(boolean showLineNumbers)
    {
        showLineNumbers_ = showLineNumbers;
    }
    
    
    /**
     * Returns the font.
     * 
     * @return Font
     */
    public Font getFont()
    {
        return font_;
    }


    /**
     * Sets the font.
     * 
     * @param font The font to set
     */
    public void setFont(Font font)
    {
        font_ = font;
    }
    
    
    /**
     * Returns the filter.
     * 
     * @return String
     */
    public String getFilter()
    {
        return filter_;
    }


    /**
     * Sets the filter.
     * 
     * @param filter The filter to set
     */
    public void setFilter(String filter)
    {
        filter_ = filter;
    }
}
