package toolbox.util.ui.font.test;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.font.JFontChooser;

/**
 * Unit test for JFontChooser
 */
public class JFontChooserTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JFontChooserTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entry point
     * 
     * @param args None recognized
     * @throws Exception on LAF error
     */
    public static void main(String[] args) throws Exception
    {
    	SwingUtil.setPreferredLAF();
        TestRunner.run(JFontChooserTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Test for void JFontChooser().
     */
    public void testFontSelectionPanel()
    {
        logger_.info("Running testFontSelectionPanel...");
        
        JDialog dialog = new JDialog(new JFrame(), "testJFontChooser", true);
        JFontChooser chooser = new JFontChooser();
        dialog.getContentPane().add(BorderLayout.CENTER, chooser);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.pack();
        SwingUtil.centerWindow(dialog);
        dialog.setVisible(true);
    }
}
