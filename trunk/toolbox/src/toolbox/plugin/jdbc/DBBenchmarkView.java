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

/**
 * DBBenchmarkView allows for configuration of the database benchmark.
 * 
 * @see toolbox.plugin.jdbc.DBBenchmark 
 */
public class DBBenchmarkView extends JPanel
{
    private static final Logger logger_ = 
        Logger.getLogger(DBBenchmarkView.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Property sheet panel.
     */
    private PropertySheetPanel sheet_;
    
    /**
     * Database benchmark which this view will configure.
     */
    private DBBenchmark benchmark_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DBBenchmarkView.
     * 
     * @param benchmark Database benchmark to configure.
     */
    public DBBenchmarkView(DBBenchmark benchmark)
    {
        buildView();
        setBenchmark(benchmark);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the benchmark and refreshes the property sheet.
     * 
     * @param benchmark Query benchmark.
     */
    public void setBenchmark(DBBenchmark benchmark)
    {
        benchmark_ = benchmark;
        sheet_.readFromObject(benchmark);
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
            beanInfo = Introspector.getBeanInfo(DBBenchmark.class);
        }
        catch (IntrospectionException ie)
        {
            ExceptionUtil.handleUI(ie, logger_);
        }

        // Build the list of properties to support
        PropertyDescriptor[] da = beanInfo.getPropertyDescriptors();
        List descriptors = new ArrayList();
        CollectionUtils.addAll(descriptors, da);
        
        // Remove unsupported properties
        CollectionUtils.filter(descriptors, new Predicate()
        {
            public boolean evaluate(Object object)
            {
                PropertyDescriptor pd = (PropertyDescriptor) object;
                return ArrayUtil.contains(
                    DBBenchmark.SAVED_PROPS, pd.getName());
            }
        });

        da = (PropertyDescriptor[]) 
            descriptors.toArray(new PropertyDescriptor[0]);

//        if (logger_.isDebugEnabled())
//            for (int i = 0; i < dArray.length; i++)
//                logger_.info(StringUtil.banner(AsMap.of(dArray[i]) + ""));

        // Create and init the property sheet
        sheet_ = new PropertySheetPanel();
        sheet_.setProperties(da);
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
            p.writeToObject(benchmark_);
        }
    }
}