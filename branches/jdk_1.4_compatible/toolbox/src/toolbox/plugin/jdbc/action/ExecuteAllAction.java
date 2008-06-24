package toolbox.plugin.jdbc.action;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import org.jedit.syntax.JEditTextArea;

import toolbox.plugin.jdbc.QueryPlugin;
import toolbox.plugin.jdbc.SQLMessageException;
import toolbox.util.ElapsedTime;
import toolbox.util.StringUtil;
import toolbox.workspace.IStatusBar;

/**
 * Runs all SQL statements in the editor. Each SQL statement must be terminated
 * by a semicolon. The results are appended to the output textarea.
 * 
 * @see toolbox.plugin.jdbc.QueryPlugin
 */
public class ExecuteAllAction extends BaseAction 
{
    private static final Logger logger_ =
        Logger.getLogger(ExecuteAllAction.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an ExecuteAllAction.
     */
    public ExecuteAllAction(QueryPlugin plugin)
    {
        super(
            plugin, 
            "Execute All SQL", 
            true, 
            plugin.getView(),
            plugin.getStatusBar());
        
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
        IStatusBar statusBar = plugin.getStatusBar();
        JEditTextArea editor = plugin.getSQLEditor();
        String sqlText = editor.getSelectedText();
        
        if (StringUtils.isBlank(sqlText))
            sqlText = editor.getText();
        
        sqlText = sqlText.trim();
        
        if (StringUtils.isBlank(sqlText))
        {
            statusBar.setWarning(
                "Enter SQL statements to execute into the editor first.");
        }
        else
        {
            plugin.getStatusBar().setInfo("Executing...");

            // Split sql editor contents into individual sql statements
            String[] stmts = 
                StringUtils.split(sqlText, plugin.getSqlTerminator());
            
            // Collect errors if continueOnError is true
            List errors = new ArrayList();
            
            // Time the execution
            ElapsedTime time = new ElapsedTime();
            
            // Execute each statement 
            for (int i = 0; i < stmts.length; i++)
            {
                try
                {
                    stmts[i] = stmts[i].trim();

                    String status =
                        "Executing statement " 
                        + (i + 1) 
                        + " of " 
                        + stmts.length
                        + "...";

                    statusBar.setInfo(status);
                    //logger_.debug(status);
                    
                    String results = 
                        plugin.executeSQL(
                            stmts[i], 
                            new ResultFormatter(getPlugin()));
                    
                    // Scroll to the end of the output textarea if more
                    // than a couple of page fulls of results is appended
                    
                    if ((!StringUtils.isBlank(results)) &&
                        (StringUtil.tokenize(results, StringUtil.NL).length 
                            < plugin.getAutoScrollThreshold()))
                    {
                        plugin.getResultsArea().scrollToEnd();
                    }
                }
                catch (SQLException se)
                {
                    // Wrap sql exceptions with their offending sql statements
                    SQLMessageException sme = 
                        new SQLMessageException(se, stmts[i]);
                    
                    errors.add(sme);
                    
                    // Break out if specified on first error
                    if (!plugin.isContinueOnError())
                        break;
                }
                catch (Exception ex)
                {
                    errors.add(ex);

                    // Break out if specified on first error
                    if (!plugin.isContinueOnError())
                        break;
                }
            }
            
            // Stop timer
            time.setEndTime();
            
            switch (errors.size())
            {
                // SUCCESS
                case 0:
                    statusBar.setInfo(
                        stmts.length 
                        + " statement(s) executed successfully in " 
                        + time);
                    break;
                
                // FAILURE
                case 1:
                    statusBar.setError("Statement failed execution in " + time);
                    throw (Exception) errors.get(0);
                
                // MULTIPLE FAILURES
                default:
                    
                    // Merge errors into a single exception.
                    StringBuffer sb = new StringBuffer();
                
                    sb.append(
                        errors.size()     
                        + " out of " 
                        + stmts.length 
                        + " statments failed execution in " 
                        + time 
                        + "\n");

                    for (int j = 0; j < errors.size(); j++)
                    {
                        Exception ex = (Exception) errors.get(j);
                        
                        sb.append((j + 1) + ") ")  // Exception number
                          .append(ex.getMessage())
                          .append("\n")
                          .append(ExceptionUtils.getFullStackTrace(ex))
                          .append("\n");
                    }
                    
                    throw new SQLException(sb.toString());
            }
        }
    }
}