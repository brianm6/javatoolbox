package toolbox.util.ui;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import junit.textui.TestRunner;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.FontUtil;
import toolbox.util.RandomUtil;

/**
 * Unit test for JSmartTextPane.
 */
public class JSmartTextPaneTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JSmartTextPaneTest.class);
        
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
        TestRunner.run(JSmartTextPaneTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------
    
    /**
     * Tests autoscroll feature.
     * 
     * @throws Exception on error.
     */
    public void testColors() throws Exception
    {
        logger_.info("Running testColors...");
        
        JSmartSplitPane splitter = 
            new JSmartSplitPane(SwingConstants.HORIZONTAL, true);
        
        JSmartTextPane tp = new JSmartTextPane();
        tp.setFont(FontUtil.getPreferredMonoFont());
        
        splitter.setTopComponent(
            new JHeaderPanel("Test Component", null, new JScrollPane(tp)));
        
        class ButtonPanel extends JPanel
        {
            public ButtonPanel()
            {
                setLayout(new FlowLayout());
            }
        }

        splitter.setBottomComponent(new ButtonPanel());
        splitter.setDividerLocation(200);
        
        
        for (int i = 0; i < 100; i++)
        {
            for (int j = 0; j < 10; j++)
                tp.append(RandomUtil.nextString(RandomUtil.nextInt(5,15)), nextColor(), nextColor());
                
            tp.append("\n", nextColor(), nextColor());
        }
        
        //tp.append("hello", Color.red, Color.white);
        //tp.append("world!", Color.green, Color.white);
         
        
        
        launchInDialog(splitter, UITestCase.SCREEN_ONE_HALF);
    }
    
    Color nextColor()
    {
        Color c = new Color(
            RandomUtils.nextInt(256), 
            RandomUtils.nextInt(256), 
            RandomUtils.nextInt(256)); 
        
        return c;
    }
}