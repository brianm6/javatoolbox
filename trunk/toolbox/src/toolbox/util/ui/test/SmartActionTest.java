package toolbox.util.ui.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.SmartAction;

/**
 * Unit test for SmartAction 
 */
public class SmartActionTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(SmartActionTest.class);
        
    /** 
     * Time to simulate action execution doing work 
     */        
    private int delay_ = 3000;
            
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args) throws Exception
    {
        SwingUtil.setPreferredLAF();
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
     */    
    public void testSmartAction() throws Exception
    {
        logger_.info("Running testSmartAction...");

        JDialog dialog = new JDialog(new JFrame(), "testSmartAction", true);        
        Container cp = dialog.getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(buildButtonPanel());
        dialog.pack();
        SwingUtil.centerWindow(dialog);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
    
    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------
    
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
    // Inner Classes
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