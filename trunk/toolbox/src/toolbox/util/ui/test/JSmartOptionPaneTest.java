package toolbox.util.ui.test;

import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import toolbox.util.ExceptionUtil;
import toolbox.util.ui.JSmartOptionPane;

/**
 * Unit test for JSmartOptionPaneTest 
 */
public class JSmartOptionPaneTest extends TestCase
{
    private static final String MSG_TITLE = "JSmartOptionPane";
    
    private static final String MSG_TEXT = 
        "This is the test of the JOptionPane";
        
    private static String MSG_DETAIL;


    private static final JFrame PARENT = new JFrame();
    
    public static void main(String[] args)
    {
        TestRunner.run(JSmartOptionPaneTest.class);
    }

    public void setUp()
    {
        MSG_DETAIL = ExceptionUtil.getStackTrace(
            new Exception("This is an exception"));        
    }

    /**
     * Constructor for DetailedDialogTest.
     * @param arg0
     */
    public JSmartOptionPaneTest(String arg0)
    {
        super(arg0);
    }
    
    /**
     * Tests showMessageDialog() for defaults 
     */
    public void testShowMessageDialogDefault()
    {
        JSmartOptionPane.showDetailedMessageDialog(
            PARENT, MSG_TEXT, MSG_DETAIL);
    }
    
    /**
     * Tests showMessageDialog() for error msg type
     */ 
    public void testShowMessageDialogError()
    {
        JSmartOptionPane.showDetailedMessageDialog(
            PARENT, MSG_TEXT, MSG_DETAIL, MSG_TITLE, JOptionPane.ERROR_MESSAGE);
    }
}
