package toolbox.util.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.SwingUtil;

/**
 * Convenience class that adds a few creature comforts to the writing of 
 * Actions. Features include: <p>
 * <ul>
 * <li>Built in try/catch handler so you don't have to. All errors are displayed 
 *     via the GUI (including stack trace) and also logged via Log4J.
 * <li>Action can be specified to execute synchronously or asynchronously
 * <li>A wait cursor can automatically be applied to a top level JComponent and
 *     then is reset back to the default after the action has completed 
 *     execution
 * <li>Any <code>Action</code> can be arbitrarily inserted in the execution path
 *     at the following interception points: Before execution, during a failure, 
 *     or after execution.
 * </ul>  
 * <p>
 * Usage:
 * <pre>
 * 
 * public class MyAction extends SmartAction
 * {
 *     public MyAction()
 *     {
 *         super(
 *             "MyAction",   // Action name 
 *             true,         // Handle exceptions
 *             true,         // Run in separate thread
 *             panel);       // Set busy cursor on this component
 *     }
 * 
 *     public void runAction(ActionEvent e) throws Exception
 *     {
 *         // Action code goes here
 *         work.doWork();
 *     } 
 * }
 * 
 * </pre>
 */
public abstract class SmartAction extends AbstractAction
{
    private static final Logger logger_ = 
        Logger.getLogger(SmartAction.class);

    /** 
     * Surround the action with a try/catch block? 
     */
    private boolean tryCatch_;
    
    /** 
     * Execute action asynchronously? 
     */
    private boolean async_;
    
    /** 
     * Set the cursor to busy while action executing. 
     */
    private boolean busyCursor_;
    
    /** 
     * Root component on which to set the busy cursor on. 
     */
    private Component scope_;

    /** 
     * Caught exception if one was thrown. 
     */
    private Throwable caught_;
    
    /** 
     * List of actions to execute before this action. 
     */
    private List preActions_;
    
    /** 
     * List of actions to execute (regardless of failure) after this action. 
     */
    private List finallyActions_;
    
    /** 
     * List of actions to execute if an exception is thrown.
     */
    private List errorActions_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SmartAction with the given options.
     * 
     * @param name Name of action
     * @param tryCatch Set to true to surround the action with a try/catch block
     * @param async Set to tree to execute the action asynchronously
     * @param scope Component to set the busy cursor on (implies that you want 
     *        the busy cursor set). Pass in null to ignore.
     */
    public SmartAction(String name, boolean tryCatch, boolean async, 
        Component scope)
    {
        super(name);
        tryCatch_   = tryCatch;
        async_      = async;
        scope_      = scope;
        busyCursor_ = (scope_ != null);
        
        preActions_      = new ArrayList(2);
        finallyActions_  = new ArrayList(2);
        errorActions_    = new ArrayList(2);
        
        if (busyCursor_)
        {
            preActions_.add(new BusyCursorAction());
            finallyActions_.add(new DefaultCursorAction());
        }
        
        if (tryCatch_)
        {
            errorActions_.add(new HandleErrorAction());
        }
    } 
    
    //--------------------------------------------------------------------------
    // ActionListener Interface
    //--------------------------------------------------------------------------

    /**
     * Core of behavior is in this method. Provides interception points for
     * execution of arbitrary actions and also handles sync/async exection
     * of the action. Delegates to runAction() which subclass implement.
     * 
     * @param e ActionEvent
     */    
    public final void actionPerformed(final ActionEvent e)
    {
        Runnable r = new Runnable() {

            public void run()
            {
                try
                {
                    for (int i=0, n=preActions_.size(); i<n; i++)
                        ((Action) preActions_.get(i)).actionPerformed(e);

                    SmartAction.this.runAction(e);
                }
                catch (Throwable t)
                {
                    caught_ = t;

                    for (int i=0, n=errorActions_.size(); i<n; i++)
                        ((Action) errorActions_.get(i)).actionPerformed(e);
                }
                finally
                {
                    for (int i=0, n=finallyActions_.size(); i<n; i++)
                        ((Action) finallyActions_.get(i)).actionPerformed(e);
                }
            }
        };

        if (async_)
        {
            new Thread(r).start();
        }
        else
        {
            r.run();            
        }
    }

    //--------------------------------------------------------------------------
    // Abstract Methods 
    //--------------------------------------------------------------------------
        
    /**
     * Called by actionPerformed() as a delegate for subclasses to override
     * and implement their "actionPerformed" behavior.
     * 
     * @param e ActionEvent
     * @throws Exception on error
     */
    public abstract void runAction(ActionEvent e) throws Exception;
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Adds an action to be executed prior the exection of this action
     * 
     * @param action Action to execute before main action
     */
    public void addPreAction(Action action)
    {
        preActions_.add(action);
    }


    /**
     * Adds an action to be executed after the execution of the main action
     * regardless or an error condition or not.
     * 
     * @param action Action to execute after the main action
     */
    public void addFinallyAction(Action action)
    {
        finallyActions_.add(action);
    }
    
    
    /**
     * Adds an action to be executed in the event of an error condition (an
     * exception is thrown)
     * 
     * @param action Action to execute if an exception is thrown
     */
    public void addErrorAction(Action action)
    {
        errorActions_.add(action);
    }


    /**
     * Returns thrown exception
     * 
     * @return Exception that was thrown if an error occurred
     */
    public Throwable getCaught()
    {
        return caught_;
    }
    
    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Sets the busy cursor on the passed in JComponent.
     */
    class BusyCursorAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            SwingUtil.setWaitCursor(scope_);
        }
    }


    /**
     * Sets the default cursor on the passed in JComponent.
     */    
    class DefaultCursorAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            SwingUtil.setDefaultCursor(scope_);
        }
    }


    /**
     * Logs the exception and displays on the GUI.
     */
    class HandleErrorAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
           ExceptionUtil.handleUI(getCaught(), logger_);
        }
    }
}
