package toolbox.junit;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import toolbox.util.ClassUtil;
import toolbox.util.SwingUtil;

/**
 * Base class UITestCase.
 */
public class UITestCase extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(UITestCase.class);
    
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
            SwingUtil.setPreferredLAF();
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
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
        JFrame parent = new JFrame();
        
        JDialog dlg = new JDialog(parent, 
            ClassUtil.stripPackage(getClass().getName()), true);
        
        dlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dlg.getContentPane().setLayout(new BorderLayout());
        dlg.getContentPane().add(c, BorderLayout.CENTER);
        dlg.pack();
        SwingUtil.centerWindow(dlg);
        dlg.setVisible(true);
        parent.dispose();
        return dlg;
    }
    
    
    protected void launchInPanel(JComponent c)
    {
    }
}