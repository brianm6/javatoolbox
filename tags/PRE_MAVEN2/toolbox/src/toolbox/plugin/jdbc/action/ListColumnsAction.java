package toolbox.plugin.jdbc.action;

import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import org.apache.commons.lang.StringUtils;

import toolbox.plugin.jdbc.QueryPlugin;
import toolbox.util.JDBCSession;
import toolbox.util.JDBCUtil;
import toolbox.util.ui.JSmartOptionPane;

/**
 * Queries the DB metadata and dumps a list of all columns. If a table name is
 * selected in the results area, then only the columns for the selected table
 * are returned.
 * 
 * @see toolbox.plugin.jdbc.QueryPlugin
 */
public class ListColumnsAction extends BaseAction
{
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
        super(plugin, "List Columns", true, null, plugin.getStatusBar());
    }

    //--------------------------------------------------------------------------
    // SmartAction Abstract Methods
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e) throws Exception
    {
        String tableName = getPlugin().getResultsArea().getSelectedText();

        if (StringUtils.isBlank(tableName))
        {
            JSmartOptionPane.showMessageDialog(
                getPlugin().getView(),
                "Select text matching the column name from the outputarea.", 
                "Error", 
                JSmartOptionPane.ERROR_MESSAGE);
        }
        else
        {
            Connection conn = null;
            ResultSet rs = null;
            String session = getPlugin().getCurrentProfile().getProfileName();
            
            try
            {
                conn = JDBCSession.getConnection(session);
                DatabaseMetaData meta = conn.getMetaData();
                rs = meta.getColumns(null, null, tableName, null);
                String tables = JDBCUtil.format(rs);
                getPlugin().getResultsArea().append(tables);
            }
            finally
            {
                JDBCUtil.close(rs);
                JDBCUtil.releaseConnection(conn);
            }
        }
    }
}