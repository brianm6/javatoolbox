package toolbox.workspace;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import toolbox.util.ExceptionUtil;
import toolbox.util.StringUtil;
import toolbox.util.ui.SmartAction;

/**
 * Extension of SmartAction that adds behavior to set the busy state of the
 * progress bar.
 */
public abstract class WorkspaceAction extends SmartAction
{
    /** 
     * Reference to the workspace status bar 
     */
    private IStatusBar statusBar_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a workspace action with exception handling turned on by default.
     * 
     * @param name Name of the action
     * @param async Set to true if the action is to be executed asynchronously.
     * @param scope Component that demarcates the scope of this action. This 
     *        scope is used to set the wait/default cursor accordingly. Set to 
     *        null if you do not want the busy cursor to be set.
     * @param statusBar Workspace status bar on with the progress bar busy
     *        state will be set. Set to null if you do not want the progress 
     *        bar to indicate the action is executing.
     */
    public WorkspaceAction(
        String name,
        boolean async,
        Component scope,
        IStatusBar statusBar)
    {
        this(name, true, async, scope, statusBar);
    }
    
    /**
     * Creates a workspace action.
     * 
     * @param name Name of the action
     * @param tryCatch Set to true if action is to be surrounded by a try/catch
     *        block with the accompanying error handler.
     * @param async Set to true if the action is to be executes asynchronously.
     * @param scope Component that demarcates the scope of this action. This 
     *        scope is used to set the wait/default cursor accordingly. Set to 
     *        null if you do not want the busy cursor to be set.
     * @param statusBar Workspace status bar on with the progress bar busy
     *        state will be set. Set to null if you do not want the progress 
     *        bar to indicate the action is executing.
     */
    public WorkspaceAction(
        String name,
        boolean tryCatch,
        boolean async,
        Component scope,
        IStatusBar statusBar)
    {
        super(name, tryCatch, async, scope);
        
        statusBar_ = statusBar;
        
        if (statusBar_ != null)
        {
            addPreAction(new BusyProgressBarAction());
            addFinallyAction(new DoneProgressBarAction());
        }

        // Always display error on status bar        
        addErrorAction(new DisplayErrorOnStatusBarAction());
    }

    //--------------------------------------------------------------------------
    // Actions
    //--------------------------------------------------------------------------
    
    /**
     * Sets the progress bar to busy 
     */
    class BusyProgressBarAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            statusBar_.setBusy(true);    
        }
    }

    /**
     * Sets the progress bar to not busy  
     */    
    class DoneProgressBarAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            statusBar_.setBusy(false);    
        }
    }

    /**
     * Displays error message on the status bar
     */    
    class DisplayErrorOnStatusBarAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            Throwable caught = getCaught();
            String message = caught.getMessage();
            
            if (StringUtil.isNullOrBlank(message))
            {
                message = 
                    StringUtil.getLine(ExceptionUtil.getStackTrace(caught),0);
            }
            
            statusBar_.setError("Error: " + message);    
        }
    }
}