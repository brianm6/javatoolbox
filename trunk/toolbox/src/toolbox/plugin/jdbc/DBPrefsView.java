package toolbox.plugin.jdbc;

import java.awt.BorderLayout;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.StringUtil;
import toolbox.util.collections.AsMap;

/**
 * DBPrefsView allows the QueryPlugin preferences to be edited. 
 */
public class DBPrefsView extends JPanel
{
    private static final Logger logger_ = Logger.getLogger(DBPrefsView.class);

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
     * @param formatter SQL formatter.
     */
    public DBPrefsView(QueryPlugin plugin)
    {
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
        
        // Remove unsupported properties
        CollectionUtils.filter(descriptors, new Predicate()
        {
            public boolean evaluate(Object object)
            {
                PropertyDescriptor pd = (PropertyDescriptor) object;
                return ArrayUtil.contains(
                    QueryPlugin.SAVED_PROPS, pd.getName());
            }
        });

        dArray = (PropertyDescriptor[]) 
            descriptors.toArray(new PropertyDescriptor[0]);

        if (logger_.isDebugEnabled())
            for (int i = 0; i < dArray.length; i++)
                logger_.info(StringUtil.banner(AsMap.of(dArray[i]) + ""));

        // Create and init the property sheet
        sheet_ = new PropertySheetPanel();
        sheet_.setProperties(dArray);
        sheet_.addPropertySheetChangeListener(new MyPropertyChangeListener());
        
        // Build the GUI
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, sheet_);
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