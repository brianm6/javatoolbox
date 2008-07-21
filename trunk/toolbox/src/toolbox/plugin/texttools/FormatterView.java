package toolbox.plugin.texttools;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;

import nu.xom.Element;

import toolbox.util.XOMUtil;
import toolbox.util.formatter.Formatter;
import toolbox.util.formatter.HTMLFormatter;
import toolbox.util.formatter.JavaFormatter;
import toolbox.util.formatter.XMLFormatter;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.SmartAction;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.PreferencedException;

/**
 * Flipper for formatting various text formats.
 */
public class FormatterView extends JPanel implements IPreferenced
{
    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------

    /**
     * Root node for preferences
     */
    private static final String NODE_FORMAT_VIEW = "FormatView";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Reference to the parent plugin.
     */
    private final TextToolsPlugin plugin_;

    /**
     * Formatter for xml documents.
     */
    private Formatter xmlFormatter_;
    
    /**
     * Formatter for java source code.
     */
    private Formatter javaFormatter_;
    
    /**
     * Formatter for html code.
     */
    private Formatter htmlFormatter_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    public FormatterView(TextToolsPlugin plugin)
    {
        buildView();
        plugin_ = plugin;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    public Formatter getHtmlFormatter()
    {
        return htmlFormatter_;
    }


    public Formatter getJavaFormatter()
    {
        return javaFormatter_;
    }


    public Formatter getXmlFormatter()
    {
        return xmlFormatter_;
    }    
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    protected void buildView()
    {
        setLayout(new FlowLayout());
        
        add(new JSmartButton(new FormatAction(
            "Format HTML", htmlFormatter_ = new HTMLFormatter())));
        
        add(new JSmartButton(new FormatAction(
            "Format XML", xmlFormatter_ = new XMLFormatter())));
        
        add(new JSmartButton(new FormatAction(
            "Format Java", javaFormatter_ = new JavaFormatter())));
    }

    //----------------------------------------------------------------------
    // FormatAction
    //----------------------------------------------------------------------

    class FormatAction extends SmartAction
    {
        private Formatter formatter_;
        
        FormatAction(String label, Formatter formatter)
        {
            super(label, true, false, null);
            formatter_ = formatter;
        }

        
        public void runAction(ActionEvent e) throws Exception
        {
            plugin_.getOutputArea().setText(
                formatter_.format(plugin_.getInputText()));
        }
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    public void applyPrefs(Element prefs) throws PreferencedException
    {
        Element root = XOMUtil.getFirstChildElement(prefs, NODE_FORMAT_VIEW,
            new Element(NODE_FORMAT_VIEW));
        
        xmlFormatter_.applyPrefs(root);
        htmlFormatter_.applyPrefs(root);
        javaFormatter_.applyPrefs(root);
    }


    public void savePrefs(Element prefs) throws PreferencedException
    {
        Element root = new Element(NODE_FORMAT_VIEW);

        xmlFormatter_.savePrefs(root);
        htmlFormatter_.savePrefs(root);
        javaFormatter_.savePrefs(root);

        XOMUtil.insertOrReplace(prefs, root);
    }
}