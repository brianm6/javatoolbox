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

    /**
     * Constructor for JFontChooserTest.
     * 
     * @param arg0 Name
     */
    public JFontChooserTest(String arg0)
    {
        super(arg0);
    }

    /*
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

    /*
     * Test for void JFontChooser(Font)
     */
    public void testFontSelectionPanelFont()
    {
    }

    /*
     * Test for void JFontChooser(Font, String[], int[])
     */
    public void testFontSelectionPanelFontStringArrayIArray()
    {
    }

    public void testGetSelectedFontFamily()
    {
    }

    public void testGetSelectedFontStyle()
    {
    }

    public void testGetSelectedFontSize()
    {
    }

    public void testGetSelectedFont()
    {
    }

    public void testSetSelectedFont()
    {
    }

    public void testSetSelectedFontFamily()
    {
    }

    public void testSetSelectedFontStyle()
    {
    }

    public void testSetSelectedFontSize()
    {
    }
}
