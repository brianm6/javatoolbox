package toolbox.util.ui.table;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.RandomUtil;

/**
 * Unit test for {@link toolbox.util.ui.table.LeftClippingCellRenderer}.
 */
public class LeftClippingCellRendererTest extends UITestCase {
    
    private static final Logger logger_ =
        Logger.getLogger(LeftClippingCellRendererTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    public static void main(String[] args) {
        TestRunner.run(LeftClippingCellRendererTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    public void testLeftClippingCellRenderer() {
        logger_.info("Running testLeftClippingCellRenderer ...");

        final DefaultTableModel model = new DefaultTableModel(0, 3);
        final JTable table = new JTable(model);
        
        // Install the left clipping cell renderer as the default
        table.setDefaultRenderer(
            Object.class, 
            new LeftClippingCellRenderer(new DefaultTableCellRenderer()));
        
        JPanel content = new JPanel(new BorderLayout());
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        content.add(BorderLayout.NORTH, 
            new JLabel("Shrinking the columns should left clip the cell text"));

        // Add some test data
        for (int i = 0, n = RandomUtil.nextInt(5, 15); i < n; i++) {
            model.addRow(new String[] {
                RandomUtil.nextString(20),
                RandomUtil.nextString(10),
                RandomUtil.nextString(5) });
        }

        launchInDialog(content);
    }
}