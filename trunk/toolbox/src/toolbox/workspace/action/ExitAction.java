package toolbox.workspace.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.workspace.IPlugin;
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
     * Routes method invocation to exit().
     * 
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ae)
    {
        exit();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * The real exit method.
     */
    public void exit()
    {
        exitForTesting();
        System.exit(0);
    }
    
    /**
     * Exits the application but does not exit the JVM.
     */
    public void exitForTesting()
    {
        try
        {
            // Save all preferences first...
            (new SavePreferencesAction(getWorkspace())).runAction();
            
            // Release resources retained by the plugins by "destroying" them
            IPlugin[] plugins = getWorkspace().getPluginHost().getPlugins();
            
            logger_.debug("Plugins to destroy:" + plugins.length);
            for (int i = 0; i < plugins.length; i++)
            {
                try
                {
                    logger_.debug(
                        "Destroying plugin " + plugins[i].getPluginName());
                    
                    plugins[i].destroy();
                }
                catch (Exception e)
                {
                    logger_.error("destroy", e);
                }
            }
        }
        catch (Exception e)
        {
            ExceptionUtil.handleUI(e, logger_);
        }
        
        //getWorkspace().setVisible(false);
        getWorkspace().dispose();
        logger_.debug("Goodbye!");
        LogManager.shutdown();
    }
}