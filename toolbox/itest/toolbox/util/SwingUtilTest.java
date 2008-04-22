package toolbox.util;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartFrame;
import toolbox.util.ui.JSmartMenu;
import toolbox.util.ui.JSmartMenuItem;
import toolbox.util.ui.plaf.LookAndFeelUtil;

/**
 * Unit test for {@link toolbox.util.SwingUtil}.
 */
public class SwingUtilTest extends UITestCase
{
    private static final Logger logger_ = Logger.getLogger(SwingUtilTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
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
        logger_.info("Running testTile...");
        
        TestFrame frame = new TestFrame();
        frame.setVisible(true);
    }
    
    
    /**
     * Tests getLAFs() to make sure all default and additional LAF's have been
     * installed correctly.
     */
    public void testGetLAFs()
    {
        logger_.debug("Running testGetLAFs...");
        
        UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        logger_.debug(StringUtil.banner(ArrayUtil.toString(lafs, true)));
    }
    
    
    /**
     * Tests findInstancesOf() for nothing found. 
     * 
     * @throws Exception
     */
    public void testFindInstancesOf_ZeroFound() throws Exception
    {
        logger_.info("Running testFindInstancesOf_ZeroFound...");
        
        JButton b = new JButton("Groovy");
        List results = new ArrayList();
        SwingUtil.findInstancesOf(JLabel.class, b, results);
        assertTrue(results.isEmpty());
        
        JPanel p = new JPanel();
        p.add(new JPanel());
        p.add(new JLabel());
        p.add(new JButton());
        
        JPanel sub1 = new JPanel();
        sub1.add(new JCheckBox());
        sub1.add(new JMenuItem());
        p.add(sub1);
        
        List results2 = new ArrayList();
        SwingUtil.findInstancesOf(JTextField.class, b, results);
        assertTrue(results2.isEmpty());
    }
    

    /**
     * Tests findInstancesOf() for one instance found 
     * 
     * @throws Exception
     */
    public void testFindInstancesOf_OneFound() throws Exception
    {
        logger_.info("Running testFindInstancesOf_OneFound...");
        
        JButton b = new JButton("Groovy");
        List results = new ArrayList();
        SwingUtil.findInstancesOf(JButton.class, b, results);
        assertEquals(1, results.size());
        assertTrue(results.iterator().next() instanceof JButton);
        
        JPanel p = new JPanel();
        p.add(new JPanel());
        p.add(new JLabel());
        p.add(new JButton());
        
        JPanel sub1 = new JPanel();
        sub1.add(new JCheckBox());
        sub1.add(new JMenuItem());
        p.add(sub1);
        
        List results2 = new ArrayList();
        SwingUtil.findInstancesOf(JCheckBox.class, p, results2);
        assertEquals(1, results2.size());
        assertTrue(results2.iterator().next() instanceof JCheckBox);
    }
    

    
    /**
     * Tests findInstancesOf() for one instance found 
     * 
     * @throws Exception
     */
    public void testFindInstancesOf_SubclassFound() throws Exception
    {
        logger_.info("Running testFindInstancesOf_SubclassFound...");
        
        JPanel p = new JPanel();
        p.add(new JPanel());
        p.add(new JLabel());
        p.add(new JButton());
        
        JPanel sub1 = new JPanel();
        sub1.add(new JCheckBox());
        sub1.add(new JMenuItem());
        sub1.add(new JTextArea());
        p.add(sub1);
        
        List results2 = new ArrayList();
        SwingUtil.findInstancesOf(JTextComponent.class, p, results2);
        assertEquals(1, results2.size());
        assertTrue(results2.iterator().next() instanceof JTextComponent);
    }
    
    
    /**
     * Tests rotateImage() by rotating an image 360 degrees. 
     */
    public void testRotate() throws Exception
    {
        logger_.info("Running testRotate...");
        
        JPanel p = new JPanel(new GridLayout(18, 18));
        Image image = ImageCache.getImage(ImageCache.IMAGE_DUKE);
        
        while(image.getHeight(null) < 0);
        while(image.getWidth(null) < 0);
        
        for (int i = 0; i < 360; i++)
        {
            logger_.debug("Image " + i);
            Image rotated = SwingUtil.rotate(image, i);
            JButton b = new JSmartButton(i+"", new ImageIcon(rotated));
            p.add(b);
        }
            
        launchInDialog(p);
    }
    
    //--------------------------------------------------------------------------
    // Helper Classes
    //--------------------------------------------------------------------------
        
    class TestFrame extends JSmartFrame
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
