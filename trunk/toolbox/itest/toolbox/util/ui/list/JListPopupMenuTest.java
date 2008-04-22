package toolbox.util.ui.list;

import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;

/**
 * Unit test for {@link toolbox.util.ui.list.JListPopupMenu}.
 */
public class JListPopupMenuTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JListPopupMenuTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(JListPopupMenuTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------
        
    /**
     * Tests the popupmenu.
     */
    public void testListPopup()
    {
        logger_.info("Running testListPopup...");
        
        JPanel cp = new JPanel(new BorderLayout());
        JList list = new JList(new String[] { 
            "right", "click", "on", "us", "to", "see", "the", "popup", "menu"});
        new JListPopupMenu(list);
        cp.add(new JScrollPane(list));
        launchInDialog(cp);
    }
}