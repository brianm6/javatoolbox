package toolbox.util.ui.test;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.UITestCase;
import toolbox.util.ExceptionUtil;
import toolbox.util.RandomUtil;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartOptionPane;

/**
 * Unit test for JSmartOptionPane.
 */
public class JSmartOptionPaneTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JSmartOptionPaneTest.class);
        
    private static final String MSG_TITLE = "JSmartOptionPane";
    
    private static final String MSG_TEXT = 
        "This is the test of the JOptionPane";
        
    private static String MSG_DETAIL;

    private JFrame parent_;

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
        TestRunner.run(JSmartOptionPaneTest.class);
    }

    //--------------------------------------------------------------------------
    // Overrides TestCase
    //--------------------------------------------------------------------------
    
    /**
     * Creates the detail portion of the test message.
     * 
     * @throws Exception on error 
     */
    public void setUp() throws Exception
    {
        super.setUp();
        parent_ = new JFrame();
        MSG_DETAIL = ExceptionUtil.getStackTrace(
            new Exception("This is an exception"));        
    }
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        parent_.dispose();
        super.tearDown();
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests showMessageDialog() for defaults. 
     */
    public void testShowDetailedMessageDialogDefault()
    {
        logger_.info("Running testShowDetailedMessageDialogDefault...");
        
        JSmartOptionPane.showDetailedMessageDialog(
            parent_, MSG_TEXT, MSG_DETAIL);
    }
    
    
    /**
     * Tests showMessageDialog() for error msg type.
     * 
     * @throws Exception on error
     */ 
    public void testShowDetailedMessageDialogError() throws Exception
    {
        logger_.info("Running testShowDetailedMessageDialogError...");
        
        SwingUtil.setMotifLAF();
        JSmartOptionPane.showDetailedMessageDialog(
            parent_, MSG_TEXT, MSG_DETAIL, MSG_TITLE, JOptionPane.ERROR_MESSAGE);
    }
    
    
    /**
     * Tests showMessageDialog() for variable parameters.
     * 
     * @throws Exception on error
     */
    public void testShowDetailedMessageDialogParams() throws Exception
    {
        logger_.info("Running testShowDetailedMessageDialogParams...");
        
        SwingUtil.setWindowsLAF();
        JSmartOptionPane.showDetailedMessageDialog(
            parent_, "message text", MSG_DETAIL, "message title", 
                JOptionPane.WARNING_MESSAGE);    
    }
    
    
    /**
     * Tests showExceptionMessageDialog()
     * 
     * @throws Exception on error
     */
    public void testShowExceptionMessageDialog()  throws Exception
    {
        logger_.info("Running testShowExceptionMessageDialog...");
        
        SwingUtil.setMetalLAF();
        JSmartOptionPane.showExceptionMessageDialog(parent_, 
            new Exception("testing"));
    }
    
    
    /**
     * Tests for a detailed message with a large number of rows.
     */
    public void testShowDetailedMessageDialogLotsOfRows()
    {
        logger_.info("Running testShowExceptionMessageDialogLogsOfRows...");
        
        StringBuffer sb = new StringBuffer();
        
        for (int i=0; i<20000; i++)
            sb.append(RandomUtil.nextUpperAlpha());
            
        String msg = StringUtil.wrap(sb.toString(), 50, true);
        
        JSmartOptionPane.showDetailedMessageDialog(parent_, "yo yo yo!", msg);
    }
}