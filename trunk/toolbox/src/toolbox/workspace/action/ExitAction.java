package toolbox.workspace.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.workspace.PluginWorkspace;

/**
 * Exits the appication.
 */
public class ExitAction extends BaseAction
{
    private static final Logger logger_ = Logger.getLogger(ExitAction.class);

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ExitAction.
     * 
     * @param workspace Plugin workspace.
     */
    public ExitAction(PluginWorkspace workspace)
    {
        super(workspace);
        putValue(Action.NAME, PluginWorkspace.LABEL_EXIT_MENUITEM);
        putValue(Action.MNEMONIC_KEY, new Integer('X'));
    }


    /**
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ae)
    {
        try
        {
            (new SavePreferencesAction(getWorkspace())).runAction();
        }
        catch (Exception e)
        {
            ExceptionUtil.handleUI(e, logger_);
        }
        
        getWorkspace().setVisible(false);
        getWorkspace().dispose();
        logger_.debug("Goodbye!");
        LogManager.shutdown();
        System.exit(0);
    }
}