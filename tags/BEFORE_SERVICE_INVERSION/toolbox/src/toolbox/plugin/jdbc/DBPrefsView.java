package toolbox.plugin.jdbc;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.beans.BeanPropertyFilter;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;

/**
 * DBPrefsView allows the QueryPlugin preferences to be edited.
 * 
 * @see toolbox.plugin.jdbc.QueryPlugin 
 */
public class DBPrefsView extends JHeaderPanel
{
    private static final Logger logger_ = Logger.getLogger(DBPrefsView.class);

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
    private PropertySheetPanel sheet_;
    
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
        BeanInfo beanInfo = null;

        try
        {
            beanInfo = Introspector.getBeanInfo(QueryPlugin.class);
        }
        catch (IntrospectionException ie)
        {
            ExceptionUtil.handleUI(ie, logger_);
        }

        // Build the list of properties to support
        PropertyDescriptor[] dArray = beanInfo.getPropertyDescriptors();
        List descriptors = new ArrayList();
        CollectionUtils.addAll(descriptors, dArray);
        
        // Filter out only the properties that the QueryPlugin can save. 
        CollectionUtils.filter(
            descriptors, 
            new BeanPropertyFilter(
                "name", 
                QueryPlugin.SAVED_PROPS));
        
        dArray = (PropertyDescriptor[]) 
            descriptors.toArray(new PropertyDescriptor[0]);
        
        //if (logger_.isDebugEnabled())
        //    for (int i = 0; i < dArray.length; i++)
        //        logger_.info(StringUtil.banner(AsMap.of(dArray[i]) + ""));

        // Create and init the property sheet
        sheet_ = new PropertySheetPanel();
        sheet_.setProperties(dArray);
        sheet_.setDescriptionVisible(true);
        sheet_.addPropertySheetChangeListener(new MyPropertyChangeListener());
        
        setContent(sheet_);
    }

    //----------------------------------------------------------------------
    // MyPropertyChangeListener
    //----------------------------------------------------------------------

    /**
     * Writes the changed property back to the source javabean.
     */
    class MyPropertyChangeListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent evt)
        {
            Property p = (Property) evt.getSource();
            p.writeToObject(plugin_);
        }
    }
}