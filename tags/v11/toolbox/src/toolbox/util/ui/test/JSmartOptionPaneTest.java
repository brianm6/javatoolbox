package toolbox.util.ui.test;

import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import toolbox.util.ExceptionUtil;
import toolbox.util.SwingUtil;
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

    /**
     * Entry point
     * 
     * @param  args  None recognized
     */    
    public static void main(String[] args)
    {
        TestRunner.run(JSmartOptionPaneTest.class);
    }

    /**
     * Setup 
     */
    public void setUp()
    {
        MSG_DETAIL = ExceptionUtil.getStackTrace(
            new Exception("This is an exception"));        
    }

    /**
     * Constructor for DetailedDialogTest.
     * 
     * @param arg0  Name
     */
    public JSmartOptionPaneTest(String arg0)
    {
        super(arg0);
    }
    
    /**
     * Tests showMessageDialog() for defaults 
     */
    public void testShowDetailedMessageDialogDefault()
    {
        JSmartOptionPane.showDetailedMessageDialog(
            PARENT, MSG_TEXT, MSG_DETAIL);
    }
    
    /**
     * Tests showMessageDialog() for error msg type
     */ 
    public void testShowDetailedMessageDialogError() throws Exception
    {
        SwingUtil.setMotifLAF();
        JSmartOptionPane.showDetailedMessageDialog(
            PARENT, MSG_TEXT, MSG_DETAIL, MSG_TITLE, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Tests showMessageDialog() for variable paramteres
     */
    public void testShowDetailedMessageDialogParams() throws Exception
    {
        SwingUtil.setWindowsLAF();
        JSmartOptionPane.showDetailedMessageDialog(
            PARENT, "message text", MSG_DETAIL, "message title", 
                JOptionPane.WARNING_MESSAGE);    
    }
    
    /**
     * Tests showExceptionMessageDialog()
     */
    public void testShowExceptionMessageDialog()  throws Exception
    {
        SwingUtil.setMetalLAF();
        JSmartOptionPane.showExceptionMessageDialog(PARENT, 
            new Exception("testing"));
    }
}