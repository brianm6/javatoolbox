package toolbox.util.ui.font.test;

import javax.swing.JFrame;
import junit.framework.TestCase;

import org.apache.log4j.Category;
import toolbox.util.ui.font.FontSelectionDialog;
import toolbox.util.ui.font.FontSelectionPane;
import toolbox.util.ui.font.IFontDialogListener;

/**
 * Unit test for FontSelectionDialog
 */
public class FontSelectionDialogTest extends TestCase
{
    private static final Category logger_ = 
        Category.getInstance(FontSelectionDialogTest.class);
        
    /**
     * Entry point
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(FontSelectionDialogTest.class);
    }

    /**
     * Constructor for FontSelectionDialogTest.
     * 
     * @param arg0 Name
     */
    public FontSelectionDialogTest(String arg0)
    {
        super(arg0);
    }

    /**
     * Test for void FontSelectionPane()
     */
    public void testFontSelectionPanel()
    {
        IFontDialogListener fsdListener = new IFontDialogListener()
        {
            public void okButtonPressed(FontSelectionPane fontPanel)
            {
                logger_.info("OK button pressed");                
            }

            public void cancelButtonPressed(FontSelectionPane fontPanel)
            {
                logger_.info("Cancel button pressed");                
            }

            public void applyButtonPressed(FontSelectionPane fontPanel)
            {
                logger_.info("Apply button pressed");
            }
        };
        
        FontSelectionDialog fsd = 
            new FontSelectionDialog(new JFrame(), "Select font", false);
            
        fsd.addFontDialogListener(fsdListener);
        
        fsd.setVisible(true);            
    }
}
