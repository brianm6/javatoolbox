package toolbox.util.ui.font.test;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import junit.framework.TestCase;

import toolbox.util.ui.font.FontSelectionPanel;

/**
 * Unit test for FontSelectionPanel
 */
public class FontSelectionPanelTest extends TestCase
{
    /**
     * Entry point
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(FontSelectionPanelTest.class);
    }

    /**
     * Constructor for FontSelectionPanelTest.
     * 
     * @param arg0 Name
     */
    public FontSelectionPanelTest(String arg0)
    {
        super(arg0);
    }

    /*
     * Test for void FontSelectionPanel()
     */
    public void testFontSelectionPanel()
    {
        JFrame f = new JFrame();
        FontSelectionPanel fsp = new FontSelectionPanel();
        f.getContentPane().add(BorderLayout.CENTER, fsp);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }

    /*
     * Test for void FontSelectionPanel(Font)
     */
    public void testFontSelectionPanelFont()
    {
    }

    /*
     * Test for void FontSelectionPanel(Font, String[], int[])
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
