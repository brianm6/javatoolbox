package toolbox.plugin.jdbc.action;

import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import toolbox.plugin.jdbc.QueryPlugin;
import toolbox.util.JDBCUtil;
import toolbox.workspace.WorkspaceAction;

/**
 * Queries the DB metadata and dumps a list of the tables.
 */
public class ListTablesAction extends WorkspaceAction
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
     * Creates a ListTablesAction.
     * 
     * @param plugin Parent plugin.
     */
    public ListTablesAction(QueryPlugin plugin)
    {
        super("List Tables", true, true, null, plugin.getStatusBar());
        plugin_ = plugin;
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
        Connection conn = JDBCUtil.getConnection();
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = meta.getTables(null, null, null, null);
        String tables = JDBCUtil.format(rs);
        plugin_.getResultsArea().append(tables);
        JDBCUtil.releaseConnection(conn);
    }
}