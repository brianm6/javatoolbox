package toolbox.util.ui.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.UITestCase;
import toolbox.util.ui.JConveyorPopupMenu;
import toolbox.util.ui.JPopupListener;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartMenuItem;

/**
 * Unit test for JConveyorPopupMenu.
 */
public class JConveyorPopupMenuTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JConveyorPopupMenuTest.class);

    static int cnt = 1;
    private JPopupMenu popupMenu_;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /** 
     * Entry point.
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(JConveyorPopupMenuTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------
        
    /**
     * Makes sure menu behaves like a conveyor belt.
     */
    public void testJConveyorPopupMenu()
    {
        logger_.info("Running testJConveyorPopupMenu...");
        
        popupMenu_ = new JConveyorPopupMenu("Menu", 5);
        JPanel cp = new JPanel(new BorderLayout());
        JButton add = new JSmartButton(new AddItemAction());
        add.addMouseListener(new JPopupListener(popupMenu_));
        cp.add(BorderLayout.CENTER, add);
        launchInDialog(cp, SCREEN_ONE_THIRD);
    }

    //--------------------------------------------------------------------------
    // TestAction
    //--------------------------------------------------------------------------
    
    class TestAction extends AbstractAction
    {
        public TestAction()
        {
            super("Menu Item " + cnt++);
        }
        
        public void actionPerformed(ActionEvent e)
        {
        }
    }

    //--------------------------------------------------------------------------
    // AddItemAction
    //--------------------------------------------------------------------------
    
    class AddItemAction extends AbstractAction
    {
        public AddItemAction()
        {
            super("Add Item to PopupMenu");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            popupMenu_.add(new JSmartMenuItem(new TestAction()));
        }
    }
}