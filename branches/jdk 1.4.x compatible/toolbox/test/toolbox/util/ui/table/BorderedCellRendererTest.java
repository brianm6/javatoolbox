package toolbox.util.ui.table;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.RandomUtil;

/**
 * Unit test for {@link toolbox.util.ui.table.BorderedCellRenderer}.
 */
public class BorderedCellRendererTest extends UITestCase {
    
    private static final Logger logger_ =
        Logger.getLogger(BorderedCellRendererTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    public static void main(String[] args) {
        TestRunner.run(BorderedCellRendererTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    public void testBorderedCellRenderer() {
        logger_.info("Running testBorderedCellRenderer ...");

        final DefaultTableModel model = new DefaultTableModel(0, 3);
        final JTable table = new JTable(model);
        
        // Install the left clipping cell renderer as the default
        table.setDefaultRenderer(
            Object.class, 
            new BorderedCellRenderer(
                new DefaultTableCellRenderer(),
                new CompoundBorder(
                    BorderFactory.createEmptyBorder(3,10,3,10),
                    BorderFactory.createLineBorder(Color.RED))));
        
        table.setRowHeight((int) (table.getRowHeight() * 1.5));
        
        JPanel content = new JPanel(new BorderLayout());
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        content.add(BorderLayout.NORTH, 
            new JLabel("Cells should have an inner red border"));

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