package toolbox.util.ui.list.test;

import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.UITestCase;
import toolbox.util.ui.list.JListPopupMenu;

/**
 * Unit test for JListPopupMenu.
 */
public class JListPopupMenuTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JListPopupMenuTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /** 
     * Entry point.
     * 
     * @param args None
     */
    public static void main(String[] args)
    {
        TestRunner.run(JListPopupMenuTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------
        
    /**
     * Tests popupmenu.
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