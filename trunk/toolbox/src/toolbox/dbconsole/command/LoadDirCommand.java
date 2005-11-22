package toolbox.dbconsole.command;

import java.io.File;
import java.io.FileFilter;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;

import toolbox.dbconsole.Command;
import toolbox.dbconsole.TestConsole;
import toolbox.dbconsole.TestEnvironment;
import toolbox.dbconsole.util.DataSet;
import toolbox.dbconsole.util.DataSuite;

/**
 * Executes set of sql files in a specified directory.
 */
public class LoadDirCommand implements Command {

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Output stream.
     */
    private PrintStream ps;

    /**
     * Test environment for this command.
     */
    private TestEnvironment testEnv;

    /**
     * Valid list of datasets
     */
    private Map suites;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a LoadCommand.
     */
    public LoadDirCommand() {
        suites = new HashMap();
    }

    //--------------------------------------------------------------------------
    // Command Interface
    //--------------------------------------------------------------------------

    /*
     * @see toolbox.dbconsole.Command#getName()
     */
    public String getName() {
        return "loaddir";
    }


    /*
     * @see toolbox.dbconsole.Command#getDescription()
     */
    public String getDescription() {
        return "Loads a given set DIRECTORY of test data ";
    }


    /*
     * @see toolbox.dbconsole.Command#execute(toolbox.dbconsole.TestConsole, java.lang.Object[])
     */
    public void execute(TestConsole console, Object[] args) throws Exception {

        ps = console.getPrintStream();
        testEnv = console.getTestEnv();

        if (testEnv == null) {
            ps.println("Set the test environment first using 'setenv'");
            return;
        }

        switch (args.length) {
            case 0 : printHelp(ps); break;
            default: handleCommand(args); break;
        }
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Build list of SQL files to load for each named set of round data.
     */
    protected String buildDataSets(Object[] dataSuites) throws Exception {

        File aFile = new File(".");
        ps.println("The base Dir is: "+ aFile.getCanonicalPath());
        File path = new File(String.valueOf(dataSuites[0]));
        File[] list;
        list = path.listFiles(new SQLFileFilter());
        String[] strList = new String[list.length];
        Arrays.sort(list);
        for(int i = 0; i < list.length; i++) {
            strList[i] = list[i].getAbsoluteFile().toString();
            ps.println("File "+i+": "+list[i].getAbsoluteFile().toString());
        }
        
        // TODO: Fix to iterate over dbs
        DataSet dirDataset =
            new DataSet(testEnv, testEnv.getDatabases()[0], strList);

        DataSuite dirSuite = new DataSuite("dirSuite", testEnv, 
            new String[] {"/resources/data/bigpop/bigpop.properties"});
        
        dirSuite.addDataSet(dirDataset);
        suites.put(dirSuite.getName(), dirSuite);
        return "dirSuite";
    }


    /**
     * Loads the given datasuites into the database.
     *
     * @param dataSuite Name of the data suite.
     */
    protected void handleCommand(Object[] dataSuites) throws Exception {

        // Initialize the files that make up the datasets
        String dataSuiteName = buildDataSets(dataSuites);

        StopWatch watch = new StopWatch();
        watch.start();

        // Verify all data set names are valid

        if (!suites.containsKey(dataSuiteName)) {
            ps.println("ERROR: Invalid dataset: " + dataSuiteName);
            printHelp(ps);
            return;
        }

        // Execute as an single unit
        DataSuite suite = (DataSuite) suites.get(dataSuiteName);
        suite.execute();

        watch.stop();
        ps.println("Loaded in: " + watch +" @ "+new Date().toString());
    }


    /**
     * Prints out help for this command.
     *
     * @param ps Print destination.
     */
    protected void printHelp(PrintStream ps) {
        ps.println();
        ps.println("Loads .sql files in the specified directory into the database.");
        ps.println("Usage    : loaddir <path>");
        ps.println("Example  : loaddir c:\\sql");
    }

    // TODO: Replace with  org.apache.commons.io.filefilter.SuffixFileFilter
    
    class SQLFileFilter implements FileFilter {
        public boolean accept(File pathname) {
            if (pathname.getName().endsWith(".sql")) {
                return true;
            }
            return false;
        }
        
        public boolean accept(File pathname, String aStr) {
            if (aStr.endsWith(".sql")) {
                return true;
            }
            return false;
        }
        
    }

}