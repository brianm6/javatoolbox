package toolbox.plugin.texttools.format;

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

import org.w3c.tidy.Tidy;

import toolbox.util.ArrayUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.StringUtil;
import toolbox.util.collections.AsMap;

/**
 * HTMLFormatterView exposes select javabean properties of the Tidy HTML
 * formatter so that it can be configured via a user interface.
 * 
 * @see org.w3c.tidy.Tidy
 */
public class HTMLFormatterView extends JPanel
{
    private static final Logger logger_ = 
        Logger.getLogger(HTMLFormatterView.class);

    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Javabean properties that are editable.
     */
    public static final String[] SAVED_PROPERTIES = new String[] {
        "spaces",
        "tabsize",
        "smartIndent",
        "indentAttributes",
        "indentComments"
    };
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Tidy formatter with javabean accessible properties.
     */
    private Tidy formatter_;

    /**
     * Property sheet panel.
     */
    private PropertySheetPanel sheet_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a HTMLFormatterView.
     */
    public HTMLFormatterView()
    {
        buildView();
    }

    
    /**
     * Creates a HTMLFormatterView.
     * 
     * @param formatter SQL formatter.
     */
    public HTMLFormatterView(Tidy formatter)
    {
        buildView();
        setFormatter(formatter);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the HTML formatter and triggers population of the property sheet.
     * 
     * @param formatter Formatter
     */
    public void setFormatter(Tidy formatter)
    {
        formatter_ = formatter;
        sheet_.readFromObject(formatter_);
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Constructs the user interface.
     * 
     * TODO: This method is cut-n-pasted 3+ times!
     */
    protected void buildView()
    {
        BeanInfo beanInfo = null;

        try
        {
            beanInfo = Introspector.getBeanInfo(Tidy.class);
        }
        catch (IntrospectionException ie)
        {
            ExceptionUtil.handleUI(ie, logger_);
        }

        // Set the property editor for CapsMode
        //PropertyEditorRegistry registry = new PropertyEditorRegistry();
        //registry.registerEditor(CapsMode.class, CapsModePropertyEditor.class);

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
                return ArrayUtil.contains(SAVED_PROPERTIES, pd.getName());
            }
        });

        dArray = (PropertyDescriptor[]) 
            descriptors.toArray(new PropertyDescriptor[0]);

        if (logger_.isDebugEnabled())
            for (int i = 0; i < dArray.length; i++)
                logger_.info(StringUtil.banner(AsMap.of(dArray[i]) + ""));

        // Create and init the property sheet
        sheet_ = new PropertySheetPanel();
        //sheet_.setEditorRegistry(registry);
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
            // TODO: This class is cut-n-pasted 3+ times!
            Property p = (Property) evt.getSource();
            p.writeToObject(formatter_);
        }
    }
}