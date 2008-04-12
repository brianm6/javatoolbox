package toolbox.dbconsole.command;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;


import toolbox.dbconsole.Command;
import toolbox.dbconsole.TestConsole;
import toolbox.dbconsole.TestDatabase;
import toolbox.dbconsole.TestEnvironment;
import toolbox.util.JDBCSession;
import toolbox.util.JDBCUtil;

/**
 * Lists the columns for a given table.
 */
public class ColumnsCommand implements Command {

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a ColumnsCommand.
     */
    public ColumnsCommand() {
    }

    //--------------------------------------------------------------------------
    // Command Interface
    //--------------------------------------------------------------------------

    /*
     * @see toolbox.dbconsole.Command#getName()
     */
    public String getName() {
        return "columns";
    }


    /*
     * @see toolbox.dbconsole.Command#getDescription()
     */
    public String getDescription() {
        return "Lists all the columns in a table.";
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

        if (args.length == 0) {
            printHelp(ps);
            return;
        }

        String tableName = args[0].toString();
        TestDatabase db = SelectCommand.getDatabaseForTable(env, tableName);

        if (db == null) {
            ps.println("Table '" + tableName + "' not found");
            return;
        }

        Connection conn = JDBCSession.getConnection(db.getName());

        try {
            DatabaseMetaData meta = conn.getMetaData();

            // Case has to match for oracle..
            ResultSet rs = meta.getColumns(
                null, null, tableName.toUpperCase(), null);

            String columns = JDBCUtil.format(rs);
            ps.println(columns);
        }
        finally {
            JDBCUtil.releaseConnection(conn);
        }
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Prints out help for this command.
     *
     * @param ps Print destination.
     */
    protected void printHelp(PrintStream ps) {
        ps.println(getDescription());
        ps.println("Usage: columns <table_name>");
    }
}