package toolbox.plugin.jdbc.action;

import java.awt.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.plugin.jdbc.QueryPlugin;
import toolbox.util.ArrayUtil;
import toolbox.util.StringUtil;
import toolbox.util.db.SQLFormatter;
import toolbox.workspace.WorkspaceAction;

/**
 * Formats the current or selected sql statements.
 * 
 * @see toolbox.plugin.jdbc.QueryPlugin
 */
public class FormatSQLAction extends WorkspaceAction
{
    private static final Logger logger_ = 
        Logger.getLogger(FormatSQLAction.class);
    
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
     * Creates a FormatSQLAction.
     * 
     * @param plugin Parent plugin.
     */
    public FormatSQLAction(QueryPlugin plugin)
    {
        super("Format", false, null, plugin.getStatusBar());
        plugin_ = plugin;
    }

    //--------------------------------------------------------------------------
    // SmartAction Abstract Methods
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e) throws Exception
    {
        String sql = plugin_.getActiveText();

        if (StringUtils.isBlank(sql))
        {
            plugin_.getStatusBar().setWarning("Nothing to format.");
        }
        else
        {
            sql = StringUtils.replace(sql, "\n", "");
            
            SQLFormatter formatter = plugin_.getFormatter();
            String[] statements = StringUtil.tokenize(sql, ";", true);
            StringBuffer sb = new StringBuffer();

            logger_.info(ArrayUtil.toString(statements, true));
            
            for (int i = 0; i < statements.length; i++)
            {
                if (statements[i].indexOf(";") >= 0)
                {
                    sb.append(";\n");
                    continue;
                }
                    
                if (StringUtils.isBlank(statements[i]))
                {
                    sb.append("\n");
                    continue;
                }
                
                String s = formatter.format(statements[i]);
                s = StringUtils.stripEnd(s, "\n");
               
                logger_.info(
                    StringUtil.banner(
                        "Before\n" + "'" + statements[i] + "'" +
                        "\nAfter\n" + "'" + s + "'"));
                
                sb.append(s);
                
            }
            
            plugin_.setActiveText(sb.toString());
        }
    }
}