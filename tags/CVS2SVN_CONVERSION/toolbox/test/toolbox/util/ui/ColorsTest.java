package toolbox.util.ui;

import java.awt.Component;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JScrollPane;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.ui.list.JSmartList;
import toolbox.util.ui.list.SmartListCellRenderer;

/**
 * Unit test for {@link toolbox.util.ui.Colors}.
 */
public class ColorsTest extends UITestCase
{
    private static final Logger logger_ = Logger.getLogger(ColorsTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(ColorsTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testColors()
    {
        logger_.info("Running testColors...");
        
        Iterator i = Colors.iterator();
        Vector colors = new Vector();
        
        while (i.hasNext())
        {
            Colors.XColor c = (Colors.XColor) i.next();
            colors.add(c.getName());
        }
        
        JSmartList colorList = new JSmartList(colors);
        colorList.setCellRenderer(new ColorCellRenderer());
        launchInDialog(new JScrollPane(colorList));
    }
    
    //--------------------------------------------------------------------------
    // ColorCellRenderer
    //--------------------------------------------------------------------------
    
    class ColorCellRenderer extends SmartListCellRenderer 
    {
        public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus)
        {
            Component c = super.getListCellRendererComponent(list, value,
                index, isSelected, cellHasFocus);

            c.setBackground(Colors.getColor(value.toString()));
            return c;
        }
    }
}