package toolbox.util.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import junit.textui.TestRunner;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.ExceptionUtil;

/**
 * Unit test for JSmartOptionPane.
 */
public class JSmartFileChooserTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JSmartFileChooserTest.class);
        
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    private static final String MSG_TITLE = "JSmartOptionPane";
    private static final String MSG_TEXT = "This is the test of JOptionPane";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private static String MSG_DETAIL;

    /**
     * Parent frame.
     */    
    private JFrame parent_;

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(JSmartFileChooserTest.class);
    }

    //--------------------------------------------------------------------------
    // Overrides TestCase
    //--------------------------------------------------------------------------
    
    /**
     * Creates the detail portion of the test message.
     * 
     * @throws Exception on error. 
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
     *  
     */
    public void testPrefs()
    {
        logger_.info("Running testPrefs...");
        
        JPanel p = new JPanel(new FlowLayout());
        p.add(new JSmartButton(new SavePrefsAction()));
        p.add(new JSmartButton(new ApplyPrefsAction()));
        launchInDialog(p, UITestCase.SCREEN_ONE_THIRD);
    }
    
    Element chooserPrefs_;
    
    class SavePrefsAction extends AbstractAction
    {
        public SavePrefsAction()
        {
            super("Save Prefs");
        }
        
        
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                JSmartFileChooser sfc = new JSmartFileChooser();
                sfc.showSaveDialog((JComponent) e.getSource());
                
                chooserPrefs_ = new Element("saved");
                sfc.savePrefs(chooserPrefs_);
                
                logger_.debug(chooserPrefs_.toXML());
            }
            catch (Exception ex)
            {
                logger_.error("save prefs", ex);
            }
        }
    }
    
    class ApplyPrefsAction extends AbstractAction
    {
        public ApplyPrefsAction()
        {
            super("Apply Prefs");
        }
        
        
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                JSmartFileChooser sfc = new JSmartFileChooser();
                sfc.applyPrefs(chooserPrefs_);
                sfc.showSaveDialog((JComponent) e.getSource());
            }
            catch (Exception ex)
            {
                logger_.error("apply prefs", ex);
            }
        }
    }    
}