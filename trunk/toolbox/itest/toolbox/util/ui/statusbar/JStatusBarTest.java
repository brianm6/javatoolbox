package toolbox.util.ui.statusbar;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.RandomUtil;
import toolbox.util.ui.JSmartLabel;

/**
 * Unit test for {@link toolbox.util.ui.statusbar.JStatusBar}.
 */
public class JStatusBarTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JStatusBarTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    public static void main(String[] args)
    {
        TestRunner.run(JStatusBarTest.class);   
    }

    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------

    /**
     * Tests JStatusBar.
     * 
     * @throws Exception on error.
     */    
    public void testJStatusBar() throws Exception
    {
        logger_.info("Running testJStatusBar...");
        
        JStatusBar status = new JStatusBar();

        // Relative, twice normal
        status.addStatusComponent(
            new JSmartLabel(" Relative x2 "), JStatusBar.RELATIVE, 2);
        
        // Relative normal
        status.addStatusComponent(
            new JSmartLabel(" Relative x1 "), JStatusBar.RELATIVE, 1);
        
        // Fixed, based on preferred size
        status.addStatusComponent(
            new JSmartLabel(" Preferred "));
        
        // Fixed, based on specified size
        status.addStatusComponent(
            new JSmartLabel(" Fixed "), JStatusBar.FIXED, 40);
        
        // Relative normal
        status.addStatusComponent(
            new JSmartLabel(" Relative x1 "), JStatusBar.RELATIVE);
        
        // Progress bar is based on preferred size
        JProgressBar progressBar = new JProgressBar(1, 100);
        progressBar.setValue(RandomUtil.nextInt(1, 100));
        status.addStatusComponent(progressBar);

        JPanel p = new JPanel(new BorderLayout());
        p.add(BorderLayout.SOUTH, status);
        p.add(createPropertySheet(progressBar), BorderLayout.NORTH);
        launchInDialog(p, SCREEN_ONE_HALF);
    }
}