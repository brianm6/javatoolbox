package toolbox.dbconsole.command;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import toolbox.dbconsole.Command;
import toolbox.dbconsole.TestConsole;
import toolbox.dbconsole.TestDatabase;
import toolbox.dbconsole.TestEnvironment;
import toolbox.util.ArrayUtil;
import toolbox.util.JDBCSession;

/**
 * Executes a SQL query against the either one of the registered databases. 
 * Figures out which database to query by keying off of the table name.
 */
public class SelectCommand implements Command {

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    static private Map db2TableMap = new HashMap();
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a SelectCommand. Necessary for instantiation via reflection.
     */
    public SelectCommand() {
    }

    //--------------------------------------------------------------------------
    // Command Interface
    //--------------------------------------------------------------------------

    /*
     * @see toolbox.dbconsole.Command#getName()
     */
    public String getName() {
        return "select";
    }


    /*
     * @see toolbox.dbconsole.Command#getDescription()
     */
    public String getDescription() {
        return "Executes a sql select statement.";
    }

    
    /*
     * @see toolbox.dbconsole.Command#execute(toolbox.dbconsole.TestConsole, java.lang.Object[])
     */
    public void execute(TestConsole console, Object[] args) throws Exception {

        PrintStream ps = console.getPrintStream();
        TestEnvironment testEnv = console.getTestEnv();

        if (testEnv == null) {
            ps.println("Set the test environment first using 'setenv'");
            return;
        }

        // Needs command line verbatim, not as tokens
        String select = console.getCommandLine();

        String[] tokens = StringUtils.split(select);

        int fromIdx = ArrayUtil.indexOf(
            tokens,
            "from",
            String.CASE_INSENSITIVE_ORDER);

        if (fromIdx < 2 && tokens.length > fromIdx + 1) {
            ps.println("Not a valid sql select statment.");
            return;
        }

        String table = tokens[fromIdx + 1];
        
        TestDatabase db = getDatabaseForTable(testEnv, table);

        if (db == null) {
            ps.println("Invalid table name: " + table);
            return;
        }

        String result = JDBCSession.executeQuery(db.getName(), select);
        ps.println(result);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Assumes dbs don't share tables of the same name.
     *
     * @param env Test environment.
     * @param table Name of table.
     * @return TestDatabase
     */
    public static TestDatabase getDatabaseForTable(
        TestEnvironment env,
        String table)
        throws Exception {
        
        TestDatabase[] dbs = env.getDatabases();
        
        for (int i = 0; i < dbs.length; i++) {

            if (!db2TableMap.containsKey(dbs[i].getName())) {
                db2TableMap.put(
                    dbs[i].getName(), 
                    JDBCSession.getTableNames(dbs[i].getName()));
            }
            
            String[] tables = (String[]) db2TableMap.get(dbs[i].getName());
            
            if (ArrayUtil.contains(tables, table, String.CASE_INSENSITIVE_ORDER))
                return dbs[i];
        }

        // Table not found
        return null;
    }

    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Prints out help for this command.
     *
     * @param ps Print stream to write help to.
     */
    protected void printHelp(PrintStream ps) {
        ps.println("\nExecutes a sql select statement against the appropriate test database.");
        ps.println("Example: select * from address");
    }
}