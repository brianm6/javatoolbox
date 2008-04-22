package toolbox.util.ui.statusbar;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;

/**
 * Unit test for {@link toolbox.util.ui.statusbar.SimpleStatusBar}.
 */
public class SimpleStatusBarTest extends UITestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(SimpleStatusBarTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    public static void main(String[] args)
    {
        TestRunner.run(SimpleStatusBarTest.class);   
    }

    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------

    /**
     * Tests SimpleStatusBar.
     * 
     * @throws Exception on error.
     */    
    public void testSimpleStatusBar() throws Exception
    {
        logger_.info("Running testSimpleStatusBar...");
        
        SimpleStatusBar sb = new SimpleStatusBar();
        sb.setStatus("This is the status");
        JPanel p = new JPanel(new BorderLayout());
        p.add(sb, BorderLayout.SOUTH);
        p.add(createPropertySheet(sb), BorderLayout.CENTER);
        launchInDialog(p, SCREEN_ONE_HALF);
    }
}