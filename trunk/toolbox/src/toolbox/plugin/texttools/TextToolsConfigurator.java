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
import toolbox.workspace.PreferencedException;
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
    // IConfigurator Interface
    //--------------------------------------------------------------------------

    public String getLabel()
    {
        return "Text Tools";
    }


    public JComponent getView()
    {
        return this;
    }

    
    public Icon getIcon()
    {
        return ImageCache.getIcon(ImageCache.IMAGE_BRACES);
    }
    
    
    public void onOK()
    {
        onApply();
    }


    public void onApply()
    {
    }


    public void onCancel()
    {
        // Nothing to do
    }
    
    
    public boolean isApplyOnStartup()
    {
        return false;
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    public void applyPrefs(Element prefs) throws PreferencedException
    {
    }


    public void savePrefs(Element prefs) throws PreferencedException
    {
    }
}