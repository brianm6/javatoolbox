package toolbox.plugin.jdbc.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.plugin.jdbc.QueryPlugin;
import toolbox.util.ExceptionUtil;
import toolbox.util.StringUtil;
import toolbox.util.ui.SmartAction;

/**
 * ErrorHandlerAction is an alternative to the built in error handler in 
 * SmartAction. This handler appends the details of the thrown exception to
 * the results text area of the QueryPlugin instead of popping the error up
 * in a dialog box.
 * 
 * @see toolbox.util.ui.SmartAction
 */
public class ErrorHandlerAction extends AbstractAction
{
    private static final Logger logger_ = 
        Logger.getLogger(ErrorHandlerAction.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Used to get at the results text area.
     */
    private QueryPlugin plugin_;
    
    /**
     * Used to get at the caught exception.
     */
    private SmartAction smart_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ErrorHandlerAction.
     * 
     * @param plugin Query plugin.
     * @param smart SmartAction that has the error.
     */
    public ErrorHandlerAction(QueryPlugin plugin, SmartAction smart)
    {
        plugin_ = plugin;
        smart_ = smart;
    }

    //--------------------------------------------------------------------------
    // ActionListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        if (plugin_.isSendErrorToConsole())
        {
            StringBuffer sb = new StringBuffer();
            sb.append(StringUtils.center(" ERROR ", 80, "="));
            sb.append("\n");
            sb.append(smart_.getCaught().getMessage() + "\n");
            sb.append("\n");
            sb.append(ExceptionUtil.getStackTrace(smart_.getCaught()));
            sb.append(StringUtil.BR + "\n");
            
            plugin_.getResultsArea().append(sb.toString());
        }
        else
            ExceptionUtil.handleUI(smart_.getCaught(), logger_);
    }
}