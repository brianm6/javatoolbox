package toolbox.util.ui.test;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartTextArea;

/**
 * Unit test for JSmartTextArea
 */
public class JSmartTextAreaTest extends TestCase
{
    /** 
     * Entry point
     */
    public static void main(String[] args)
    {
        TestRunner.run(JSmartTextAreaTest.class);
    }

    /**
     * Constructor for JSmartTextAreaTest.
     * @param arg0
     */
    public JSmartTextAreaTest(String arg0)
    {
        super(arg0);
    }
    
    /**
     * Tests autoscroll feature
     */
    public void testAutoScroll()
    {
        JFrame frame = new JFrame("testAutoScroll");        
        Container cp = frame.getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(new JScrollPane(new JSmartTextArea("hello")));
        frame.pack();
        frame.setVisible(true);
        SwingUtil.centerWindow(frame);
    }
}
