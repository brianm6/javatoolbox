package toolbox.jtail.config.tinyxml;

import java.awt.Font;
import java.io.IOException;

import org.apache.log4j.Logger;

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
    private static final Logger logger_ =
        Logger.getLogger(TailPaneConfig.class);
    
    private String  filename_;
    private boolean autoScroll_;
    private boolean showLineNumbers_;
    private boolean antiAlias_;
    private Font    font_;
    private String  regularExpression_;
    private String  cutExpression_;

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
     * @param  file               File to tail
     * @param  autoScroll         Turn on autoscroll
     * @param  showLineNumbers    Shows line numbers in output
     * @param  antiAlias          Antialias text in output area
     * @param  font               Font of display text area
     * @param  regularExpression  Optional filter (regular expression) 
     *                            for weeding out junk
     * @param  cutExpression      Optional expression for removing columns
     */
    public TailPaneConfig(String file, boolean autoScroll, 
        boolean showLineNumbers, boolean antiAlias, Font font,
        String regularExpression, String cutExpression)
    {
        setFilename(file);
        setAutoScroll(autoScroll);
        setShowLineNumbers(showLineNumbers);
        setAntiAlias(antiAlias);
        setFont(font);
        setRegularExpression(regularExpression);
        setCutExpression(cutExpression);
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * XML -> TailConfig
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
            config.setShowLineNumbers(
                new Boolean(showLineNumbers).booleanValue());
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
        
        // Handle optional regular expression element
        XMLNode regexNode = tail.getNode(ELEMENT_REGULAR_EXPRESSION);
        
        if (regexNode != null)
        {
            config.setRegularExpression(regexNode.getAttr(ATTR_EXPRESSION));
            // TODO: support case sensetivity
        }
        else
        {
            config.setRegularExpression(DEFAULT_REGEX);
        }

        // Handle optional cut expression element
        XMLNode cutNode = tail.getNode(ELEMENT_CUT_EXPRESSION);
        
        if (cutNode != null)
            config.setCutExpression(cutNode.getAttr(ATTR_EXPRESSION));
        else
            config.setCutExpression(DEFAULT_CUT_EXPRESSION);
            
        return config;
    }


    /**
     * TailConfig -> XML
     * <pre>
     * 
     * Tail
     *   |
     *   +--Font
     *   |
     *   +--RegularExpression
     *   |
     *   +--CutExpression
     * 
     * </pre>
     * 
     * @return  Tail XML node
     * @throws  IOException on IO error
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
        XMLNode fontNode = new XMLNode(ELEMENT_FONT);
        fontNode.addAttr(ATTR_FAMILY, getFont().getFamily());
        fontNode.addAttr(ATTR_STYLE, getFont().getStyle() + "");
        fontNode.addAttr(ATTR_SIZE, getFont().getSize() + "");            
        
        // Regex element
        XMLNode regexNode = new XMLNode(ELEMENT_REGULAR_EXPRESSION);
        regexNode.addAttr(ATTR_EXPRESSION, getRegularExpression());

        // Cut element
        XMLNode cutNode = new XMLNode(ELEMENT_CUT_EXPRESSION);
        cutNode.addAttr(ATTR_EXPRESSION, getCutExpression());
        
        // Add child nodes to tail
        tail.addNode(fontNode);        
        tail.addNode(regexNode);
        tail.addNode(cutNode);        
        
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
    //  ITailPaneConfig Interface
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
    public String getRegularExpression()
    {
        return regularExpression_;
    }


    /**
     * Sets the filter.
     * 
     * @param filter The filter to set
     */
    public void setRegularExpression(String filter)
    {
        regularExpression_ = filter;
    }
    
    /**
     * @see toolbox.jtail.config.ITailPaneConfig#getCutExpression()
     */
    public String getCutExpression()
    {
        return cutExpression_;
    }
    
    /**
     * @see toolbox.jtail.config.ITailPaneConfig#setCutExpression(String)
     */
    public void setCutExpression(String cutExpression)
    {
        cutExpression_ = cutExpression;
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