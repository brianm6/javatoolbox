package toolbox.plugin.jdbc.action;

import java.awt.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import toolbox.plugin.jdbc.QueryPlugin;
import toolbox.util.StringUtil;
import toolbox.util.db.SQLFormatter;
import toolbox.workspace.WorkspaceAction;

/**
 * Formats the current or selected sql statements.
 */
public class FormatSQLAction extends WorkspaceAction
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
            SQLFormatter formatter = new SQLFormatter();
            String[] statements = StringUtil.tokenize(sql, ";");
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < statements.length; i++)
            {
                sb.append(formatter.format(statements[i] + ";"));
                sb.append("\n");
            }

            plugin_.setActiveText(sb.toString());
        }
    }
}