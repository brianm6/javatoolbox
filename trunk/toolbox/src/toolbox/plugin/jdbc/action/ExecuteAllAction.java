package toolbox.plugin.jdbc.action;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import toolbox.plugin.jdbc.QueryPlugin;
import toolbox.util.StringUtil;

/**
 * Runs all SQL statements in the editor. Each SQL statement must be 
 * terminated by a semicolon. The results are appendended to the output 
 * textarea.
 * 
 * @see toolbox.plugin.jdbc.QueryPlugin
 */
public class ExecuteAllAction extends BaseAction
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an ExecuteAllAction.
     */
    public ExecuteAllAction(QueryPlugin plugin)
    {
        super(plugin, "Execute All SQL", true, plugin, plugin.getStatusBar());
        putValue(MNEMONIC_KEY, new Integer('E'));
        putValue(SHORT_DESCRIPTION, "Executes all the SQL statements");
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
        
        String sqlText = plugin.getSQLEditor().getText().trim();
        
        if (StringUtils.isBlank(sqlText))
        {
            plugin.getStatusBar().setWarning(
                "Enter SQL statements to execute into the editor first.");
        }
        else
        {
            plugin.getStatusBar().setInfo("Executing...");
            
            String[] stmts = 
                StringUtils.split(sqlText, QueryPlugin.SQL_TERMINATOR);
            
            //logger_.debug(
            //    StringUtil.addBars(ArrayUtil.toString(stmts, true)));
            
            List errors = new ArrayList();
            
            for (int i = 0; i < stmts.length; i++)
            {
                try
                {
                    stmts[i] = stmts[i].trim();
                    String results = plugin.executeSQL(stmts[i]);
                    
                    if (!StringUtil.isMultiline(results))
                    {
                        String command = StringUtils.split(stmts[i])[0];
                        //plugin.getResultsArea().append(command + " ");
                    }
                    else
                    {
                        plugin.getResultsArea().append("\n");
                        
                        //resultsArea_.append(
                        //    "Multline found..skipping command : " + 
                        //    stmts[i].substring(0,10));
                    }
                    
                    //resultsArea_.append(StringUtil.addBars(results) + "\n");
                    plugin.getResultsArea().append(results + "\n");
                    
                    //StringOutputStream sos = new StringOutputStream();
                    //HexDump.dump(results.getBytes(), 0, sos, 0);
                    //resultsArea_.append(sos.toString());
                    
                    
                    //
                    // Scroll to the end of the output textarea if more
                    // than a couple of page fulls of results is appended
                    //
                    
                    if ((!StringUtils.isBlank(results)) &&
                        (StringUtil.tokenize(results, StringUtil.NL).length < 
                            QueryPlugin.AUTO_SCROLL_THRESHOLD))
                    {
                        plugin.getResultsArea().scrollToEnd();
                    }
                }
                catch (Exception ex)
                {
                    // Collect the errors
                    errors.add(ex);
                }
                
                if (errors.size() == 1)
                {
                    throw (Exception) errors.get(0);
                }
                else if (errors.size() > 1)
                {
                    // Merge errors into a single exception if many.
                    StringBuffer sb = new StringBuffer();
                    sb.append("Not all statements executed successfully.");
                    sb.append("\n");
                    
                    for (int j = 0; j < errors.size(); j++)
                    {
                        Exception ex = (Exception) errors.get(j);
                        sb.append(ex.getMessage()).append("\n");
                        sb.append(ExceptionUtils.getFullStackTrace(ex));
                        sb.append("\n");
                    }
                    
                    throw new SQLException(sb.toString());
                }
            }

            plugin.getStatusBar().setInfo("Done");
        }
    }
}