package toolbox.util.ui.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.UITestCase;
import toolbox.util.ui.JConveyorMenu;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartMenu;
import toolbox.util.ui.JSmartMenuItem;

/**
 * Unit test for JConveyorMenu.
 */
public class JConveyorMenuTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JConveyorMenuTest.class);

    private static int cnt = 1;
    
    private JMenu menu_;
    
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
        TestRunner.run(JConveyorMenuTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------
        
    /**
     * Makes sure menu behaves like a conveyor belt.
     */
    public void testJConveyorMenu()
    {
        logger_.info("Running testJConveyorMenu...");
        
        menu_ = new JConveyorMenu("Menu", 5);
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu_);
        menuBar.add(new JSmartMenu(
            "Menu Items should start dropping off after 5 items"));
        
        JPanel cp = new JPanel(new BorderLayout());
        JButton add = new JSmartButton(new AddItemAction());
        cp.add(BorderLayout.CENTER, add);
        
        setMenuBar(menuBar);
        launchInDialog(cp, SCREEN_ONE_THIRD);
    }
    
    //--------------------------------------------------------------------------
    // TestAction
    //--------------------------------------------------------------------------
    
    /**
     * TestAction
     */
    class TestAction extends AbstractAction
    {
        /**
         * Creates a test action. 
         */
        public TestAction()
        {
            super("Menu Item " + cnt++);
        }
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
        }
    }

    //--------------------------------------------------------------------------
    // AddItemAction
    //--------------------------------------------------------------------------
    
    /**
     * AddItemAction.
     */
    class AddItemAction extends AbstractAction
    {
        /**
         * Creates an AddItemAction. 
         */
        public AddItemAction()
        {
            super("Add Item to Menu");
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            menu_.add(new JSmartMenuItem(new TestAction()));
        }
    }
}