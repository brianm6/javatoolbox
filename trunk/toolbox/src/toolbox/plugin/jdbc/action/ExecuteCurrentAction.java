package toolbox.plugin.jdbc.action;

import java.awt.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import toolbox.jedit.JEditTextArea;
import toolbox.plugin.jdbc.QueryPlugin;
import toolbox.util.StringUtil;

/**
 * Runs the query and appends the results to the output text area.
 * 
 * @see toolbox.plugin.jdbc.QueryPlugin
 */
public class ExecuteCurrentAction extends BaseAction
{
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
            plugin, 
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
        //
        // By default, execute the selected text
        //

        String sql = getPlugin().getSQLEditor().getSelectedText();

        //
        // If no text is selected, then execute the current statement. This
        // assumes we are on the first line of the statement and that there
        // is a semicolon somewhere to tell us where the statement ends.
        //

        if (StringUtils.isBlank(sql))
        {
            JEditTextArea sqlEditor = getPlugin().getSQLEditor();
            int max = sqlEditor.getLineCount();
            int curr = sqlEditor.getCaretLine();
            boolean terminatorFound = false;
            StringBuffer stmt = new StringBuffer();

            while (curr <= max && !terminatorFound)
            {
                String line = getPlugin().getSQLEditor().getLineText(curr++);
                int pos = -1;
                
                if ((pos = line.indexOf(QueryPlugin.SQL_TERMINATOR)) >= 0)
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
            getPlugin().getStatusBar().setInfo("Enter SQL to execute");
        }
        else
        {
            getPlugin().getStatusBar().setInfo("Executing...");
            String results = getPlugin().executeSQL(sql);
            getPlugin().getResultsArea().append(results + "\n");

            if ((!StringUtils.isBlank(results)) &&
                (StringUtil.tokenize(results, StringUtil.NL).length < 50))
                getPlugin().getResultsArea().scrollToEnd();

            getPlugin().getStatusBar().setInfo("Done");
        }
    }
}