package toolbox.util.ui.test;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JListPopupMenu;

/**
 * Unit test for JListPopupMenuTest
 */
public class JListPopupMenuTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JListPopupMenuTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /** 
     * Entry point
     * 
     * @param  args  None
     */
    public static void main(String[] args)
    {
        TestRunner.run(JListPopupMenuTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------
        
    /**
     * Tests popupmenu
     */
    public void testListPopup()
    {
        logger_.info("Running testListPopup...");
        
        JFrame frame = new JFrame("testListPopup");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
        Container cp = frame.getContentPane();
        cp.setLayout(new BorderLayout());
        
        JList list = new JList(new String[] { "one", "two", "three", "four"});
        new JListPopupMenu(list);
        
        cp.add(new JScrollPane(list));
        frame.pack();
        SwingUtil.centerWindow(frame);
        frame.setVisible(true);
    }
}