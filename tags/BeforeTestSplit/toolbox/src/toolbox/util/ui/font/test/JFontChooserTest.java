package toolbox.util.ui.font.test;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.UITestCase;
import toolbox.util.ui.font.JFontChooser;

/**
 * Unit test for JFontChooser.
 */
public class JFontChooserTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JFontChooserTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entry point.
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
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
        
        JFontChooser chooser = new JFontChooser();
        launchInDialog(chooser);
    }
}