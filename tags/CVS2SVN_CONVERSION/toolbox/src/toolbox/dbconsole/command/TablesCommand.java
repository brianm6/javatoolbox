package toolbox.dbconsole.command;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.set.ListOrderedSet;


import toolbox.dbconsole.Command;
import toolbox.dbconsole.TestConsole;
import toolbox.dbconsole.TestDatabase;
import toolbox.dbconsole.TestEnvironment;
import toolbox.util.JDBCSession;
import toolbox.util.StringUtil;

/**
 * Lists the tables in a database. Tries to filter out system level tables.
 */
public class TablesCommand implements Command {
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a TablesCommand.
     */
    public TablesCommand() {
    }

    //--------------------------------------------------------------------------
    // Command Interface
    //--------------------------------------------------------------------------

    /*
     * @see toolbox.dbconsole.Command#getName()
     */
    public String getName() {
        return "tables";
    }


    /*
     * @see toolbox.dbconsole.Command#getDescription()
     */
    public String getDescription() {
        return "List the tables in the database.";
    }


    /*
     * @see toolbox.dbconsole.Command#execute(toolbox.dbconsole.TestConsole, java.lang.Object[])
     */
    public void execute(TestConsole console, Object[] args) throws Exception {

        PrintStream ps = console.getPrintStream();
        TestEnvironment env = console.getTestEnv();

        if (env == null) {
            ps.println("Set the test environment first using 'setenv'");
            return;
        }

        TestDatabase[] dbs = env.getDatabases();
        
        for (int j = 0; j < dbs.length; j++) {
            
            ps.println(StringUtil.banner(dbs[j].getName() + " Tables"));
            String [] tables = JDBCSession.getTableNames(dbs[j].getName());
            
            // Remove oracle table we don't give a rats patootie about...
            Set filtered = ListOrderedSet.decorate(new HashSet());
            int cnt = 0;
            
            for (int i = 0; i < tables.length; i++) {
                String tableName = tables[i];
                
                if (tableName.indexOf('/') < 0 &&
                    tableName.indexOf('$') < 0 &&
                    !tableName.startsWith("DBMS_") &&
                    !tableName.startsWith("ALL_") &&
                    !tableName.startsWith("ORA_") &&
                    !tableName.startsWith("OWA_") &&
                    !tableName.startsWith("USER_") &&
                    !tableName.startsWith("UTL_") &&
                    !tableName.startsWith("PKG_") &&
                    !tableName.startsWith("HS_") &&
                    !tableName.startsWith("NLS_") &&
                    !tableName.startsWith("EXU") &&
                    !tableName.startsWith("LOADER") &&
                    !tableName.startsWith("_ALL_") &&
                    !tableName.startsWith("OLAP_") &&
                    !tableName.startsWith("ROLE_") &&
                    !tableName.startsWith("DBA_")) {
                    
                    filtered.add(tableName);
                    cnt++;
                } 
            }
            
            for (Iterator i = filtered.iterator(); i.hasNext();) {
                ps.println(i.next().toString());
            }
            
            ps.println(filtered.size() + " Tables");
        }
    }

    // -------------------------------------------------------------------------
    // Protected
    // -------------------------------------------------------------------------
    
    /**
     * Prints help.
     * 
     * @param ps Stream to print help to.
     */
    protected void printHelp(PrintStream ps) {
        ps.println(getDescription());
    }
}