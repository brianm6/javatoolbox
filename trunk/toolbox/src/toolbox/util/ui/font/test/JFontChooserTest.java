package toolbox.util.ui.font.test;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import junit.framework.TestCase;

import toolbox.util.ui.font.JFontChooser;

/**
 * Unit test for JFontChooser
 */
public class JFontChooserTest extends TestCase
{
    /**
     * Entry point
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(JFontChooserTest.class);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for JFontChooserTest.
     * 
     * @param arg0 Name
     */
    public JFontChooserTest(String arg0)
    {
        super(arg0);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Test for void JFontChooser()
     */
    public void testFontSelectionPanel()
    {
        JFrame f = new JFrame();
        JFontChooser fsp = new JFontChooser();
        f.getContentPane().add(BorderLayout.CENTER, fsp);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }
}
