package toolbox.util.ui.font;

import java.awt.GridLayout;

import javax.swing.JPanel;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.FontUtil;

/**
 * Unit test for JFontChooser.
 */
public class JFontChooserTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JFontChooserTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entry point.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(JFontChooserTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Test for void JFontChooser().
     */
    public void testJFontChooser()
    {
        logger_.info("Running testJFontChooser...");
        
        JPanel p = new JPanel(new GridLayout(2,2));
        
        JFontChooser fc = null;
        
        fc = new JFontChooser(FontUtil.getPreferredMonoFont());
        p.add(fc);
        
        fc = new JFontChooser(FontUtil.getPreferredSerifFont());
        fc.setMonospaceEmphasized(true);
        p.add(fc);
        
        fc = new JFontChooser(FontUtil.getPreferredMonoFont());
        fc.setRenderedUsingFont(true);
        p.add(fc);
        
        fc = new JFontChooser(FontUtil.getPreferredSerifFont());
        fc.setRenderedUsingFont(true);
        fc.setMonospaceEmphasized(true);
        p.add(fc);
        
        launchInDialog(p);
    }
}