package toolbox.util.ui;

import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;

/**
 * Unit test for {@link toolbox.util.ui.CompoundIcon}.
 */
public class CompoundIconTest extends UITestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(CompoundIconTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    public static void main(String[] args)
    {
        TestRunner.run(CompoundIconTest.class);   
    }

    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------

    /**
     * Tests CompoundIcon.
     * 
     * @throws Exception on error.
     */    
    public void testCompoundIcon() throws Exception
    {
        logger_.info("Running testCompoundIcon...");
        
        JPanel p = new JPanel(new GridLayout(2, 1));

        Icon icon1 = ImageCache.getIcon(ImageCache.IMAGE_DUKE);
        Icon icon2 = ImageCache.getIcon(ImageCache.IMAGE_SPANNER);

        JLabel horiz = new JLabel( 
            "Duke should be in the left, wrench on the right",
            new CompoundIcon(icon1, icon2, SwingConstants.HORIZONTAL),
            SwingConstants.CENTER);
        
        JLabel vert  = new JLabel(
            "Duke should be on top, wrench on the bottom",
            new CompoundIcon(icon1, icon2, SwingConstants.VERTICAL),
            SwingConstants.CENTER);

        p.add(horiz);
        p.add(vert);
        
        launchInDialog(p);
    }
}