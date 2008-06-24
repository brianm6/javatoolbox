package toolbox.dbconsole.command;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import toolbox.dbconsole.Command;
import toolbox.dbconsole.TestConsole;
import toolbox.dbconsole.TestDatabase;
import toolbox.dbconsole.TestEnvironment;

/**
 * Sets the currently active test environment.
 */
public class SetEnvCommand implements Command {

    // TODO: Move all configuration information for environments to a properties
    //       or XML file.

    //--------------------------------------------------------------------------
    // Static Fields
    //--------------------------------------------------------------------------

    /**
     * Maps an environment's name to its corresponding instance of
     * TestEnvironment.
     */
    private static Map envMap;

    //--------------------------------------------------------------------------
    // Static Blocks
    //--------------------------------------------------------------------------

    static {

        // Create all test environments and place in the environment map.
        envMap = new HashMap();

        // Oracle 
        // =====================================================================
        TestDatabase oracleDatabase = new TestDatabase(
            "oracle",
            "oracle.jdbc.driver.OracleDriver",
            "jdbc:oracle:thin:fill_me_in",
            "myusername",
            "mypassword",
            "adminusername");

        TestEnvironment oracleEnvironment = new TestEnvironment("oracle");
        oracleEnvironment.addDatabase(oracleDatabase);
        envMap.put(oracleEnvironment.getName(), oracleEnvironment);

        // P6SPY-TEST
        // =====================================================================
        TestDatabase p6SpyDatabase = new TestDatabase(
            "p6spy",
            "com.p6spy.engine.spy.P6SpyDriver",
            "jdbc:oracle:thin:fill_me_in",
            "myusername",
            "mypassword",
            "adminusername");

        TestEnvironment p6SpyEnvironment = new TestEnvironment("p6spy");
        p6SpyEnvironment.addDatabase(p6SpyDatabase);
        envMap.put(p6SpyEnvironment.getName(), p6SpyEnvironment);

        // HSQL1 - Local instance of hypersonic sql db
        // =====================================================================
        TestDatabase hsqlDatabase = new TestDatabase(
            "hsql1",
            "org.hsqldb.jdbcDriver",
            "jdbc:hsqldb:hsql://hsql1",
            "sa",
            "",
            "sa");

        TestEnvironment hsql1Environment = new TestEnvironment("hsql1");
        hsql1Environment.addDatabase(hsqlDatabase);
        envMap.put(hsql1Environment.getName(), hsql1Environment);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Necessary for instantiation via reflection.
     */
    public SetEnvCommand() {
    }

    //--------------------------------------------------------------------------
    // Command Interface
    //--------------------------------------------------------------------------

    /*
     * @see toolbox.dbconsole.Command#getName()
     */
    public String getName() {
        return "setenv";
    }


    /*
     * @see toolbox.dbconsole.Command#getDescription()
     */
    public String getDescription() {
        return "Sets the currently active testing environment.";
    }


    /*
     * @see toolbox.dbconsole.Command#execute(toolbox.dbconsole.TestConsole, java.lang.Object[])
     */
    public void execute(TestConsole console, Object[] args) throws Exception {

        PrintStream ps = console.getPrintStream();
        TestEnvironment testEnv = null;

        if (args.length == 0) {
            printHelp(ps);
        }
        else {
            String envName = args[0].toString();
            testEnv = (TestEnvironment) envMap.get(envName);

            if (testEnv == null) {
                ps.println("Environment '" + envName + "' does not exist.");
            }
            else {
                testEnv.setPrintStream(ps);
                testEnv.validate();
            }
        }

        console.setTestEnv(testEnv);
    }


    /**
     * Returns the environment with the given name.
     *
     * @param name Environment name.
     * @return TestEnvironment
     */
    public static TestEnvironment getEnvironment(String name) {
        return (TestEnvironment) envMap.get(name);
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Prints help on this command to the console.
     *
     * @param ps PrintStream
     */
    protected void printHelp(PrintStream ps) {
        ps.println();
        ps.println("Sets the test environment to point to.");
        ps.println("Usage  : setenv <environment>");
        ps.println("Options: oracle - sample oracle env");
        ps.println("         p6spy  - sample p6spy env");        
        ps.println("         hdsq1  - sample hsql env");
        ps.println();
    }
}