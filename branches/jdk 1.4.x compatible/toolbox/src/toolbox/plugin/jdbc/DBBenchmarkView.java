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
 * DBBenchmarkView allows for configuration of the database benchmark.
 * 
 * @see toolbox.plugin.jdbc.DBBenchmark 
 */
public class DBBenchmarkView extends JHeaderPanel
{
    private static final Logger logger_ = 
        Logger.getLogger(DBBenchmarkView.class);

    //--------------------------------------------------------------------------
    // Icons
    //--------------------------------------------------------------------------
    
    /**
     * Icon for header and flipper.
     */
    public static final Icon ICON_DBBENCHMARK =
        ImageCache.getIcon(ImageCache.IMAGE_BAR_CHART);
    
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
        super(ICON_DBBENCHMARK, "Benchmark Properties");
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
        
        // Filter out only the properties that the DBBenchmark is able to save.
        CollectionUtils.filter(
            descriptors, 
            new BeanPropertyFilter(
                "name", 
                DBBenchmark.SAVED_PROPS));
        
        da = (PropertyDescriptor[]) 
            descriptors.toArray(new PropertyDescriptor[0]);

        // Create and init the property sheet
        sheet_ = new PropertySheetPanel();
        sheet_.setProperties(da);
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
            p.writeToObject(benchmark_);
        }
    }
}