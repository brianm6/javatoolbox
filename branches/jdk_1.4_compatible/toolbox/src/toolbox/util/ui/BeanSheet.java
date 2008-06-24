package toolbox.util.ui;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheet;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import toolbox.util.beans.BeanPropertyFilter;

/**
 * Property sheet for a Javabean. Shows javabean properties in a property sheet
 * and allows editing.
 */
public class BeanSheet extends PropertySheetPanel
{
    private static final Logger logger_ = Logger.getLogger(BeanSheet.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Javabean to display in this property sheet.
     */
    private Object bean_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a BeanSheet.
     * 
     * @param bean Java bean.
     * @param savedProps List of properties to popuate the sheet with.
     */
    public BeanSheet(Object bean, String[] savedProps)
    {
        bean_ = bean;
        BeanInfo beanInfo = null;

        try
        {
            beanInfo = Introspector.getBeanInfo(bean_.getClass());
            
            // Build the list of properties to support
            PropertyDescriptor[] dArray = beanInfo.getPropertyDescriptors();
            List descriptors = new ArrayList();
            CollectionUtils.addAll(descriptors, dArray);
            
            // Filter out only the properties that the bean class can save. 
            CollectionUtils.filter(
                descriptors, 
                new BeanPropertyFilter(
                    "name", 
                    savedProps));
            
            dArray = (PropertyDescriptor[]) 
                descriptors.toArray(new PropertyDescriptor[0]);
            
            //if (logger_.isDebugEnabled())
            //    for (int i = 0; i < dArray.length; i++)
            //        logger_.info(StringUtil.banner(AsMap.of(dArray[i]) + ""));

            // Create and init the property sheet
            setProperties(dArray);
            setDescriptionVisible(false);
            setToolBarVisible(false);
            setMode(PropertySheet.VIEW_AS_FLAT_LIST);
            addPropertySheetChangeListener(new MyPropertyChangeListener());
        }
        catch (IntrospectionException ie)
        {
            logger_.error("getBeanInfo", ie);
        }
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
            p.writeToObject(bean_);
        }
    }
}