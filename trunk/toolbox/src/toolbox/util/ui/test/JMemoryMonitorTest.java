package toolbox.util.ui.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.RandomUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JMemoryMonitor;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.SmartAction;

/**
 * Unit test for JMemoryMonitor
 */
public class JMemoryMonitorTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JMemoryMonitorTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /** 
     * Entry point
     * 
     * @param  args  None recognized
     * @throws Exception on error
     */
    public static void main(String[] args) throws Exception
    {
        SwingUtil.setPreferredLAF();
        TestRunner.run(JMemoryMonitorTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------
    
    /**
     * Tests the memory monitor
     */
    public void testMemoryMonitor()
    {
        logger_.info("Running testMemoryMonitor...");
        
        JDialog frame = new JDialog(new JFrame(), "testMemoryMonitor", true);
                
        Container cp = frame.getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(new JMemoryMonitor(), BorderLayout.CENTER);
        
        JButton button = new JSmartButton(new EatMemoryAction());
        cp.add(button, BorderLayout.SOUTH);        
        frame.setSize(300,75);
        SwingUtil.centerWindow(frame);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
    
    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Supposed to eat memory, but gets GC'ed 
     */
    class EatMemoryAction extends SmartAction
    {
        EatMemoryAction()
        {
            super("Eat Memory", true, true, null);    
        }
        
        public void runAction(ActionEvent e) throws Exception
        {
            for (int i=0; i<2000000; i++)
            {
                RandomUtil.nextInt();
            }
        }
    }
}
