package toolbox.plugin.texttools;

import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import nu.xom.Element;

import toolbox.util.formatter.HTMLFormatter;
import toolbox.util.formatter.XMLFormatter;
import toolbox.util.ui.BeanSheet;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.workspace.prefs.IConfigurator;

/**
 * Configures TextTool related preferences.
 */
public class TextToolsConfigurator extends JPanel implements IConfigurator
{
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
    private BeanSheet htmlSheet_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DBPrefsView.
     * 
     * @param plugin Query plugin.
     */
    public TextToolsConfigurator(TextToolsPlugin plugin)
    {
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
        setLayout(new GridLayout(2,1));
        
        xmlSheet_ = new BeanSheet(
            plugin_.getFormatterView().getXmlFormatter(),
            XMLFormatter.SAVED_PROPS);

        htmlSheet_ = new BeanSheet(
            plugin_.getFormatterView().getHtmlFormatter(),
            HTMLFormatter.SAVED_PROPS);

        add(new JHeaderPanel("XML Formatter", null, xmlSheet_));
        add(new JHeaderPanel("HTML Formatter", null, htmlSheet_));
    }

    //--------------------------------------------------------------------------
    // Preferences Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.prefs.IConfigurator#getLabel()
     */
    public String getLabel()
    {
        return "Text Tools";
    }


    /**
     * @see toolbox.workspace.prefs.IConfigurator#getView()
     */
    public JComponent getView()
    {
        return this;
    }

    
    /**
     * @see toolbox.workspace.prefs.IConfigurator#getIcon()
     */
    public Icon getIcon()
    {
        return ImageCache.getIcon(ImageCache.IMAGE_BRACES);
    }
    
    
    /**
     * @see toolbox.workspace.prefs.IConfigurator#onOK()
     */
    public void onOK()
    {
        onApply();
    }


    /**
     * @see toolbox.workspace.prefs.IConfigurator#onApply()
     */
    public void onApply()
    {
    }


    /**
     * @see toolbox.workspace.prefs.IConfigurator#onCancel()
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