package toolbox.util.db;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.UITestCase;
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
    
    class FormatterView extends JPanel 
    {
        private SQLFormatter formatter_;
        
        class PCL implements PropertyChangeListener 
        {
            /**
             * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
             */
            public void propertyChange(PropertyChangeEvent evt)
            {
                logger_.debug(
                    "old=" + evt.getOldValue() + " new=" + evt.getNewValue());
                
                DefaultProperty p = (DefaultProperty) evt.getSource();
                p.writeToObject(formatter_);
                
                logger_.debug(AsMap.of(formatter_).toString());
            }
        }
        
        
        public FormatterView(SQLFormatter formatter)
        {
            formatter_ = formatter;
            
            setLayout(new BorderLayout());
            //add(new JButton("Hello!"));
            
            DefaultProperty p = new DefaultProperty();
            p.setValue("I am the value");
            p.setDisplayName("Semirs Variable");
            p.setEditable(true);
            p.setName("semirsVariable");
            p.setShortDescription("Semirs short description");
            p.setType(String.class);
            
            PropertyChangeListener pcl = new PCL();
            
            DefaultProperty debug = new DefaultProperty();
            //debug.setValue(Boolean.valueOf(formatter_.isDebug()));
            debug.setDisplayName("Debug");
            debug.setEditable(true);
            debug.setName("debug");
            debug.setShortDescription("Toggles the debug flag");
            debug.setType(Boolean.class);
            debug.readFromObject(formatter_);
            debug.addPropertyChangeListener(pcl);

            DefaultProperty indent = new DefaultProperty();
            indent.setDisplayName("Number of spaces per indentation");
            indent.setEditable(true);
            indent.setName("indent");
            indent.setShortDescription("Number of spaces per indentation");
            indent.setType(Integer.class);
            indent.readFromObject(formatter_);
            indent.addPropertyChangeListener(pcl);

            DefaultProperty newLine = new DefaultProperty();
            newLine.setDisplayName("Newline character");
            newLine.setEditable(true);
            newLine.setName("newLine");
            newLine.setShortDescription("Newline character");
            newLine.setType(String.class);
            newLine.readFromObject(formatter_);
            newLine.addPropertyChangeListener(pcl);
            
            //PropertyDescriptor[] descriptors = 
            //    PropertyUtils.getPropertyDescriptors(formatter_);
            
            PropertySheetPanel psp = new PropertySheetPanel();
            psp.setProperties(new Property[] 
            {
                debug,
                indent,
                newLine
            });
            
            
            //psp.setProperties(descriptors);
            
            
            add(BorderLayout.CENTER, psp);
        }
    }
}
