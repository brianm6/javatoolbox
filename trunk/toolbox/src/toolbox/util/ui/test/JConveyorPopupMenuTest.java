package toolbox.util.ui.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JConveyorPopupMenu;
import toolbox.util.ui.JPopupListener;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartMenuItem;

/**
 * Unit test for JConveyorPopupMenu
 */
public class JConveyorPopupMenuTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JConveyorPopupMenuTest.class);

    static int cnt = 1;
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /** 
     * Entry point
     * 
     * @param args None recognized
     * @throws Exception on error
     */
    public static void main(String[] args) throws Exception
    {
        SwingUtil.setPreferredLAF();
        TestRunner.run(JConveyorPopupMenuTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------
        
    /**
     * Makes sure menu behaves like a conveyor belt
     */
    public void testJConveyorPopupMenu()
    {
        logger_.info("Running testJConveyorPopupMenu...");
        
        JDialog dialog=new JDialog(new JFrame(),"testJConveyorPopupMenu",true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        final JPopupMenu popupMenu = new JConveyorPopupMenu("Menu", 5);
        
        Container cp = dialog.getContentPane();
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
                super("Add Item to PopupMenu");
            }
                    
            public void actionPerformed(ActionEvent e)
            {
                popupMenu.add(new JSmartMenuItem(new DummyAction()));
            }
        
        }
        
        JButton add = new JSmartButton(new AddItemAction());
        add.addMouseListener(new JPopupListener(popupMenu));
        
        cp.add(BorderLayout.CENTER, add);
        dialog.pack();
        SwingUtil.centerWindow(dialog);
        dialog.setVisible(true);
    }
}
