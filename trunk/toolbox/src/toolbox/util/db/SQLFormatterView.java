package toolbox.util.db;

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
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.StringUtil;
import toolbox.util.collections.AsMap;

/**
 * SQLFormatterView is view onto a SQLFormatter allowing javabean properties 
 * to be changed via the user interface.
 * 
 * @see toolbox.util.db.SQLFormatter
 */
public class SQLFormatterView extends JPanel
{
    private static final Logger logger_ = 
        Logger.getLogger(SQLFormatterView.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * SQLFormatter that serves as the model for this view.
     */
    private SQLFormatter formatter_;

    /**
     * Property sheet panel.
     */
    private PropertySheetPanel sheet_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a SQLFormatterView.
     */
    public SQLFormatterView()
    {
        buildView();
    }

    
    /**
     * Creates a SQLFormatterView.
     * 
     * @param formatter SQL formatter.
     */
    public SQLFormatterView(SQLFormatter formatter)
    {
        buildView();
        setFormatter(formatter);
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    public void setFormatter(SQLFormatter formatter)
    {
        formatter_ = formatter;
        sheet_.readFromObject(formatter_);
    }
    
    
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
        
        // Remove unsupported properties
        CollectionUtils.filter(descriptors, new Predicate()
        {
            public boolean evaluate(Object object)
            {
                PropertyDescriptor pd = (PropertyDescriptor) object;
                return ArrayUtil.contains(
                    SQLFormatter.SAVED_PROPERTIES, pd.getName());
            }
        });

        dArray = (PropertyDescriptor[]) 
            descriptors.toArray(new PropertyDescriptor[0]);

        if (logger_.isDebugEnabled())
            for (int i = 0; i < dArray.length; i++)
                logger_.info(StringUtil.banner(AsMap.of(dArray[i]) + ""));

        // Create and init the property sheet
        sheet_ = new PropertySheetPanel();
        sheet_.setEditorRegistry(registry);
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
            //logger_.debug(
            //    "old = " + evt.getOldValue() 
            //    + " new = " + evt.getNewValue() 
            //    + " class= " + evt.getSource().getClass());

            Property p = (Property) evt.getSource();
            p.writeToObject(formatter_);

            //logger_.debug(StringUtil.banner(AsMap.of(formatter_).toString()));
        }
    }
}