package toolbox.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.UITestCase;
import toolbox.util.ArrayUtil;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartMenu;
import toolbox.util.ui.JSmartMenuItem;
import toolbox.util.ui.plaf.LookAndFeelUtil;

/**
 * Unit test for SwingUtil.
 */
public class SwingUtilTest extends UITestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(SwingUtilTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Entry point.
     * 
     * @param args None recognized
     * @throws Exception on LAF error
     */
    public static void main(String[] args) throws Exception
    {
        LookAndFeelUtil.setPreferredLAF();
        TestRunner.run(SwingUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the tiling of a JDesktopPane.
     */
    public void testTile()
    {
        logger_.info("Running testTime...");
        
        TestFrame frame = new TestFrame();
        frame.setVisible(true);
    }
    
    
    /**
     * Tests getLAFs() to make sure all default and additional LAF's have been
     * installed correctly.
     */
    public void testGetLAFs()
    {
        logger_.info("Running testGetLAFs...");
        
        UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        logger_.debug(StringUtil.addBars(ArrayUtil.toString(lafs, true)));
    }
    
    //--------------------------------------------------------------------------
    // Helper Classes
    //--------------------------------------------------------------------------
        
    class TestFrame extends JFrame
    {
        private JDesktopPane desktop_;
        
        /**
         * Creates a TestFrame.
         */
        public TestFrame()
        {
            super("InternalFrameDemo");
            
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            // Make the big window be indented 50 pixels from each edge 
            // of the screen.
            int inset = 50;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            
            setBounds(
                inset,
                inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);
                
            // Quit this app when the big window closes.
            addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    //dispose();
                }
            });
            
            // Set up the GUI.
            desktop_ = new JDesktopPane(); //a specialized layered pane
            createFrame(); //Create first window
            setContentPane(desktop_);
            setJMenuBar(createMenuBar());
            
            // Make dragging faster:
            desktop_.putClientProperty("JDesktopPane.dragMode", "outline");
        }

        
        /**
         * Creates a menu bar.
         * 
         * @return Menu bar.
         */
        protected JMenuBar createMenuBar()
        {
            JMenuBar menuBar = new JMenuBar();
            JMenu menu = new JSmartMenu("Document");
            menu.setMnemonic(KeyEvent.VK_D);
            JMenuItem menuItem = new JSmartMenuItem("New");
            menuItem.setMnemonic(KeyEvent.VK_N);
            
            menuItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    createFrame();
                }
            });
            
            JMenuItem tileItem = new JSmartMenuItem("Tile");
            tileItem.setMnemonic(KeyEvent.VK_T);
            
            tileItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    SwingUtil.tile(desktop_);
                }
            });
            
            JMenuItem cascadeItem = new JSmartMenuItem("Cascade");
            cascadeItem.setMnemonic(KeyEvent.VK_C);
            
            cascadeItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    SwingUtil.cascade(desktop_);
                }
            });
            
            menu.add(menuItem);
            menu.add(tileItem);
            menu.add(cascadeItem);
            menuBar.add(menu);
            return menuBar;
        }
        
        
        /**
         * Creates a frame. 
         */
        protected void createFrame()
        {
            MyInternalFrame frame = new MyInternalFrame();
            frame.setVisible(true);
            desktop_.add(frame);
            
            try
            {
                frame.setSelected(true);
            }
            catch (PropertyVetoException e)
            {
                ; // Ignore
            }
        }
    }
}

class MyInternalFrame extends JInternalFrame
{
    private static int openFrameCount_ = 0;
    private static final int xOffset_ = 30, yOffset_ = 30;
    
    /**
     * Creates a MyInternalFrame.
     */
    public MyInternalFrame()
    {
        super("Document #" + (++openFrameCount_), true, true, true, true);
        setSize(300, 300);
        setLocation(xOffset_ * openFrameCount_, yOffset_ * openFrameCount_);
    }
}    