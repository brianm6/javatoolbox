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
import toolbox.util.StringUtil;
import toolbox.workspace.IStatusBar;

/**
 * Runs all SQL statements in the editor. Each SQL statement must be terminated
 * by a semicolon. The results are appendended to the output textarea.
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
            
            String[] stmts = 
                StringUtils.split(
                    sqlText, 
                    plugin.getSqlTerminator());
            
            //logger_.debug(
            //    StringUtil.addBars(ArrayUtil.toString(stmts, true)));
            
            List errors = new ArrayList();
            
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
                    logger_.debug(status);
                    
                    String results = plugin.executeSQL(stmts[i]);
                    
                    if (!StringUtil.isMultiline(results))
                    {
                        String command = StringUtils.split(stmts[i])[0];
                        //plugin.getResultsArea().append(command + " ");
                    }
                    else
                    {
                        // plugin.getResultsArea().append("\n");
                        
                        //resultsArea_.append(
                        //    "Multline found..skipping command : " + 
                        //    stmts[i].substring(0,10));
                    }
                    
                    //resultsArea_.append(StringUtil.addBars(results) + "\n");
                    //plugin.getResultsArea().append(results + "\n");
                    
                    //StringOutputStream sos = new StringOutputStream();
                    //HexDump.dump(results.getBytes(), 0, sos, 0);
                    //resultsArea_.append(sos.toString());
                    
                    
                    //
                    // Scroll to the end of the output textarea if more
                    // than a couple of page fulls of results is appended
                    //
                    
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
                    
                    // Break out..
                    if (!plugin.isContinueOnError())
                        break;
                    
                }
                catch (Exception ex)
                {
                    errors.add(ex);

                    // Break out..
                    if (!plugin.isContinueOnError())
                        break;
                }
            }
            
            switch (errors.size())
            {
                case 0:
                    statusBar.setInfo(
                        stmts.length + " statement(s) executed successfully.");
                    break;
                    
                case 1:
                    statusBar.setError("Statement failed execution");
                    throw (Exception) errors.get(0);
                    
                default:
                    
                    // Merge errors into a single exception if many.
                    
                    StringBuffer sb = new StringBuffer();
                    sb.append(
                        errors.size()     
                        + " out of " 
                        + stmts.length 
                        + " statments failed execution.\n");

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