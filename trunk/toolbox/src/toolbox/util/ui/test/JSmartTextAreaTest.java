package toolbox.util.ui.test;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartTextArea;

/**
 * Unit test for JSmartTextArea
 */
public class JSmartTextAreaTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JSmartTextAreaTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /** 
     * Entry point
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(JSmartTextAreaTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------
    
    /**
     * Tests autoscroll feature
     */
    public void testAutoScroll()
    {
        logger_.info("Running testAutoScroll...");
        
        JFrame frame = new JFrame("testAutoScroll");        
        Container cp = frame.getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(new JScrollPane(new JSmartTextArea("hello")));
        frame.pack();
        frame.setVisible(true);
        SwingUtil.centerWindow(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
