package toolbox.plugin.jdbc.action;

import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import org.apache.commons.lang.StringUtils;

import toolbox.plugin.jdbc.QueryPlugin;
import toolbox.util.JDBCUtil;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.workspace.WorkspaceAction;

/**
 * Queries the DB metadata and dumps a list of all columns. If a table name is
 * selected in the results area, then only the columns for the selected table
 * are returned.
 * 
 * @see toolbox.plugin.jdbc.QueryPlugin
 */
public class ListColumnsAction extends WorkspaceAction
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Parent plugin.
     */
    private QueryPlugin plugin_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a ListColumnsAction.
     * 
     * @param plugin Parent plugin.
     */
    public ListColumnsAction(QueryPlugin plugin)
    {
        super("List Columns", true, true, null, plugin.getStatusBar());
        plugin_ = plugin;
    }

    //--------------------------------------------------------------------------
    // Abstract SmartAction
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e) throws Exception
    {
        String tableName = plugin_.getResultsArea().getSelectedText();

        if (StringUtils.isBlank(tableName))
        {
            JSmartOptionPane.showMessageDialog(plugin_,
                "Select text matching the column name from the output " +
                "area.", "Error", JSmartOptionPane.ERROR_MESSAGE);
        }
        else
        {
            Connection conn = null;

            try
            {
                conn = JDBCUtil.getConnection();
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet rs = meta.getColumns(null, null, tableName, null);
                String tables = JDBCUtil.format(rs);
                plugin_.getResultsArea().append(tables);
            }
            finally
            {
                JDBCUtil.releaseConnection(conn);
            }
        }
    }
}