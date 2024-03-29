package toolbox.plugin.jdbc.action;

import java.awt.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.jedit.JEditTextArea;
import toolbox.plugin.jdbc.QueryPlugin;
import toolbox.util.ElapsedTime;
import toolbox.util.StringUtil;
import toolbox.workspace.IStatusBar;

/**
 * Executes the current SQL statement. Current is defined as follows:
 * 
 * <ul> 
 *  <li>Current selection if the selection is not empty. 
 *  <li>SQL statement that begins on the current line and is terminated by a 
 *      semicolon on the same or a subsequent line.
 * </ul>
 * 
 * @see toolbox.plugin.jdbc.QueryPlugin
 */
public class ExecuteCurrentAction extends BaseAction 
{
    private static final Logger logger_ = 
        Logger.getLogger(ExecuteCurrentAction.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates an ExecuteCurrentAction.
     * 
     * @param plugin Parent plugin.
     */
    public ExecuteCurrentAction(QueryPlugin plugin)
    {
        super(
            plugin, 
            "Execute sql statement", 
            true, 
            plugin.getView(), 
            plugin.getStatusBar());
    }

    //--------------------------------------------------------------------------
    // SmartAction Abstract Methods
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.SmartAction#runAction(
     *      java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e) throws Exception
    {
        QueryPlugin plugin = getPlugin();
        JEditTextArea sqlEditor = plugin.getSQLEditor();
        IStatusBar statusBar = plugin.getStatusBar();
        String sql = sqlEditor.getSelectedText();
        
        // Check for a text selection first.
        // If no text is selected, then execute the current statement. This
        // assumes we are on the first line of the statement and that there
        // is a semicolon somewhere to tell us where the statement ends.

        if (StringUtils.isBlank(sql))
        {
            int max = sqlEditor.getLineCount();   // One based
            int curr = sqlEditor.getCaretLine();  // Zero based
            boolean terminatorFound = false;
            StringBuffer stmt = new StringBuffer();

            // TODO: Identify sql statements that do on begin on the current
            //       line but terminate on the current line.
            
            while (curr < max && !terminatorFound)
            {
                String line = sqlEditor.getLineText(curr++);
                int termPos = line.indexOf(plugin.getSqlTerminator());
                
                if (termPos >= 0)
                {
                    stmt.append(line.substring(0, termPos + 1));
                    terminatorFound = true;
                }
                else
                {
                    stmt.append("\n" + line);
                }
            }

            
            for (int i = curr - 1; i >= 0; i--)
            {
                String line = sqlEditor.getLineText(i);
                int termPos = line.lastIndexOf(plugin.getSqlTerminator());
                
                // Terminator is the last char on the line..break out
                if (termPos == line.length() - 1)
                    break;
                
                // Terminator is somewhere in the line...break out
                else if (termPos >= 0)
                {
                    stmt.insert(0, line.substring(termPos + 1));
                    break;
                }
                
                // Terminator not found, keep searching up...
                else
                {
                    stmt.insert(0, line);
                }
            }
            
            // If no terminating semicolon for the statement is found, then
            // assume only the current line contains the entire sql statement to
            // execute.
            sql = stmt.toString();
        }

        
        if (StringUtils.isBlank(sql))
        {
            statusBar.setWarning("No SQL statement(s) to execute");
        }
        else
        {
            statusBar.setInfo("Executing...");
            
            ElapsedTime time = new ElapsedTime();
            
            String results = plugin.executeSQL(
                sql, new ResultFormatter(getPlugin()));
            
            time.setEndTime();
 
            if ((!StringUtils.isBlank(results)) &&
                (StringUtil.tokenize(results, StringUtil.NL).length 
                    < plugin.getAutoScrollThreshold()))
                plugin.getResultsArea().scrollToEnd();

            statusBar.setInfo("SQL statement executed in " + time);
        }
    }
}