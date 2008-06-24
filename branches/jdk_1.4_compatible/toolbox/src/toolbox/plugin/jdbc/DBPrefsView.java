package toolbox.plugin.jdbc;

import javax.swing.Icon;

import toolbox.util.ui.BeanSheet;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;

/**
 * DBPrefsView allows the QueryPlugin preferences to be edited.
 * 
 * @see toolbox.plugin.jdbc.QueryPlugin 
 */
public class DBPrefsView extends JHeaderPanel
{
    //--------------------------------------------------------------------------
    // Icons
    //--------------------------------------------------------------------------
    
    /**
     * Icon for header and flipper.
     */
    public static final Icon ICON_DBPREFS =
        ImageCache.getIcon(ImageCache.IMAGE_CONFIG);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Property sheet panel.
     */
    private BeanSheet sheet_;
    
    /**
     * Query plugin.
     */
    private QueryPlugin plugin_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DBPrefsView.
     * 
     * @param plugin Query plugin.
     */
    public DBPrefsView(QueryPlugin plugin)
    {
        super(ICON_DBPREFS, "Plugin Properties");
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
     * @param plugin Query plugin.
     */
    public void setPlugin(QueryPlugin plugin)
    {
        plugin_ = plugin;
        sheet_.readFromObject(plugin);
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        sheet_ = 
            new BeanSheet(
                plugin_,
                QueryPluginConstants.SAVED_PROPS);
        
        setContent(sheet_);
    }
}