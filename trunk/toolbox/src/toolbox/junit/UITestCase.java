package toolbox.junit;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import toolbox.util.ClassUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.plaf.LookAndFeelUtil;

/**
 * Base class UITestCase.
 */
public class UITestCase extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(UITestCase.class);
    
    //--------------------------------------------------------------------------
    // Constants 
    //--------------------------------------------------------------------------
    
	// Window/Frame/Dialog dimensions relative to the size of the desktop.
    public static final int SCREEN_PACK           = 0;
    public static final int SCREEN_FULL           = 1;
    public static final int SCREEN_ONE_HALF       = 2;
    public static final int SCREEN_ONE_THIRD      = 3;
    public static final int SCREEN_TWO_THIRDS     = 4;
    public static final int SCREEN_THREE_QUARTERS = 5;
    
    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    private JMenuBar menuBar_;

    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Protected constructor.
     */
    protected UITestCase()
    {
        try
        {
            LookAndFeelUtil.setPreferredLAF();
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the menuBar.
     *
     * @return JMenuBar
     */
    public JMenuBar getMenuBar()
    {
        return menuBar_;
    }

    /**
     * Sets the menuBar.
     *
     * @param menuBar The menuBar to set.
     */
    public void setMenuBar(JMenuBar menuBar)
    {
        menuBar_ = menuBar;
    }
    
    
    //--------------------------------------------------------------------------
    // Protected 
    //--------------------------------------------------------------------------
    
    /**
     * Launches test component in a separate frame.
     * 
     * @param c Component containing UI objects to test.
     * @return Newly created frame.
     */
    protected JFrame launchInFrame(JComponent c)
    {
        JFrame frame = new JFrame(ClassUtil.stripPackage(getClass().getName()));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(c, BorderLayout.CENTER);
        
        if (menuBar_ != null)
            frame.setJMenuBar(menuBar_);
        
        frame.pack();
        SwingUtil.centerWindow(frame);
        frame.setVisible(true);
        return frame;
    }
    
    
    /**
     * Launches test component in a modal dialog.
     * 
     * @param c Component containing UI objects to test.
     * @return Newly created dialog.
     */
    protected JDialog launchInDialog(JComponent c)
    {
        return launchInDialog(c, SCREEN_PACK);
    }

    
    /**
     * Launches test component in a modal dialog.
     * 
     * @param c Component containing UI objects to test.
     * @return Newly created dialog.
     */
    protected JDialog launchInDialog(JComponent c, int size)
    {
        JFrame parent = new JFrame();
        
        JDialog dlg = new JDialog(parent, 
            ClassUtil.stripPackage(getClass().getName()), true);
        
        dlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dlg.getContentPane().setLayout(new BorderLayout());
        dlg.getContentPane().add(c, BorderLayout.CENTER);
        
        if (menuBar_ != null)
            dlg.setJMenuBar(menuBar_);
        
        switch (size)
        {
            case SCREEN_FULL : 
                
                SwingUtil.setWindowSizeAsPercentage(dlg, 100, 100);
                break;
                
            case SCREEN_ONE_HALF :
                
                SwingUtil.setWindowSizeAsPercentage(dlg, 50, 50);
                break;
                
            case SCREEN_ONE_THIRD :
                
                SwingUtil.setWindowSizeAsPercentage(dlg, 33, 33);
                break;
                
            case SCREEN_TWO_THIRDS :
                
                SwingUtil.setWindowSizeAsPercentage(dlg, 66, 66);
                break;
                
            case SCREEN_THREE_QUARTERS :
                
                SwingUtil.setWindowSizeAsPercentage(dlg, 75, 75);
                break;
            
            case SCREEN_PACK :
                
                dlg.pack();
                break;
                
            default:
                
                throw new IllegalArgumentException(
                    "Screen size " + size + " is invalid.");
        }
        
        SwingUtil.centerWindow(dlg);
        dlg.setVisible(true);
        parent.dispose();
        return dlg;
    }
    
    
    protected void launchInPanel(JComponent c)
    {
    }
}