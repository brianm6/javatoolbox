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

import junit.textui.TestRunner;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import toolbox.junit.UITestCase;
import toolbox.util.StringUtil;
import toolbox.util.collections.AsMap;

/**
 * SQLFormatterPrefsView is responsible for _____.
 */
public class SQLFormatterPrefsView extends UITestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(SQLFormatterPrefsView.class);
    
    public static void main(String[] args)
    {
        TestRunner.run(SQLFormatterPrefsView.class);
    }
    
    
    public void testView() throws Exception
    {
        launchInFrame(new FormatterView(new SQLFormatter()));
    }

    //--------------------------------------------------------------------------
    // FormatterView
    //--------------------------------------------------------------------------
    
    class FormatterView extends JPanel 
    {
        private SQLFormatter formatter_;
        
        public FormatterView(SQLFormatter formatter)
        {
            formatter_ = formatter;
            
            setLayout(new BorderLayout());
            
            PropertySheetPanel sheet = new PropertySheetPanel();
            BeanInfo beanInfo = null;
            
            try
            {
                beanInfo = Introspector.getBeanInfo(SQLFormatter.class);
            }
            catch (IntrospectionException e)
            {
                logger_.error(e);
            }
            
            PropertyDescriptor[] descArray = beanInfo.getPropertyDescriptors();
            
//            for (int i = 0; i < descArray.length; i++)
//                logger_.info(StringUtil.banner(AsMap.of(descArray[i]) + ""));
            
            List desc;
            CollectionUtils.addAll(desc = new ArrayList(), descArray);

            // Remove the class property descriptor
            // =================================================================
            
            CollectionUtils.filter(desc, new Predicate()
            {
                public boolean evaluate(Object object)
                {
                    PropertyDescriptor pd = (PropertyDescriptor) object;
                    return !pd.getName().equals("class");
                }
            });
            
            // Set the combobox property editor for CapsModes
            // =================================================================
            
            PropertyEditorRegistry registry = new PropertyEditorRegistry();
            registry.registerEditor(CapsMode.class, CapsModePropertyEditor.class);
            sheet.setEditorRegistry(registry);
            
            // =================================================================

            descArray = (PropertyDescriptor[]) 
                desc.toArray(new PropertyDescriptor[0]);

            for (int i = 0; i < descArray.length; i++)
                logger_.info(StringUtil.banner(AsMap.of(descArray[i]) + ""));
            
            sheet.setProperties(descArray);
            
            add(BorderLayout.CENTER, sheet);
            sheet.readFromObject(formatter_);
            sheet.addPropertySheetChangeListener(new PCL());
        }

        //----------------------------------------------------------------------
        // PCL
        //----------------------------------------------------------------------
        
        class PCL implements PropertyChangeListener 
        {
            public void propertyChange(PropertyChangeEvent evt)
            {
                logger_.debug(
                    "old=" + evt.getOldValue() 
                    + " new=" + evt.getNewValue()
                    + " class= " + evt.getSource().getClass());
                
                Property p = (Property) evt.getSource();
                
                p.writeToObject(formatter_);
                
                logger_.debug(StringUtil.banner(AsMap.of(formatter_).toString()));
            }
        }
        
        
        
    }
}


//Collection capsModeProps = CollectionUtils.select(desc,
//new Predicate()
//{
//  public boolean evaluate(Object object)
//  {
//      PropertyDescriptor pd = (PropertyDescriptor) object;
//      return pd.getPropertyType() == CapsMode.class;
//  }
//});
//
//logger_.info("Caps modes = " + capsModeProps.size());
//
//
//for (Iterator iter = capsModeProps.iterator(); iter.hasNext();)
//{
//PropertyDescriptor p = (PropertyDescriptor) iter.next();
//p.setPropertyEditorClass(CapsModePropertyEditor.class);
//}


//            psp.setProperties(new Property[] 
//            {
//                debug,
//                indent,
//                newLine
//            });

//DefaultProperty p = new DefaultProperty();
//p.setValue("I am the value");
//p.setDisplayName("Semirs Variable");
//p.setEditable(true);
//p.setName("semirsVariable");
//p.setShortDescription("Semirs short description");
//p.setType(String.class);
//
//PropertyChangeListener pcl = new PCL();
//
//DefaultProperty debug = new DefaultProperty();
////debug.setValue(Boolean.valueOf(formatter_.isDebug()));
//debug.setDisplayName("Debug");
//debug.setEditable(true);
//debug.setName("debug");
//debug.setShortDescription("Toggles the debug flag");
//debug.setType(Boolean.class);
//debug.readFromObject(formatter_);
//debug.addPropertyChangeListener(pcl);
//
//DefaultProperty indent = new DefaultProperty();
//indent.setDisplayName("Number of spaces per indentation");
//indent.setEditable(true);
//indent.setName("indent");
//indent.setShortDescription("Number of spaces per indentation");
//indent.setType(Integer.class);
//indent.readFromObject(formatter_);
//indent.addPropertyChangeListener(pcl);
//
//DefaultProperty newLine = new DefaultProperty();
//newLine.setDisplayName("Newline character");
//newLine.setEditable(true);
//newLine.setName("newLine");
//newLine.setShortDescription("Newline character");
//newLine.setType(String.class);
//newLine.readFromObject(formatter_);
//newLine.addPropertyChangeListener(pcl);

//PropertyDescriptor[] descriptors = 
//    PropertyUtils.getPropertyDescriptors(formatter_);

//logger_.info(mockInfo.getBeanDescriptor());
//logger_.info(ArrayUtil.toString(mockInfo.getAdditionalBeanInfo()));
//logger_.info(ArrayUtil.toString(mockInfo.getEventSetDescriptors()));
//logger_.info(ArrayUtil.toString(mockInfo.getMethodDescriptors()));
//logger_.info(ArrayUtil.toString(mockInfo.getPropertyDescriptors()));
//logger_.info(mockInfo.getPropertyDescriptorCount()+"");
