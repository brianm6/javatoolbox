package toolbox.util.ui.table;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.RandomUtil;

/**
 * Unit test for {@link toolbox.util.ui.table.JSmartTableHeader}.
 */
public class JSmartTableHeaderTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JSmartTableHeaderTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    public static void main(String[] args)
    {
        TestRunner.run(JSmartTableHeaderTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests JSmartTableHeader.
     */
    public void testJSmartTableHeader()
    {
        logger_.info("Running testJSmartTableHeader...");

        final DefaultTableModel model = new DefaultTableModel(0, 3);
        final JTable table = new JTable(model);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        
        JSmartTableHeader header = 
            new JSmartTableHeader(table.getColumnModel(), table);
        
        table.setTableHeader(header);
        model.addRow(new String[]{"double click", "on the", "column dividers"});
        
        for (int i = 0; i < 15;  i++)
        {
            model.addRow(
                new String[]
                {
                    RandomUtil.nextString(RandomUtil.nextInt(5,15)) + "",
                    RandomUtil.nextString(RandomUtil.nextInt(1,10)) + "",
                    RandomUtil.nextString(RandomUtil.nextInt(10,15)) + ""
                });
        }
        
        launchInDialog(new JScrollPane(table));
    }
}