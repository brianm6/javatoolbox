package toolbox.util.ui.font.test;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.SwingUtil;
import toolbox.util.ui.font.IFontChooserDialogListener;
import toolbox.util.ui.font.JFontChooser;
import toolbox.util.ui.font.JFontChooserDialog;

/**
 * Unit test for JFontChooserDialog
 */
public class JFontChooserDialogTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(JFontChooserDialogTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
            
    /**
     * Entry point
     * 
     * @param  args  None recognized
     * @throws Exception on LAF error
     */
    public static void main(String[] args) throws Exception
    {
    	SwingUtil.setPreferredLAF();
        TestRunner.run(JFontChooserDialogTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Test for the font chooser dialog
     */
    public void testFontSelectionPanel()
    {
        IFontChooserDialogListener listener = new IFontChooserDialogListener()
        {
            public void okButtonPressed(JFontChooser fontPanel)
            {
                logger_.info("OK button pressed");                
            }

            public void cancelButtonPressed(JFontChooser fontPanel)
            {
                logger_.info("Cancel button pressed");                
            }

            public void applyButtonPressed(JFontChooser fontPanel)
            {
                logger_.info("Apply button pressed");
            }
        };
        
        JFontChooserDialog fsd = 
            new JFontChooserDialog(new JFrame(), "Select font", false);
            
        fsd.addFontDialogListener(listener);
        SwingUtil.centerWindow(fsd);
        fsd.setVisible(true);            
    }
}
