package toolbox.util.ui.test;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.UITestCase;
import toolbox.util.ThreadUtil;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.SmartAction;

/**
 * Unit test for SmartAction. 
 */
public class SmartActionTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(SmartActionTest.class);
        
    /** 
     * Time to simulate action execution doing work. 
     */        
    private int delay_ = 3000;
            
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(SmartActionTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * There are four scenarios to test the SmartAction for:
     * <ul>
     * <li>Synchronous call that completes successfully
     * <li>Synchronous call that results in an error (exception is thrown)
     * <li>Asynchronous call that completes successfully
     * <li>Asynchronous call that results in an error (exception is thrown)
     * </ul>
     * 
     * @throws Exception on error.
     */    
    public void testSmartAction() throws Exception
    {
        logger_.info("Running testSmartAction...");

        JPanel cp = new JPanel(new BorderLayout());
        cp.add(buildButtonPanel());
        launchInDialog(cp);
    }
    
    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------

    /**
     * Builds the GUI.
     */
    protected JPanel buildButtonPanel()
    {
        JPanel p = new JPanel(new GridLayout(4,4));
        
        p.add(new JSmartButton(new SyncTimedNoProbsAction(
            "Run synchronous timed action with no problems", false, p)));
            
        p.add(new JSmartButton(new SyncTimedThrowingAction(
            "Run synchronous timed action that throws an exception", false,p)));
            
        p.add(new JSmartButton(new AsyncTimedNoProbsAction(
            "Run asynchronous timed action with no problems", true, p)));
            
        p.add(new JSmartButton(new AsyncTimedThrowingAction(
            "Run asynchronous timed action that throws an exception", true,p)));
        return p;
    }
    
    //--------------------------------------------------------------------------
    // SyncTimedNoProbsAction
    //--------------------------------------------------------------------------
    
    class SyncTimedNoProbsAction extends SmartAction
    {
        public SyncTimedNoProbsAction(
            String name, boolean async, JComponent scope)
        {
            super(name, true, async, scope);
        }

        public void runAction(ActionEvent e)
        {
            logger_.info("Running synchronous timed action with no problems");
            ThreadUtil.sleep(delay_);
        }
    }

    //--------------------------------------------------------------------------
    // SyncTimedThrowingAction 
    //--------------------------------------------------------------------------
    
    class SyncTimedThrowingAction extends SmartAction
    {
        public SyncTimedThrowingAction(
            String name, boolean async, JComponent scope)
        {
            super(name, true, async, scope);
        }

        public void runAction(ActionEvent e)
        {
            logger_.info(
                "Running synchronous timed action that throws an exception");
                
            ThreadUtil.sleep(delay_);   
            throw new IllegalStateException("I'm a big mean sync exception!");
        }
    }

    //--------------------------------------------------------------------------
    // AsyncTimedNoProbsAction 
    //--------------------------------------------------------------------------
    
    class AsyncTimedNoProbsAction extends SmartAction
    {
        public AsyncTimedNoProbsAction(
            String name, boolean async, JComponent scope)
        {
            super(name, true, async, scope);
        }

        public void runAction(ActionEvent e)
        {
            logger_.info("Running asynchronous timed action with no problems");
            ThreadUtil.sleep(delay_);
        }
    }

    //--------------------------------------------------------------------------
    // AsyncTimedThrowingAction 
    //--------------------------------------------------------------------------
    
    class AsyncTimedThrowingAction extends SmartAction
    {
        public AsyncTimedThrowingAction(
            String name, boolean async, JComponent scope)
        {
            super(name, true, async, scope);
        }

        public void runAction(ActionEvent e)
        {
            logger_.info(
                "Running asynchronous timed action that throws an exception");
                
            ThreadUtil.sleep(delay_);   
            throw new IllegalStateException("I'm a big mean async exception!");
        }
    }
}