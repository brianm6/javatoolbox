package toolbox.util.ui.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JConveyorMenu;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartMenu;
import toolbox.util.ui.JSmartMenuItem;

/**
 * Unit test for JConveyorMenu.
 */
public class JConveyorMenuTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JConveyorMenuTest.class);

    private static int cnt = 1;
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /** 
     * Entry point.
     * 
     * @param args None recognized
     * @throws Exception on error
     */
    public static void main(String[] args) throws Exception
    {
        SwingUtil.setPreferredLAF();
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
        
        JDialog frame = new JDialog(new JFrame(), "testJConveyorMenu", true);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        final JMenu menu = new JConveyorMenu("Menu", 5);
        
        menuBar.add(menu);
        
        menuBar.add(new JSmartMenu(
            "Menu Items should start dropping off after 5 items"));
        
        Container cp = frame.getContentPane();
        cp.setLayout(new BorderLayout());

        class DummyAction extends AbstractAction
        {
            public DummyAction()
            {
                super("Menu Item " + cnt++);
            }
            
            public void actionPerformed(ActionEvent e)
            {
            }
        }

        class AddItemAction extends AbstractAction
        {
            public AddItemAction()
            {
                super("Add Item to Menu");
            }
                    
            public void actionPerformed(ActionEvent e)
            {
                menu.add(new JSmartMenuItem(new DummyAction()));
            }
        
        }
        
        JButton add = new JSmartButton(new AddItemAction());
        
        cp.add(BorderLayout.CENTER, add);
        frame.pack();
        SwingUtil.centerWindow(frame);
        frame.setVisible(true);
    }
}