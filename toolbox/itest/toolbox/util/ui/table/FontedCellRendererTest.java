package toolbox.util.ui.table;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.FontUtil;
import toolbox.util.RandomUtil;

/**
 * Unit test for {@link toolbox.util.ui.table.FontedCellRenderer}.
 */
public class FontedCellRendererTest extends UITestCase {
    
    private static final Logger logger_ =
        Logger.getLogger(FontedCellRendererTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    public static void main(String[] args) {
        TestRunner.run(FontedCellRendererTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    public void testFontedCellRenderer() {
        logger_.info("Running testFontedCellRenderer ...");

        final DefaultTableModel model = new DefaultTableModel(0, 3);
        final JTable table = new JTable(model);
        
        // Install the left clipping cell renderer as the default
        table.setDefaultRenderer(
            Object.class, 
            new FontedCellRenderer(
                table.getDefaultRenderer(Object.class),
                FontUtil.getPreferredMonoFont()));
        
        table.setRowHeight((int) (table.getRowHeight() * 1.5));
        
        JPanel content = new JPanel(new BorderLayout());
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        
        content.add(BorderLayout.NORTH, 
            new JLabel("Cell font should be monospaced"));

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