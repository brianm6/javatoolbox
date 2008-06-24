package toolbox.plugin.jdbc.action;

import java.awt.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.plugin.jdbc.QueryPlugin;
import toolbox.util.ArrayUtil;
import toolbox.util.StringUtil;
import toolbox.util.db.SQLFormatter;

/**
 * Formats the current or selected sql statements.
 * 
 * @see toolbox.plugin.jdbc.QueryPlugin
 */
public class FormatSQLAction extends BaseAction
{
    private static final Logger logger_ = 
        Logger.getLogger(FormatSQLAction.class);
    
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
        super(plugin, "Format", false, null, plugin.getStatusBar());
    }

    //--------------------------------------------------------------------------
    // SmartAction Abstract Methods
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e) throws Exception
    {
        String sql = getPlugin().getActiveText();

        if (StringUtils.isBlank(sql))
        {
            getPlugin().getStatusBar().setWarning("Nothing to format.");
        }
        else
        {
            sql = StringUtils.replace(sql, "\n", "");
            
            SQLFormatter formatter = getPlugin().getFormatter();
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
            
            getPlugin().setActiveText(sb.toString());
        }
    }
}