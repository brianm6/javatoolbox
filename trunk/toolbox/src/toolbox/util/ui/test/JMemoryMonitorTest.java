package toolbox.util.ui.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.RandomUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JMemoryMonitor;

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
        
        JFrame frame = new JFrame("testMemoryMonitor");
                
        Container cp = frame.getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(new JMemoryMonitor(), BorderLayout.CENTER);
        
        JButton button = new JButton(new EatMemoryAction());
        cp.add(button, BorderLayout.SOUTH);        
        frame.setSize(300,75);
        frame.setVisible(true);
        SwingUtil.centerWindow(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    class EatMemoryAction extends AbstractAction
    {
        public EatMemoryAction()
        {
            super("Eat Memory");    
        }
        
        public void actionPerformed(ActionEvent e)
        {
            for (int i=0; i<2000000; i++)
            {
                String s = RandomUtil.nextInt() + "";
            }
        }
    }
}
