package toolbox.plugin.jtail.config.xom;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;

import toolbox.plugin.jtail.config.ITailPaneConfig;
import toolbox.util.FontUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.workspace.IPreferenced;

/**
 * TailConfig is a data object that captures the configuration of a given tail 
 * instance with the ability to marshal itself to and from XML format.
 */
public class TailPaneConfig implements ITailPaneConfig, XMLConstants, 
    IPreferenced
{
    private static final Logger logger_ = 
        Logger.getLogger(TailPaneConfig.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Files being tailed.
     */
    private String[] filenames_;
    
    /**
     * Flag to turn line numbers on/off.
     */
    private boolean showLineNumbers_;
    
    /**
     * Flag to start the tail automatically at startup.
     */
    private boolean autoStart_;
    
    /**
     * Regular expression to filter the tail output.
     */
    private String regularExpression_;
    
    /**
     * Cut expression to filter the tail output.
     */
    private String cutExpression_;

    // TODO: Following attribs need to be migrated out of here -----------------
    
    /**
     * Font for the textarea. Should migrate to textarea preferences.
     */
    private Font font_;
    
    /**
     * Flag to turn smooth fonts on/off. Should migrate to textarea preferences.
     */
    private boolean antiAlias_;
    
    /**
     * Autoscroll flag. Should migrate to textarea preferences.
     */
    private boolean autoScroll_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a TailPaneConfig.
     */
    public TailPaneConfig()
    {
        this(new String[0], // file
             true,          // autoscroll
             false,         // show linenumbers
             SwingUtil.getDefaultAntiAlias(), // antialias
             FontUtil.getPreferredMonoFont(), // font
             "",            // Regular exp
             "",            // Cut exp
             true);         // autostart
    }


    /**
     * Creates TailConfig with given parameters
     * 
     * @param files Files to tail.
     * @param autoScroll Turn on autoscroll.
     * @param showLineNumbers Shows line numbers in output.
     * @param antiAlias Antialias text in output area.
     * @param font Font of display text area.
     * @param regularExpression Optional filter (regular expression) for 
     *        weeding out junk.
     * @param cutExpression Optional expression for removing columns.
     * @param autoStart Autostarts tailing (starts it).
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
        setAutoTail(autoScroll);
        setShowLineNumbers(showLineNumbers);
        setAntiAliased(antiAlias);
        setFont(font);
        setRegularExpression(regularExpression);
        setCutExpression(cutExpression);
        setAutoStart(autoStart);
    }

    //--------------------------------------------------------------------------
    //  IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = prefs.getFirstChildElement(NODE_TAIL);
       
        setAutoTail(
            XOMUtil.getBooleanAttribute(
                root, ATTR_AUTOSCROLL, DEFAULT_AUTOTAIL));
        
        setShowLineNumbers(
            XOMUtil.getBooleanAttribute(
                root, ATTR_LINENUMBERS, DEFAULT_LINENUMBERS));
        
        setAntiAliased(
            XOMUtil.getBooleanAttribute(
                root, ATTR_ANTIALIAS, DEFAULT_ANTIALIAS));

        setAutoStart(
            XOMUtil.getBooleanAttribute(
                root, ATTR_AUTOSTART, DEFAULT_AUTOSTART));

        if (root != null)
        {
            List filenames = new ArrayList();
            Elements files = root.getChildElements(NODE_FILE);

            for (int i = 0; i < files.size(); i++)
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
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_TAIL);

        root.addAttribute(
            new Attribute(ATTR_LINENUMBERS, isShowLineNumbers() + ""));
        
        root.addAttribute(new Attribute(ATTR_AUTOSCROLL, isAutoTail() + ""));
        root.addAttribute(new Attribute(ATTR_ANTIALIAS, isAntiAliased() + ""));
        root.addAttribute(new Attribute(ATTR_AUTOSTART, isAutoStart() + ""));

        for (int i = 0; i < filenames_.length; i++)
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
     * @see java.lang.Object#toString()
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

    /**
     * @see toolbox.plugin.jtail.config.ITailPaneConfig#isAutoTail()
     */
    public boolean isAutoTail()
    {
        return autoScroll_;
    }

    
    /**
     * @see toolbox.plugin.jtail.config.ITailPaneConfig#getFilenames()
     */
    public String[] getFilenames()
    {
        return filenames_;
    }

    
    /**
     * @see toolbox.plugin.jtail.config.ITailPaneConfig#isShowLineNumbers()
     */
    public boolean isShowLineNumbers()
    {
        return showLineNumbers_;
    }
 
    
    /**
     * @see toolbox.plugin.jtail.config.ITailPaneConfig#setAutoTail(boolean)
     */
    public void setAutoTail(boolean autoScroll)
    {
        autoScroll_ = autoScroll;
    }

    
    /**
     * @see toolbox.plugin.jtail.config.ITailPaneConfig#setFilenames(
     *      java.lang.String[])
     */
    public void setFilenames(String[] filenames)
    {
        filenames_ = filenames;
    }

    
    /**
     * @see toolbox.plugin.jtail.config.ITailPaneConfig#setShowLineNumbers(boolean)
     */
    public void setShowLineNumbers(boolean showLineNumbers)
    {
        showLineNumbers_ = showLineNumbers;
    }

    
    /**
     * @see toolbox.plugin.jtail.config.ITailPaneConfig#getFont()
     */
    public Font getFont()
    {
        return font_;
    }

    
    /**
     * @see toolbox.plugin.jtail.config.ITailPaneConfig#setFont(java.awt.Font)
     */
    public void setFont(Font font)
    {
        font_ = font;
    }
 
    
    /**
     * @see toolbox.plugin.jtail.config.ITailPaneConfig#getRegularExpression()
     */
    public String getRegularExpression()
    {
        return regularExpression_;
    }

    
    /**
     * @see toolbox.plugin.jtail.config.ITailPaneConfig#setRegularExpression(
     *      java.lang.String)
     */
    public void setRegularExpression(String filter)
    {
        regularExpression_ = filter;
    }

    
    /**
     * @see toolbox.plugin.jtail.config.ITailPaneConfig#getCutExpression()
     */
    public String getCutExpression()
    {
        return cutExpression_;
    }

    
    /**
     * @see toolbox.plugin.jtail.config.ITailPaneConfig#setCutExpression(
     *      java.lang.String)
     */
    public void setCutExpression(String cutExpression)
    {
        cutExpression_ = cutExpression;
    }

    
    /**
     * @see toolbox.plugin.jtail.config.ITailPaneConfig#isAntiAliased()
     */
    public boolean isAntiAliased()
    {
        return antiAlias_;
    }

    
    /**
     * @see toolbox.plugin.jtail.config.ITailPaneConfig#setAntiAliased(boolean)
     */
    public void setAntiAliased(boolean b)
    {
        antiAlias_ = b;
    }
 
    
    /**
     * @see toolbox.plugin.jtail.config.ITailPaneConfig#isAutoStart()
     */
    public boolean isAutoStart()
    {
        return autoStart_;
    }

    
    /**
     * @see toolbox.plugin.jtail.config.ITailPaneConfig#setAutoStart(boolean)
     */
    public void setAutoStart(boolean autoStart)
    {
        autoStart_ = autoStart;
    }
}