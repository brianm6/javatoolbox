package toolbox.plugin.texttools;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.formatter.HTMLFormatter;
import toolbox.util.formatter.XMLFormatter;
import toolbox.util.ui.BeanSheet;
import toolbox.util.ui.JHeaderPanel;
import toolbox.workspace.prefs.Preferences;

/**
 * Configures TextTool related preferences.
 */
public class TextToolsPreferences extends JHeaderPanel implements Preferences
{
    private static final Logger logger_ = 
        Logger.getLogger(TextToolsPreferences.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Query plugin.
     */
    private TextToolsPlugin plugin_;

    /**
     * Property sheet panel.
     */
    private BeanSheet xmlSheet_;

    /**
     * Property sheet panel.
     */
    private BeanSheet javaSheet_;

    /**
     * Property sheet panel.
     */
    private BeanSheet htmlSheet_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DBPrefsView.
     * 
     * @param plugin Query plugin.
     */
    public TextToolsPreferences(TextToolsPlugin plugin)
    {
        super("TextTools Properties");
        plugin_ = plugin;
        buildView();
        setPlugin(plugin);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the plugin and refreshes the property sheet.
     * 
     * @param plugin TextToolsPlugin.
     */
    public void setPlugin(TextToolsPlugin plugin)
    {
        plugin_ = plugin;
        xmlSheet_.readFromObject(plugin.getFormatterView().getXmlFormatter());
        htmlSheet_.readFromObject(plugin.getFormatterView().getHtmlFormatter());
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        JPanel p = new JPanel(new GridLayout(2,1));
        
        xmlSheet_ = new BeanSheet(
            plugin_.getFormatterView().getXmlFormatter(),
            XMLFormatter.SAVED_PROPS);

        htmlSheet_ = new BeanSheet(
            plugin_.getFormatterView().getHtmlFormatter(),
            HTMLFormatter.SAVED_PROPS);

        p.add(new JHeaderPanel("XML Formatter", null, xmlSheet_));
        p.add(new JHeaderPanel("HTML Formatter", null, htmlSheet_));
        setContent(p);
    }

    //--------------------------------------------------------------------------
    // Preferences Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.prefs.Preferences#getLabel()
     */
    public String getLabel()
    {
        return "Text Tools";
    }


    /**
     * @see toolbox.workspace.prefs.Preferences#getView()
     */
    public JComponent getView()
    {
        return this;
    }


    /**
     * @see toolbox.workspace.prefs.Preferences#onOK()
     */
    public void onOK()
    {
        onApply();
    }


    /**
     * @see toolbox.workspace.prefs.Preferences#onApply()
     */
    public void onApply()
    {
    }


    /**
     * @see toolbox.workspace.prefs.Preferences#onCancel()
     */
    public void onCancel()
    {
        // Nothing to do
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
    }
}