package toolbox.util.ui;

import javax.swing.JButton;
import javax.swing.JFrame;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.SwingUtil;
import toolbox.util.ThreadUtil;

/**
 * Unit test for JSmartDialog.
 * 
 * @see toolbox.util.ui.JSmartDialog
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
    
    public void testJSmartDialog()
    {
        logger_.info("Running testJSmartDialog...");
        
        JSmartDialog dlg = new JSmartDialog(new JFrame(), false);
        dlg.getContentPane().add(new JSmartLabel("Hit esc to exit!"));
        dlg.getContentPane().add(new JButton("Hit esc to exit"));
        dlg.pack();
        SwingUtil.centerWindow(dlg);
        dlg.setVisible(true);
        
        ThreadUtil.sleep(10000);
    }

//    public void testJSmartDialog()
//    {
//        logger_.info("Running testJSmartDialog...");
//        
//        JSmartDialog dlg = new MySmartDialog();
//        dlg.pack();
//        SwingUtil.centerWindow(dlg);
//        dlg.setVisible(true);
//        
//        ThreadUtil.sleep(10000);
//    }
//
//    class MySmartDialog extends JSmartDialog
//    {
//        public MySmartDialog()
//        {
//            super(new JFrame(), true);
//            Container c = getContentPane();
//            c.setLayout(new FlowLayout());
//            c.add(new JButton("Hello"));
//            c.add(new JTextField("bye!"));
//        }
//    }
}
