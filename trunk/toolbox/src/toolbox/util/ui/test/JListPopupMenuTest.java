package toolbox.util.ui.test;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JListPopupMenu;

/**
 * Unit test for JSmartTextArea
 */
public class JListPopupMenuTest extends TestCase
{
    /** 
     * Entry point
     * 
     * @param  args  None
     */
    public static void main(String[] args)
    {
        TestRunner.run(JListPopupMenuTest.class);
    }

    /**
     * Constructor for JSmartTextAreaTest.
     * 
     * @param arg0  Name
     */
    public JListPopupMenuTest(String arg0)
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
        
        JList list = new JList(new String[] { "one", "two", "three", "four"});
        JListPopupMenu menu = new JListPopupMenu(list);
        
        cp.add(new JScrollPane(list));
        frame.pack();
        frame.setVisible(true);
        SwingUtil.centerWindow(frame);
    }
}
