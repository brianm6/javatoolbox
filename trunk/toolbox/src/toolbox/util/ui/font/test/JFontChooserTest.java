package toolbox.util.ui.font.test;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

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
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(JFontChooserTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Test for void JFontChooser()
     */
    public void testFontSelectionPanel()
    {
        logger_.info("Running testFontSelectionPanel...");
        
        JFrame f = new JFrame();
        JFontChooser fsp = new JFontChooser();
        f.getContentPane().add(BorderLayout.CENTER, fsp);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }
}
