package toolbox.util.ui.font;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JCheckBoxOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.util.NameComponentChooser;

import toolbox.junit.UITestCase;
import toolbox.util.ArrayUtil;
import toolbox.util.JemmyUtil;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;

/**
 * Unit test for JFontChooserDialog.
 */
public class JFontChooserDialogTest extends UITestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(JFontChooserDialogTest.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private JDialogOperator dialog_;
    private JButtonOperator applyButton_;
    private JButtonOperator cancelButton_;
    private JButtonOperator okButton_;

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
            
    /**
     * Entry point.
     * 
     * @param args None recognized.
     * @throws Exception on LAF error.
     */
    public static void main(String[] args) throws Exception
    {
        logger_.info(StringUtil.banner("Main " + ArrayUtil.toString(args)));
        TestRunner.run(suite());
    }

    
    /**
     * @return
     */
    public static Test suite()
    {
        logger_.info(StringUtil.banner("JFontChooserDialogTest.suite()"));
        TestSuite suite = new TestSuite(JFontChooserDialogTest.class);
        
        TestSetup wrapper = new TestSetup(suite)
        {
            protected void setUp() throws Exception
            {
                //setUpOnce();
            }

            protected void tearDown() throws Exception
            {
                //tearDownOnce();            
            }

        };
        
        return wrapper;
    }
    
    //==================================================================
    // Setup Once
    //------------------------------------------------------------------
    private static void setUpOnce()
    {
        logger_.info(StringUtil.banner("One time setup"));
        
        //JemmyProperties.setCurrentDispatchingModel(
        //    JemmyProperties.ROBOT_MODEL_MASK);

        //JemmyProperties.setCurrentOutput(TestOut.getNullOutput());
        
        final JFontChooserDialog fsd = 
            new JFontChooserDialog(new JFrame(), "Select font", false);
            
        fsd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
        fsd.addFontDialogListener(new IFontChooserDialogListener()
        {
            public void okButtonPressed(JFontChooser fontPanel)
            {
                logger_.info("OK button pressed");
                fsd.dispose();
            }

            public void cancelButtonPressed(JFontChooser fontPanel)
            {
                logger_.info("Cancel button pressed");
                fsd.dispose();                
            }

            public void applyButtonPressed(JFontChooser fontPanel)
            {
                logger_.info("Apply button pressed");
            }
        });

        SwingUtil.centerWindow(fsd);
        fsd.setVisible(true);
    }

    //==================================================================
    // Tear Down Once
    //------------------------------------------------------------------
    private static void tearDownOnce()
    {
        logger_.info(StringUtil.banner("One time tear down"));

        // Only dispose if the dialog hasn't already been dismissed by the
        // test case.
        if (JDialogOperator.findJDialog("Select font", true, false) != null)
        {
            JDialogOperator dialog = new JDialogOperator("Select font");
            JDialog dialogSource = (JDialog) dialog.getSource();
            dialogSource.setVisible(false);
            dialogSource.dispose();
        }
        
        //new QueueTool().waitEmpty(10000);
        //dialog.waitClosed();
        //QueueTool.uninstallQueue();
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        setUpOnce();
        
        dialog_ = new JDialogOperator("Select font");
        
        applyButton_ =
            JemmyUtil.findButton(dialog_, 
                JFontChooserDialog.NAME_APPLY_BUTTON);
        
        cancelButton_ = 
            JemmyUtil.findButton(dialog_, 
                JFontChooserDialog.NAME_CANCEL_BUTTON);
        
        okButton_ =
            JemmyUtil.findButton(dialog_, 
                JFontChooserDialog.NAME_OK_BUTTON);
    }
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        tearDownOnce();
        super.tearDown();
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the JFontChooserDialog.
     */
    public void testJFontChooserDialog()
    {
        logger_.info(StringUtil.banner("testJFontChooserDialog..."));
        logger_.info("Running testJFontChooserDialog...");

        //==================================================================
        // Font Size Field
        //------------------------------------------------------------------
        JTextFieldOperator sizeField = 
            new JTextFieldOperator(dialog_, 
                new NameComponentChooser(JFontChooser.NAME_SIZE_FIELD));
        
        sizeField.enterText("55");
        
        JListOperator fontList = 
            new JListOperator(dialog_, 
                new NameComponentChooser(JFontChooser.NAME_FONT_LIST));

        JListOperator styleList = 
            new JListOperator(dialog_, 
                new NameComponentChooser(JFontChooser.NAME_STYLE_LIST));

        JListOperator sizeList = 
            new JListOperator(dialog_, 
                new NameComponentChooser(JFontChooser.NAME_SIZE_LIST));

        JCheckBoxOperator antialiasCheckBox = 
            new JCheckBoxOperator(dialog_, 
                new NameComponentChooser(
                    JFontChooser.NAME_ANTIALIAS_CHECKBOX));

        JList fontListSource = (JList) fontList.getSource();
        JList styleListSource = (JList) styleList.getSource();
        JList sizeListSource = (JList) sizeList.getSource();
        
        int numStyles = styleListSource.getModel().getSize();
        int numSizes = sizeListSource.getModel().getSize();
        
        for (int i = 0; i < fontListSource.getModel().getSize(); i++)
        {
            //==============================================================
            // Font name
            //--------------------------------------------------------------
            fontList.selectItem(i);
            
            //==============================================================
            // Font style
            //--------------------------------------------------------------
            styleList.selectItem(RandomUtils.nextInt(numStyles));
            
            //==============================================================
            // Font Size List
            //--------------------------------------------------------------
            sizeList.selectItem(RandomUtils.nextInt(numSizes));
            
            //==============================================================
            // AntiAlias CheckBox
            //--------------------------------------------------------------
            antialiasCheckBox.clickMouse();
        }
    }
    
    
    /**
     * Tests the JFontChooserDialog.
     */
    public void testApplyButton()
    {
        logger_.info("Running testApplyButton...");
        applyButton_.clickMouse();
        assertTrue(dialog_.getSource().isVisible());
    }

    
    /**
     * Tests the JFontChooserDialog.
     */
    public void testCancelButton()
    {
        logger_.info("Running testCancelButton...");
        cancelButton_.clickMouse();
        assertTrue(!dialog_.getSource().isVisible());
    }

    
    /**
     * Tests the JFontChooserDialog.
     */
    public void testOKButton()
    {
        logger_.info("Running testOKButton...");
        okButton_.clickMouse();
        assertTrue(!dialog_.getSource().isVisible());
    }
}