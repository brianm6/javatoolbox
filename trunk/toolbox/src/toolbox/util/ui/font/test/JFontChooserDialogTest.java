package toolbox.util.ui.font.test;

import javax.swing.JFrame;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Category;
import toolbox.util.ui.font.JFontChooserDialog;
import toolbox.util.ui.font.JFontChooser;
import toolbox.util.ui.font.IFontChooserDialogListener;

/**
 * Unit test for JFontChooserDialog
 */
public class JFontChooserDialogTest extends TestCase
{
    private static final Category logger_ = 
        Category.getInstance(JFontChooserDialogTest.class);
        
    /**
     * Entry point
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(JFontChooserDialogTest.class);
    }

    /**
     * Constructor for JFontChooserDialogTest.
     * 
     * @param arg0 Name
     */
    public JFontChooserDialogTest(String arg0)
    {
        super(arg0);
    }

    /**
     * Test for void JFontChooser()
     */
    public void testFontSelectionPanel()
    {
        IFontChooserDialogListener fsdListener = new IFontChooserDialogListener()
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
            
        fsd.addFontDialogListener(fsdListener);
        
        fsd.setVisible(true);            
    }
}
