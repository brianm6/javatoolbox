package toolbox.jtail.config.xom;

import java.awt.Font;
import java.io.IOException;

import org.apache.log4j.Logger;

import nu.xom.Attribute;
import nu.xom.Element;

import toolbox.jtail.config.ITailPaneConfig;
import toolbox.util.SwingUtil;

/**
 * TailConfig is a data object that captures the configuration of a given tail 
 * instance with the ability to marshal itself to and from XML format.
 */
public class TailPaneConfig implements ITailPaneConfig, XMLConstants
{
    private static final Logger logger_ =
        Logger.getLogger(TailPaneConfig.class);
    
    private String  filename_;
    private boolean autoScroll_;
    private boolean showLineNumbers_;
    private boolean antiAlias_;
    private boolean autoStart_;
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
        this(   "",         // file
                true,       // autoscroll
                false,      // show linenumbers
                false,      // antialias
                SwingUtil.getPreferredMonoFont(), // font
                "",         // Regular exp
                "",         // Cut exp
                true );     // autostart
    }

    /**
     * Creates TailConfig with given parameters
     * 
     * @param  file               File to tail
     * @param  autoScroll         Turn on autoscroll
     * @param  showLineNumbers    Shows line numbers in output
     * @param  antiAlias          Antialias text in output area
     * @param  font               Font of display text area
     * @param  regularExpression  Optional filter (regular expression) for 
     *                            weeding out junk
     * @param  cutExpression      Optional expression for removing columns
     * @param  autoStart          Autostarts tailing (starts it)
     */
    public TailPaneConfig(
        String file, 
        boolean autoScroll, 
        boolean showLineNumbers, 
        boolean antiAlias, 
        Font font,
        String regularExpression, 
        String cutExpression, 
        boolean autoStart)
    {
        setFilename(file);
        setAutoScroll(autoScroll);
        setShowLineNumbers(showLineNumbers);
        setAntiAlias(antiAlias);
        setFont(font);
        setRegularExpression(regularExpression);
        setCutExpression(cutExpression);
        setAutoStart(autoStart);
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
    public static TailPaneConfig unmarshal(Element tail) throws IOException 
    {
        TailPaneConfig config = new TailPaneConfig();
        
        // Handle tail element
        config.setFilename(tail.getAttributeValue(ATTR_FILE));
        
        // Optional autoscroll
        String autoscroll = tail.getAttributeValue(ATTR_AUTOSCROLL);
        if (autoscroll != null)
            config.setAutoScroll(new Boolean(autoscroll).booleanValue());
        else
            config.setAutoScroll(DEFAULT_AUTOSCROLL);
                
        // Optional show line numbers
        String showLineNumbers = tail.getAttributeValue(ATTR_LINENUMBERS);
        if (showLineNumbers != null)
            config.setShowLineNumbers(
                new Boolean(showLineNumbers).booleanValue());
        else
            config.setShowLineNumbers(DEFAULT_LINENUMBERS);
        
        // Optional antiAlias attribute
        String antiAlias = tail.getAttributeValue(ATTR_ANTIALIAS);
        if (antiAlias != null)
            config.setAntiAlias(new Boolean(antiAlias).booleanValue());
        else
            config.setAntiAlias(DEFAULT_ANTIALIAS);

        // Optional autoStart attribute
        String autoStart = tail.getAttributeValue(ATTR_AUTOSTART);
        if (autoStart != null)
            config.setAutoStart(new Boolean(autoStart).booleanValue());
        else
            config.setAutoStart(DEFAULT_AUTOSTART);
        
        // Handle optional font element    
        Element fontNode = tail.getFirstChildElement(ELEMENT_FONT);
        
        if (fontNode != null)
        {
            String family = fontNode.getAttributeValue(ATTR_FAMILY);
            int style = Integer.parseInt(fontNode.getAttributeValue(ATTR_STYLE));
            int size = Integer.parseInt(fontNode.getAttributeValue(ATTR_SIZE));
            config.setFont(new Font(family, style, size));
        }
        else
        {
            config.setFont(SwingUtil.getPreferredMonoFont());    
        }
        
        // Handle optional regular expression element
        Element regexNode = tail.getFirstChildElement(ELEMENT_REGULAR_EXPRESSION);
        
        if (regexNode != null)
        {
            config.setRegularExpression(regexNode.getAttributeValue(ATTR_EXPRESSION));
            // TODO: support case sensetivity
        }
        else
        {
            config.setRegularExpression(DEFAULT_REGEX);
        }

        // Handle optional cut expression element
        Element cutNode = tail.getFirstChildElement(ELEMENT_CUT_EXPRESSION);
        
        if (cutNode != null)
            config.setCutExpression(cutNode.getAttributeValue(ATTR_EXPRESSION));
        else
            config.setCutExpression(DEFAULT_CUT_EXPRESSION);
            
        return config;
    }

    /**
     * TailConfig -> XML.
     * <pre>
     * 
     * Tail attr = [file, autoscroll, lineNumbes, antialias, autostart]
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
    public Element marshal() throws IOException 
    {
        // Tail element
        Element tail = new Element(ELEMENT_TAIL);
        
        if (getFilename() != null)
            tail.addAttribute(new Attribute(ATTR_FILE, getFilename()));
            
        tail.addAttribute(new Attribute(ATTR_AUTOSCROLL, isAutoScroll() + ""));
        tail.addAttribute(new Attribute(ATTR_LINENUMBERS,isShowLineNumbers()+""));
        tail.addAttribute(new Attribute(ATTR_ANTIALIAS, isAntiAlias() + ""));
        tail.addAttribute(new Attribute(ATTR_AUTOSTART, isAutoStart() + ""));
        
        // Font element    
        Element fontNode = new Element(ELEMENT_FONT);
        fontNode.addAttribute(new Attribute(ATTR_FAMILY, getFont().getFamily()));
        fontNode.addAttribute(new Attribute(ATTR_STYLE, getFont().getStyle() + ""));
        fontNode.addAttribute(new Attribute(ATTR_SIZE, getFont().getSize() + ""));            
        
        // Regex element
        Element regexNode = new Element(ELEMENT_REGULAR_EXPRESSION);
        regexNode.addAttribute(new Attribute(ATTR_EXPRESSION, getRegularExpression()));

        // Cut element
        Element cutNode = new Element(ELEMENT_CUT_EXPRESSION);
        cutNode.addAttribute(new Attribute(ATTR_EXPRESSION, getCutExpression()));
        
        // Add child nodes to tail
        tail.appendChild(fontNode);        
        tail.appendChild(regexNode);
        tail.appendChild(cutNode);        
        
        return tail;
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object 
    //--------------------------------------------------------------------------
    
    /**
     * @return String representation
     */
    public String toString()
    {
        String s = null;
        
        try
        {
            s = marshal().toString();
        }
        catch (Exception ioe)
        {
            logger_.error("toString", ioe);
        }
        
        return s;
    }

    //--------------------------------------------------------------------------
    //  ITailPaneConfig Interface
    //--------------------------------------------------------------------------

    public boolean isAutoScroll()
    {
        return autoScroll_;
    }

    public String getFilename()
    {
        return filename_;
    }

    public boolean isShowLineNumbers()
    {
        return showLineNumbers_;
    }
 
    public void setAutoScroll(boolean autoScroll)
    {
        autoScroll_ = autoScroll;
    }

    public void setFilename(String filename)
    {
        filename_ = filename;
    }

    public void setShowLineNumbers(boolean showLineNumbers)
    {
        showLineNumbers_ = showLineNumbers;
    }
 
    public Font getFont()
    {
        return font_;
    }

    public void setFont(Font font)
    {
        font_ = font;
    }
 
    public String getRegularExpression()
    {
        return regularExpression_;
    }

    public void setRegularExpression(String filter)
    {
        regularExpression_ = filter;
    }

    public String getCutExpression()
    {
        return cutExpression_;
    }

    public void setCutExpression(String cutExpression)
    {
        cutExpression_ = cutExpression;
    }

    public boolean isAntiAlias()
    {
        return antiAlias_;
    }

    public void setAntiAlias(boolean b)
    {
        antiAlias_ = b;
    }
 
    public boolean isAutoStart()
    {
        return autoStart_;
    }

    public void setAutoStart(boolean autoStart)
    {
        autoStart_ = autoStart;
    }
}