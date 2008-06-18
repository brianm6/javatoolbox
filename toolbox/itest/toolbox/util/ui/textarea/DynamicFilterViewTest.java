package toolbox.util.ui.textarea;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.ui.table.JSmartTableHeaderTest;

public class DynamicFilterViewTest extends UITestCase {
	
    private static final Logger logger_ = Logger.getLogger(JSmartTableHeaderTest.class);

    // --------------------------------------------------------------------------
    // Main
    // --------------------------------------------------------------------------

    public static void main(String[] args) {
        TestRunner.run(DynamicFilterViewTest.class);
    }

    // --------------------------------------------------------------------------
    // Unit Tests
    // --------------------------------------------------------------------------

    public void testDynamicFilterView() {
        JPanel p = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea("hello world\na\n\b\nc");
        DynamicFilterView view = new DynamicFilterView(area);
        p.add(BorderLayout.NORTH, view);
        p.add(BorderLayout.CENTER, new JScrollPane(area));
        launchInDialog(p);
    }
}