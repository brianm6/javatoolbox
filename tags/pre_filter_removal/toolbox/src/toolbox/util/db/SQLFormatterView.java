package toolbox.util.db;

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
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.StringUtil;
import toolbox.util.beans.BeanPropertyFilter;
import toolbox.util.collections.AsMap;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;

/**
 * SQLFormatterView is a UI component that allows for the configuration of a
 * SQLFormatter. The editable properties of the SQLFormatter are presented in
 * a property sheet. Updates to the properties are reflected in the SQLFormatter
 * immediately upon pressing the enter key for a given property. 
 * 
 * @see toolbox.util.db.SQLFormatter
 */
public class SQLFormatterView extends JHeaderPanel
{
    private static final Logger logger_ = 
        Logger.getLogger(SQLFormatterView.class);

    //--------------------------------------------------------------------------
    // Icons
    //--------------------------------------------------------------------------
    
    /**
     * Icon for header and flipper.
     */
    public static final Icon ICON =
        ImageCache.getIcon(ImageCache.IMAGE_BRACES);
        
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * SQLFormatter to configure.
     */
    private SQLFormatter formatter_;

    /**
     * Property sheet that displays the SQLFormatter's configurable properties.
     */
    private PropertySheetPanel sheet_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a SQLFormatterView.
     * 
     * @param formatter SQL formatter to configure.
     */
    public SQLFormatterView(SQLFormatter formatter)
    {
        super(ICON, "SQL Formatter Properties");
        buildView();
        setFormatter(formatter);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the SQL formatter and updates the property sheet.
     * 
     * @param formatter SQL formatter.
     */
    public void setFormatter(SQLFormatter formatter)
    {
        formatter_ = formatter;
        sheet_.readFromObject(formatter_);
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
            beanInfo = Introspector.getBeanInfo(SQLFormatter.class);
        }
        catch (IntrospectionException ie)
        {
            ExceptionUtil.handleUI(ie, logger_);
        }

        // Set the property editor for CapsMode
        PropertyEditorRegistry registry = new PropertyEditorRegistry();
        registry.registerEditor(CapsMode.class, CapsModePropertyEditor.class);

        // Build the list of properties to support
        PropertyDescriptor[] dArray = beanInfo.getPropertyDescriptors();
        List descriptors = new ArrayList();
        CollectionUtils.addAll(descriptors, dArray);
        
        // Filter out only the properties that the SQLFormatter saves as 
        // preferences.
        CollectionUtils.filter(
            descriptors, 
            new BeanPropertyFilter(
                "name", 
                SQLFormatter.SAVED_PROPS));
                
        dArray = (PropertyDescriptor[]) 
            descriptors.toArray(new PropertyDescriptor[0]);

        // DEBUG
        if (logger_.isDebugEnabled())
            for (int i = 0; i < dArray.length; i++)
                logger_.info(StringUtil.banner(AsMap.of(dArray[i]) + ""));

        // Create and init the property sheet
        sheet_ = new PropertySheetPanel();
        sheet_.setEditorRegistry(registry);
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
            p.writeToObject(formatter_);
        }
    }
}