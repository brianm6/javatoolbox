package toolbox.plugin.jdbc.action;

import java.awt.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import toolbox.jedit.JEditTextArea;
import toolbox.plugin.jdbc.QueryPlugin;
import toolbox.util.StringUtil;
import toolbox.workspace.WorkspaceAction;

/**
 * Runs the query and appends the results to the output text area.
 */
public class ExecuteCurrentAction extends WorkspaceAction
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Parent plugin.
     */
    private final QueryPlugin plugin_;

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
        super("Execute Current Statement", true, plugin, plugin.getStatusBar());
        plugin_ = plugin;
    }

    //--------------------------------------------------------------------------
    // SmartAction Abstract Method
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.SmartAction#runAction(
     *      java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e) throws Exception
    {
        //
        // By default, execute the selected text
        //

        String sql = plugin_.getSQLEditor().getSelectedText();

        //
        // If no text is selected, then execute the current statement. This
        // assumes we are on the first line of the statement and that there
        // is a semicolon somewhere to tell us where the statement ends.
        //

        if (StringUtils.isBlank(sql))
        {
            JEditTextArea sqlEditor = plugin_.getSQLEditor();
            int max = sqlEditor.getLineCount();
            int curr = sqlEditor.getCaretLine();
            boolean terminatorFound = false;
            StringBuffer stmt = new StringBuffer();

            while (curr <= max && !terminatorFound)
            {
                String line = plugin_.getSQLEditor().getLineText(curr++);
                int pos = -1;
                
                if ((pos = line.indexOf(";")) >= 0)
                {
                    stmt.append(line.substring(0, pos + 1));
                    terminatorFound = true;
                }
                else
                {
                    stmt.append("\n" + line);
                }
            }

            //
            // If no terminating semicolon for the statement is found, then
            // assume only the current line contains the entire sql
            // statement to execute.
            //

            sql = stmt.toString();
        }

        if (StringUtils.isBlank(sql))
        {
            plugin_.getStatusBar().setInfo("Enter SQL to execute");
        }
        else
        {
            //QueryPlugin.logger_.debug("Executing SQL: \n" + sql);

            plugin_.getStatusBar().setInfo("Executing...");
            String results = plugin_.executeSQL(sql);
            plugin_.getResultsArea().append(results + "\n");

            if ((!StringUtils.isBlank(results)) &&
                (StringUtil.tokenize(results, StringUtil.NL).length < 50))
                plugin_.getResultsArea().scrollToEnd();

            plugin_.getStatusBar().setInfo("Done");
        }
    }
}