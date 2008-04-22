package toolbox.util.ui.font;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;

import junit.textui.TestRunner;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JCheckBoxOperator;
import org.netbeans.jemmy.operators.JComponentOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.util.NameComponentChooser;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.ArrayUtil;
import toolbox.util.JemmyUtil;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ThreadUtil;

/**
 * Unit test for {@link toolbox.util.ui.font.JFontChooserDialog}.
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
    private JListOperator fontList_;
    private JListOperator styleList_;
    private JListOperator sizeList_;

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
            
    public static void main(String[] args) throws Exception
    {
        logger_.info(StringUtil.banner("Main " + ArrayUtil.toString(args)));
        TestRunner.run(JFontChooserDialogTest.class);
        System.exit(0); 
    }

    //--------------------------------------------------------------------------
    // Overrides TestCase
    //--------------------------------------------------------------------------
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        
        //JemmyProperties.setCurrentDispatchingModel(
        //    JemmyProperties.ROBOT_MODEL_MASK);

        JemmyProperties.setCurrentOutput(TestOut.getNullOutput());
        
        final JFontChooserDialog fsd = 
            new JFontChooserDialog(new JFrame(), "Select font", false);
            
        fsd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
        fsd.addFontDialogListener(new IFontChooserDialogListener()
        {
            public void okButtonPressed(JFontChooser fontPanel)
            {
                logger_.debug("OK button pressed");
                fsd.dispose();
            }

            public void cancelButtonPressed(JFontChooser fontPanel)
            {
                logger_.debug("Cancel button pressed");
                fsd.dispose();                
            }

            public void applyButtonPressed(JFontChooser fontPanel)
            {
                logger_.debug("Apply button pressed");
            }
        });

        SwingUtil.centerWindow(fsd);
        fsd.setVisible(true);
        
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
        
        fontList_ = new JListOperator(
            dialog_, new NameComponentChooser(JFontChooser.NAME_FONT_LIST));

        styleList_ = new JListOperator(
            dialog_, new NameComponentChooser(JFontChooser.NAME_STYLE_LIST));

        sizeList_ = new JListOperator(
            dialog_, new NameComponentChooser(JFontChooser.NAME_SIZE_LIST));
    }
   
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
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
        logger_.debug("Running testJFontChooserDialog...");

        //==================================================================
        // Font Size Field
        //------------------------------------------------------------------
        JTextFieldOperator sizeField = 
            new JTextFieldOperator(dialog_, 
                new NameComponentChooser(JFontChooser.NAME_SIZE_FIELD));
        
        sizeField.enterText("55");

        JCheckBoxOperator antialiasCheckBox = 
            new JCheckBoxOperator(dialog_, 
                new NameComponentChooser(
                    JFontChooser.NAME_ANTIALIAS_CHECKBOX));

        JList fontListSource = (JList) fontList_.getSource();
        JList styleListSource = (JList) styleList_.getSource();
        JList sizeListSource = (JList) sizeList_.getSource();
        
        int numStyles = styleListSource.getModel().getSize();
        int numSizes = sizeListSource.getModel().getSize();
        
        for (int i = 0; i < fontListSource.getModel().getSize(); i++)
        {
            //==============================================================
            // Font name
            //--------------------------------------------------------------
            fontList_.selectItem(i);
            
            //==============================================================
            // Font style
            //--------------------------------------------------------------
            styleList_.selectItem(RandomUtils.nextInt(numStyles));
            
            //==============================================================
            // Font Size List
            //--------------------------------------------------------------
            sizeList_.selectItem(RandomUtils.nextInt(numSizes));
            
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
    
    
    /**
     * Tests
     */
    public void testRenderUsingFont()
    {
        logger_.info("Running testRenderUsingFont...");
        
        JFontChooser chooser = (JFontChooser) 
            new JComponentOperator(dialog_, 
                new NameComponentChooser(
                    JFontChooser.NAME_FONT_CHOOSER)).getSource();
        
        chooser.setRenderedUsingFont(true);
        chooser.setMonospaceEmphasized(false);
        fontList_.repaint();
        ThreadUtil.sleep(3000);
    }
    
    /**
     * Tests
     */
    public void testRenderUsingMonospaceEmphasis()
    {
        logger_.info("Running testRenderUsingMonospaceEmphasis...");
        
        JFontChooser chooser = (JFontChooser) 
            new JComponentOperator(dialog_, 
                new NameComponentChooser(
                    JFontChooser.NAME_FONT_CHOOSER)).getSource();
        
        chooser.setRenderedUsingFont(false);
        chooser.setMonospaceEmphasized(true);
        fontList_.repaint();
        ThreadUtil.sleep(3000);
    }    
}