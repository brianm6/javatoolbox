package toolbox.util.ui.font;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.Scenario;
import org.netbeans.jemmy.Test;
import org.netbeans.jemmy.TestCompletedException;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;

import toolbox.junit.UITestCase;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ThreadUtil;

/**
 * Unit test for JFontChooserDialog.
 */
public class JFontChooserDialogTest extends UITestCase implements Scenario
{
    private static final Logger logger_ = 
        Logger.getLogger(JFontChooserDialogTest.class);

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
//        LookAndFeelUtil.setPreferredLAF();
//        TestRunner.run(JFontChooserDialogTest.class);
        
        try
        {
            Test.main(new String[] { JFontChooserDialogTest.class.getName()});
        }
        catch (TestCompletedException tce)
        {
            logger_.info("Caught tce");
        }
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the JFontChooserDialog.
     */
    public void testJFontChooserDialog()
    {
        logger_.info("Running testJFontChooserDialog...");
        
        final JFontChooserDialog fsd = 
            new JFontChooserDialog(new JFrame(), "Select font", true);
            
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
    
    
    public int runIt(Object param) 
    {
        try {
            
            final JFontChooserDialog fsd = 
                new JFontChooserDialog(new JFrame(), "Select font", false);
                
            fsd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                
            fsd.addFontDialogListener(new IFontChooserDialogListener()
            {
                public void okButtonPressed(JFontChooser fontPanel)
                {
                    logger_.info("OK button pressed");
            //        fsd.dispose();
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
            
            //new JMenuBarOperator(mainFrame).pushMenuNoBlock("Tools|Properties", "|");
            
            JDialogOperator jdo = new JDialogOperator(fsd);
            logger_.info(jdo.getDump());
            
            JButtonOperator jbo = new JButtonOperator(jdo, "OK");
            
            ThreadUtil.sleep(3000);
            
                        
            jbo.clickMouse();

            //jdo.waitClosed();
            
            
            logger_.debug(StringUtil.banner("before delay"));
            
            ThreadUtil.sleep(5000);
            
            logger_.debug(StringUtil.banner("after delay"));
        } 
        catch(Exception e) 
        {
            logger_.error(e);
            //return(1);
        }
        
        return 0;
    }
}