package toolbox.util.ui.plaf.test;

import java.awt.FlowLayout;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import junit.textui.TestRunner;

import toolbox.junit.UITestCase;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartCheckBox;
import toolbox.util.ui.JSmartComboBox;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.list.JSmartList;
import toolbox.util.ui.plaf.LookAndFeelUtil;
import toolbox.util.ui.tree.JSmartTree;

/**
 * Unit test for LookAndFeelUtil.
 */
public class LookAndFeelUtilTest extends UITestCase
{
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(LookAndFeelUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Launches a frame with the LookAndFeel menu in the menu bar. Test by
     * activating the look and feels manually.
     */
    public void testCreateThemesMenu()
    {
        SwingUtil.setDefaultAntiAlias(true);
        
        JMenuBar mb = new JMenuBar();
        JMenu lafMenu = LookAndFeelUtil.createLookAndFeelMenu();
        mb.add(new JMenu("Testing"));
        mb.add(lafMenu);
        setMenuBar(mb);
        
        JPanel p = new JPanel(new FlowLayout());
        p.add(new JSmartButton("Button"));
        p.add(new JSmartTextField("TextField"));
        p.add(new JSmartCheckBox("CheckBox", true));
        p.add(new JSmartComboBox(new String[] {"Combo", "Box"}));
        p.add(new JSmartLabel("Label"));
        
        p.add(new JScrollPane(new JSmartList(
            new String[]{"List Item 1", "List Item 2", "List Item 3"})));
        
        p.add(new JScrollPane(new JTable(
            new String[][] { 
                new String[] {"Cell A", "Cell B"}, 
                new String[] {"Cell C", "Cell D"}
            }, 
            new String[] {"Column 1", "Column 2"})));
        
        p.add(new JScrollPane(new JSmartTextArea(
            "TextArea line 1\nTextArea line2\nTextArea line3")));
        
        p.add(new JScrollPane(new JSmartTree(
            new Object[] 
            {
                "Node 1", 
                new String[] {"Node 1.1", "Node 1.2"} , 
                "Node3"
            })));
        
        launchInFrame(p);
    }
}
