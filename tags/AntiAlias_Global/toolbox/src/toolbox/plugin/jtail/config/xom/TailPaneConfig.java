package toolbox.jtail.config.xom;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;

import toolbox.jtail.config.ITailPaneConfig;
import toolbox.util.FontUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.plugin.IPreferenced;

/**
 * TailConfig is a data object that captures the configuration of a given tail 
 * instance with the ability to marshal itself to and from XML format.
 */
public class TailPaneConfig implements ITailPaneConfig, XMLConstants, 
    IPreferenced
{
    private static final Logger logger_ =
        Logger.getLogger(TailPaneConfig.class);
    
    private String[]  filenames_;
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
        this(   new String[0], // file
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
     * @param  files              Files to tail
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
        String[] files, 
        boolean autoScroll, 
        boolean showLineNumbers, 
        boolean antiAlias, 
        Font font,
        String regularExpression, 
        String cutExpression, 
        boolean autoStart)
    {
        setFilenames(files);
        setAutoScroll(autoScroll);
        setShowLineNumbers(showLineNumbers);
        setAntiAlias(antiAlias);
        setFont(font);
        setRegularExpression(regularExpression);
        setCutExpression(cutExpression);
        setAutoStart(autoStart);
    }

    //--------------------------------------------------------------------------
    //  IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.ui.plugin.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = prefs.getFirstChildElement(NODE_TAIL);
       
        setAutoScroll(
            XOMUtil.getBooleanAttribute(
                root, ATTR_AUTOSCROLL, DEFAULT_AUTOSCROLL));
        
        setShowLineNumbers(
            XOMUtil.getBooleanAttribute(
                root, ATTR_LINENUMBERS, DEFAULT_LINENUMBERS));
        
        setAntiAlias(
            XOMUtil.getBooleanAttribute(
                root, ATTR_ANTIALIAS, DEFAULT_ANTIALIAS));

        setAutoStart(
            XOMUtil.getBooleanAttribute(
                root, ATTR_AUTOSTART, DEFAULT_AUTOSTART));

        if (root != null)
        {
            List filenames = new ArrayList();
            Elements files = root.getChildElements(NODE_FILE);
            for (int i=0; i<files.size(); i++)
                filenames.add(files.get(i).getAttributeValue(ATTR_FILENAME));
            setFilenames((String[]) filenames.toArray(new String[0]));

            setFont(
                FontUtil.toFont(root.getFirstChildElement(NODE_FONT)));
            
            setRegularExpression( 
                XOMUtil.getString(root.getFirstChildElement(
                    NODE_REGULAR_EXPRESSION), DEFAULT_REGEX));

            setCutExpression( 
                XOMUtil.getString(root.getFirstChildElement(
                    NODE_CUT_EXPRESSION), DEFAULT_CUT_EXPRESSION));
        }
    }

    /**
     * @see toolbox.util.ui.plugin.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_TAIL);

        root.addAttribute(
            new Attribute(ATTR_LINENUMBERS,isShowLineNumbers()+""));
        
        root.addAttribute(new Attribute(ATTR_AUTOSCROLL, isAutoScroll() + ""));    
        root.addAttribute(new Attribute(ATTR_ANTIALIAS, isAntiAlias() + ""));
        root.addAttribute(new Attribute(ATTR_AUTOSTART, isAutoStart() + ""));

        for (int i=0; i<filenames_.length; i++)
        {
            Element file = new Element(NODE_FILE);
            file.addAttribute(new Attribute(ATTR_FILENAME, filenames_[i]));
            root.appendChild(file);
        }        
        
        Element fontNode = FontUtil.toElement(getFont());
        Element regexNode = new Element(NODE_REGULAR_EXPRESSION);
        
        regexNode.addAttribute(
            new Attribute(ATTR_EXPRESSION, getRegularExpression()));

        Element cutNode = new Element(NODE_CUT_EXPRESSION);
        
        cutNode.addAttribute(
            new Attribute(ATTR_EXPRESSION, getCutExpression()));
        
        // Add child nodes to tail
        root.appendChild(fontNode);        
        root.appendChild(regexNode);
        root.appendChild(cutNode);        
        
        XOMUtil.insertOrReplace(prefs, root);
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
            s = super.toString();
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

    public String[] getFilenames()
    {
        return filenames_;
    }

    public boolean isShowLineNumbers()
    {
        return showLineNumbers_;
    }
 
    public void setAutoScroll(boolean autoScroll)
    {
        autoScroll_ = autoScroll;
    }

    public void setFilenames(String[] filenames)
    {
        filenames_ = filenames;
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