package toolbox.util.ui.font;

import javax.swing.JDialog;
import javax.swing.JFrame;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.UITestCase;
import toolbox.util.SwingUtil;

/**
 * Unit test for JFontChooserDialog.
 */
public class JFontChooserDialogTest extends UITestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(JFontChooserDialogTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
            
    /**
     * Entry point.
     * 
     * @param args None recognized.
     * @throws Exception on LAF error.
     */
    public static void main(String[] args) throws Exception
    {
        TestRunner.run(JFontChooserDialogTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the JFontChooserDialog.
     */
    public void testJFontChooserDialog()
    {
        logger_.info("Running testJFontChooserDialog...");
        
        final JFontChooserDialog fsd = 
            new JFontChooserDialog(new JFrame(), "Select font", false);
            
        fsd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
        fsd.addFontDialogListener(new IFontChooserDialogListener()
        {
            public void okButtonPressed(JFontChooser fontPanel)
            {
                logger_.info("OK button pressed");
                fsd.dispose();
            }

            public void cancelButtonPressed(JFontChooser fontPanel)
            {
                logger_.info("Cancel button pressed");
                fsd.dispose();                
            }

            public void applyButtonPressed(JFontChooser fontPanel)
            {
                logger_.info("Apply button pressed");
            }
        });

        SwingUtil.centerWindow(fsd);
        fsd.setVisible(true);
    }
}