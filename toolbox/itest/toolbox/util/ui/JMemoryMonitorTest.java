package toolbox.util.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import junit.textui.TestRunner;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;

/**
 * Unit test for {@link toolbox.util.ui.JMemoryMonitor}.
 */
public class JMemoryMonitorTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JMemoryMonitorTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
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
        cp.add(new JMemoryMonitor(), BorderLayout.CENTER);
        cp.add(new JSmartButton(new EatMemoryAction()), BorderLayout.SOUTH);
        cp.add(new JSmartButton(new GarbageCollectAction()), BorderLayout.NORTH);
        launchInDialog(cp);
    }
    
    //--------------------------------------------------------------------------
    // EatMemoryAction
    //--------------------------------------------------------------------------
    
    /**
     * Supposed to eat memory, but gets GC'ed. 
     */
    class EatMemoryAction extends SmartAction
    {
        /**
         * Creates an EatMemoryAction. 
         */
        EatMemoryAction()
        {
            super("Eat Memory", true, true, null);    
        }
        
        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e)
        {
            List list = new ArrayList();
            
            for (int i = 0; i < 200000; i++)
                list.add(RandomUtils.nextInt() + "");
        }
    }
    
    //--------------------------------------------------------------------------
    // GarbageCollectAction
    //--------------------------------------------------------------------------

    /**
     * Triggers garbage collection.
     */
    class GarbageCollectAction extends AbstractAction
    {
        /**
         * Creates a GarbageCollectAction.
         */
        GarbageCollectAction()
        {
            super("Run GC");
        }

        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            System.gc();
        }
    }
}