package toolbox.util.ui;

import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.FontUtil;
import toolbox.util.RandomUtil;

/**
 * Unit test for {@link toolbox.util.ui.JSmartTextPane}.
 */
public class JSmartTextPaneTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JSmartTextPaneTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
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
        
        JSmartTextPane tp = new JSmartTextPane();
        tp.setFont(FontUtil.getPreferredMonoFont());
        
        class ButtonPanel extends JPanel
        {
            public ButtonPanel()
            {
                setLayout(new FlowLayout());
            }
        }
        
        for (int i = 0; i < 100; i++)
        {
            tp.setActiveForeground(RandomUtil.nextColor());
            tp.setActiveBackground(RandomUtil.nextColor());
            
            for (int j = 0; j < 10; j++)
                //tp.append(RandomUtil.nextString(RandomUtil.nextInt(5,15)), 
                //    RandomUtil.nextColor(), RandomUtil.nextColor());
                tp.append(RandomUtil.nextString(RandomUtil.nextInt(5,15)));
                
            tp.append("\n");
        }
        
        logger_.debug("launching..");
        
        //tp.append("hello", Color.red, Color.white);
        //tp.append("world!", Color.green, Color.white);
        
        launchInDialog(
            new JHeaderPanel("testing", null, new JScrollPane(tp)), 
            UITestCase.SCREEN_ONE_HALF);
    }
}