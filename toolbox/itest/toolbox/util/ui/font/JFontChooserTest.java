package toolbox.util.ui.font;

import java.awt.GridLayout;

import javax.swing.JPanel;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.FontUtil;
import toolbox.util.ui.JHeaderPanel;

/**
 * Unit test for {@link toolbox.util.ui.font.JFontChooser}.
 */
public class JFontChooserTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JFontChooserTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(JFontChooserTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Renders the JFontChooser using the default, monospace emphasis, and
     * render with font settings.
     */
    public void testJFontChooser()
    {
        logger_.info("Running testJFontChooser...");
        
        JPanel p = new JPanel(new GridLayout(2, 3));
        
        JFontChooser fc = null;
        
        fc = new JFontChooser(FontUtil.getPreferredMonoFont());
        p.add(new JHeaderPanel("Default", null, fc));
        
        fc = new JFontChooser(FontUtil.getPreferredSerifFont());
        fc.setMonospaceEmphasized(true);
        p.add(new JHeaderPanel("Monospace On", null, fc));
        
        fc = new JFontChooser(FontUtil.getPreferredMonoFont());
        fc.setRenderedUsingFont(true);
        p.add(new JHeaderPanel("Render w/ Font On", null, fc));
        
        fc = new JFontChooser(FontUtil.getPreferredSerifFont());
        fc.setRenderedUsingFont(true);
        fc.setMonospaceEmphasized(true);
        p.add(new JHeaderPanel("Monospace and Render w/ Font On", null, fc));
        
        fc = new JFontChooser(
            FontUtil.getPreferredMonoFont(), 
            JFontChooser.DEFAULT_STYLES, 
            JFontChooser.DEFAULT_SIZES, 
            true, 
            true);
        
        //fc.setRenderedUsingFont(true);
        fc.setMonospaceEmphasized(true);
        p.add(new JHeaderPanel("Render w/ Monospaced only", null, fc));
        
        launchInDialog(p);
    }
}