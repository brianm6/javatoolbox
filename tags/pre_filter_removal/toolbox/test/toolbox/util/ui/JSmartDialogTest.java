package toolbox.util.ui;

import javax.swing.JButton;
import javax.swing.JFrame;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.SwingUtil;

/**
 * Unit test for {@link toolbox.util.ui.JSmartDialog}.
 */
public class JSmartDialogTest extends UITestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(JSmartDialogTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(JSmartDialogTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests to make sure the esc key dismisses the dialog box.
     */
    public void testJSmartDialog()
    {
        logger_.info("Running testJSmartDialog...");
        
        JFrame f = new JFrame();
        JSmartDialog dlg = new JSmartDialog(f, true);
        dlg.getContentPane().add(new JSmartLabel("Hit esc to exit!"));
        dlg.getContentPane().add(new JButton("Hit esc to exit"));
        dlg.pack();
        SwingUtil.centerWindow(dlg);
        dlg.setVisible(true);
        f.dispose();
    }
}
