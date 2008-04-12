package toolbox.util.ui;

import java.awt.FlowLayout;

import javax.swing.JFrame;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.SwingUtil;
import toolbox.util.ui.action.DisposeAction;

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
        dlg.getContentPane().setLayout(new FlowLayout());
        dlg.getContentPane().add(new JSmartButton("Hit esc to exit"));
        dlg.getContentPane().add(new JSmartLabel("Hit esc to exit!"));
        dlg.pack();
        SwingUtil.centerWindow(dlg);
        dlg.setVisible(true);
        f.dispose();
    }

    
    public void testRememberSizeAndPosition()
    {
        logger_.info("Running testRememberSizeAndPosition...");
        
        JFrame f = new JFrame();
        JSmartDialog dlg = new MockDialog(f,"Remember Size and Position",  true);
        dlg.setResizable(true);
        dlg.setSize(300,200);
        dlg.getContentPane().add(new JSmartLabel("Resize me and hit esc. I should come back the same size"));
        dlg.getContentPane().add(new JSmartButton(new DisposeAction("Nuke me", dlg)));
        dlg.pack();
        SwingUtil.centerWindow(dlg);
        dlg.setVisible(true);
        
        logger_.debug("Launching second window");
        
        // Reinstantiate same dialog box and make sure the new size was 
        // retained.
        
        dlg = new MockDialog(f, "This is the new dialog", true);
        dlg.setResizable(true);
        dlg.getContentPane().add(new JSmartLabel("I should be same size and pos"));
        dlg.getContentPane().add(new JSmartButton("Hit esc to exit"));
        //dlg.pack();
        dlg.setVisible(true);
        
        f.dispose();
    }
    
    
    class MockDialog extends JSmartDialog
    {
        public MockDialog(JFrame f, String string, boolean b)
        {
            super(f, string, b);
        }

        /**
         * @see java.awt.Component#getName()
         */
        public String getName()
        {
            return "MockDialog";
        }
    }
}
