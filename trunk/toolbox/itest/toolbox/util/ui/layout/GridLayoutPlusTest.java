package toolbox.util.ui.layout;
import javax.swing.JButton;
import javax.swing.JPanel;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.ui.JSmartButton;

/**
 * Unit test for GridLayoutPlus.
 * 
 * @see toolbox.util.ui.layout.GridLayoutPlus
 */
public class GridLayoutPlusTest extends UITestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(GridLayoutPlusTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(GridLayoutPlusTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests GridLayoutPlus. 
     */
    public void testGridLayoutPlus()
    {
        logger_.info("Running testGridLayoutPlus...");
        
        GridLayoutPlus glp = new GridLayoutPlus(0, 3, 10, 10);
        glp.setColWeight(1, 2);
        glp.setColWeight(2, 1);
        glp.setRowWeight(2, 1);
        JPanel p = new JPanel(glp);
        
        for (int r = 0; r < 6; r++)
            for (int c = 0; c < 3; c++)
                p.add(new JSmartButton(r + "," + c));
        
        launchInDialog(p);
    }

    
    /**
     * Tests BasicGridLayout. 
     */
    public void testBasicGridLayout()
    {
        logger_.info("Running testBasicGridLayout...");

        BasicGridLayout bgl = new BasicGridLayout(0, 3, 10, 10);
        bgl.setColWeight(1);
        bgl.setRowWeight(1);
        bgl.setIncludeInvisible(false);
        JPanel p = new JPanel(bgl);
        
        for (int r = 0; r < 6; r++)
        {
            for (int c = 0; c < 3; c++)
            {
                JButton b;
                p.add(b = new JSmartButton(r + "," + c));
                b.setVisible((r + c) % 4 != 0);
            }
        }
        
        launchInDialog(p);
    }
}
