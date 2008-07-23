package toolbox.plugin.jdbc.action;

import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.Connection;

import javax.swing.Action;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import toolbox.plugin.jdbc.QueryPlugin;
import toolbox.util.JDBCSession;
import toolbox.util.JDBCUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.JSmartTextArea;

public class ExportToDbUnitXMLAction extends BaseAction {

	public static final Logger log = Logger.getLogger(ExportToDbUnitXMLAction.class);
	
    public ExportToDbUnitXMLAction(QueryPlugin plugin) 
    {
        super(plugin, "Export to DbUnit XML", true, null, plugin.getStatusBar());
        putValue(SMALL_ICON, ImageCache.getIcon(ImageCache.IMAGE_DUKE));
        putValue(Action.NAME, "");
        putValue(SHORT_DESCRIPTION, "Exports database table to DbUnit XML format");
    }
    
    //--------------------------------------------------------------------------
    // SmartAction Interface
    //--------------------------------------------------------------------------
    
    public void runAction(ActionEvent e) throws Exception
    {
    	JSmartTextArea outputTextArea = getPlugin().getResultsArea();
    	String selectedText = outputTextArea.getSelectedText();
    	if (selectedText == null)
    	    selectedText = "";
    	String[] tokens = StringUtils.split(selectedText.trim());
        String tableName = "";
        String schemaName = "";
 
    	switch (tokens.length) {
    		case 0 : 
                JSmartOptionPane.showMessageDialog(
                        getPlugin().getView(),
                        "Select the name of the table to export to XML in the output textarea.", 
                        "Error", 
                        JSmartOptionPane.ERROR_MESSAGE);
                return;
                
    	    case 1 : 
    	        tableName = tokens[0];
    	        break;
    	    
    	    case 2 : 
    	        schemaName = tokens[0];
    	        tableName = tokens[1];
    	        break;
    	        
    	    default:
                JSmartOptionPane.showMessageDialog(
                        getPlugin().getView(),
                        "Could not determine schema and table name to export. Select the name of the table to export to XML in the output textarea.", 
                        "Error", 
                        JSmartOptionPane.ERROR_MESSAGE);
    	        return;
        }
       
        log.debug("Exporting: " + schemaName + "." + tableName);
        
        Connection jdbcConnection = null;
        IDatabaseConnection connection = null;
        String session = getPlugin().getCurrentProfile().getProfileName();
        
        try
        {
            jdbcConnection = JDBCSession.getConnection(session);
            connection = new DatabaseConnection(jdbcConnection, schemaName); 

            // partial database export
            QueryDataSet partialDataSet = new QueryDataSet(connection);
            //partialDataSet.addTable("FOO", "SELECT * FROM TABLE WHERE COL='VALUE'");
            partialDataSet.addTable(tableName);
            OutputStream baos = new ByteArrayOutputStream();
            FlatXmlDataSet.write(partialDataSet, baos);
            outputTextArea.append(baos.toString());

            // full database export
            //IDataSet fullDataSet = connection.createDataSet();
            //FlatXmlDataSet.write(fullDataSet, new FileOutputStream("full.xml"));

            // dependent tables database export: export table X and all tables that
            // have a PK which is a FK on X, in the right order for insertion
            //String[] depTableNames = TablesDependencyHelper.getAllDependentTables(connection, "X");
            //IDataSet depDataset = connection.createDataSet(depTableNames);
            //FlatXmlDataSet.write(depDataSet, new FileOutputStream("dependents.xml"));            
        }
        finally
        {
            if (connection != null) connection.close();
            JDBCUtil.releaseConnection(jdbcConnection);
        }
    }
}