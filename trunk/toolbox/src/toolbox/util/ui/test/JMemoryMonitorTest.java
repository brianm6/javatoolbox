package toolbox.util.ui.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.UITestCase;
import toolbox.util.RandomUtil;
import toolbox.util.ui.JMemoryMonitor;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.SmartAction;

/**
 * Unit test for JMemoryMonitor.
 */
public class JMemoryMonitorTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JMemoryMonitorTest.class);
        
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
        TestRunner.run(JMemoryMonitorTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------
    
    /**
     * Tests the memory monitor.
     */
    public void testMemoryMonitor()
    {
        logger_.info("Running testMemoryMonitor...");
        
        JPanel cp = new JPanel(new BorderLayout());
        JButton button = new JSmartButton(new EatMemoryAction());
        
        cp.add(new JMemoryMonitor(), BorderLayout.CENTER);
        cp.add(button, BorderLayout.SOUTH);        
        
        launchInDialog(cp);
    }
    
    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Supposed to eat memory, but gets GC'ed. 
     */
    class EatMemoryAction extends SmartAction
    {
        EatMemoryAction()
        {
            super("Eat Memory", true, true, null);    
        }
        
        public void runAction(ActionEvent e)
        {
            for (int i=0; i<2000000; i++)
            {
                RandomUtil.nextInt();
            }
        }
    }
}
