package toolbox.jtail.config.tinyxml;

import java.awt.Font;
import java.io.IOException;

import org.apache.log4j.Category;
import toolbox.jtail.config.ITailPaneConfig;
import toolbox.util.collections.AsMap;
import toolbox.util.xml.XMLNode;


/**
 * TailConfig is a data object that captures the configuration of a
 * given tail instance with the ability to marshal itself to and from 
 * XML format. 
 */
public class TailPaneConfig implements ITailPaneConfig, XMLConstants
{
    /** Logger **/
    private static final Category logger_ =
        Category.getInstance(TailPaneConfig.class);
    
    private String  filename_;
    private boolean autoScroll_;
    private boolean showLineNumbers_;
    private boolean antiAlias_;
    private Font    font_;
    private String  filter_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
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
     * @param  antiAlias        Antialias text in output area
     * @param  font             Font of display text area
     * @param  filter           Optional filter (regular expression) 
     *                          for weeding out junk            
     */
    public TailPaneConfig(String file, boolean autoScroll, 
        boolean showLineNumbers, boolean antiAlias, Font font, String filter)
    {
        setFilename(file);
        setAutoScroll(autoScroll);
        setShowLineNumbers(showLineNumbers);
        setAntiAlias(antiAlias);
        setFont(font);
        setFilter(filter);
    }

    //--------------------------------------------------------------------------
    //  Implementation
    //--------------------------------------------------------------------------    
    
    /**
     * Unmarshals an XML element representing a TailConfig object
     * 
     * @param   tail  Element representing a TailPaneConfig
     * @return  Fully populated TailPaneConfig
     * @throws  IOException on IO error
     */
    public static TailPaneConfig unmarshal(XMLNode tail) throws IOException 
    {
        TailPaneConfig config = new TailPaneConfig();
        
        // Handle tail element
        config.setFilename(tail.getAttr(ATTR_FILE));
        
        // Optional autoscroll
        String autoscroll = tail.getAttr(ATTR_AUTOSCROLL);
        if (autoscroll != null)
            config.setAutoScroll(new Boolean(autoscroll).booleanValue());
        else
            config.setAutoScroll(DEFAULT_AUTOSCROLL);
                
        // Optional show line numbers
        String showLineNumbers = tail.getAttr(ATTR_LINENUMBERS);
        if (showLineNumbers != null)
            config.setShowLineNumbers(new Boolean(showLineNumbers).booleanValue());
        else
            config.setShowLineNumbers(DEFAULT_LINENUMBERS);
        
        // Optional antiAlias attribute
        String antiAlias = tail.getAttr(ATTR_ANTIALIAS);
        if (antiAlias != null)
            config.setAntiAlias(new Boolean(antiAlias).booleanValue());
        else
            config.setAntiAlias(DEFAULT_ANTIALIAS);
        
        // Handle optional font element    
        XMLNode fontNode = tail.getNode(ELEMENT_FONT);
        
        if (fontNode != null)
        {
            String family = fontNode.getAttr(ATTR_FAMILY);
            int style = Integer.parseInt(fontNode.getAttr(ATTR_STYLE));
            int size = Integer.parseInt(fontNode.getAttr(ATTR_SIZE));
            config.setFont(new Font(family, style, size));
        }
        else
        {
            // TODO: what is the default font?
            config.setFont(null);    
        }
        
        // Handle optional filter element
        XMLNode filterNode = tail.getNode(ELEMENT_FILTER);
        
        if (filterNode != null)
        {
            config.setFilter(filterNode.getValue());

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
     * <pre>
     * 
     * Tail
     *   |
     *   +--Font
     *   |
     *   +--Filter
     * 
     * </pre>
     * 
     * @return  Tail XML node
     * @throws  IOExcetion on IO error
     */
    public XMLNode marshal() throws IOException 
    {
        // Tail element
        XMLNode tail = new XMLNode(ELEMENT_TAIL);
        
        if (getFilename() != null)
            tail.addAttr(ATTR_FILE, getFilename());
            
        tail.addAttr(ATTR_AUTOSCROLL, isAutoScroll() + "");
        tail.addAttr(ATTR_LINENUMBERS, isShowLineNumbers() + "");
        tail.addAttr(ATTR_ANTIALIAS, isAntiAlias() + "");
        
        // Font element    
        XMLNode font = new XMLNode(ELEMENT_FONT);
        font.addAttr(ATTR_FAMILY, getFont().getFamily());
        font.addAttr(ATTR_STYLE, getFont().getStyle() + "");
        font.addAttr(ATTR_SIZE, getFont().getSize() + "");            
        
        // Filter element
        XMLNode filter = new XMLNode(ELEMENT_FILTER);
        filter.setPlaintext(getFilter());
        
        // Add child nodes to tail
        tail.addNode(font);
        tail.addNode(filter);
        
        return tail;
    }


    /**
     * @return String representation
     */
    public String toString()
    {
        return AsMap.of(this).toString();
    }

    //--------------------------------------------------------------------------
    //  Interface ITailPaneConfig
    //--------------------------------------------------------------------------

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
    
    /**
     * Accessor for the antialias flag
     * 
     * @return  True if antialias is on, false otherwise
     */
    public boolean isAntiAlias()
    {
        return antiAlias_;
    }
    
    
    /**
     * Mutator for the antialias flag
     * 
     * @param  b  True to turn antialias on, false otherwise
     */
    public void setAntiAlias(boolean b)
    {
        antiAlias_ = b;
    }
}