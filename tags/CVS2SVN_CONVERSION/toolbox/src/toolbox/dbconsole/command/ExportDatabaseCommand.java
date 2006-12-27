package toolbox.dbconsole.command;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.ForwardOnlyResultSetTableFactory;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.oracle.OracleConnection;


import toolbox.dbconsole.Command;
import toolbox.dbconsole.TestConsole;
import toolbox.dbconsole.TestDatabase;
import toolbox.dbconsole.TestEnvironment;
import toolbox.dbconsole.util.CountingOutputStream;
import toolbox.util.FileUtil;
import toolbox.util.JDBCSession;
import toolbox.util.StringUtil;

/**
 * Exports a database to a flatfile.
 */
public class ExportDatabaseCommand implements Command {

    private static final Log logger =
        LogFactory.getLog(ExportDatabaseCommand.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Test environment to copy from.
     */
    private TestEnvironment sourceEnv;

    /**
     * Reference to the console which invoked this command.
     */
    private TestConsole console;

    /**
     * Keep the temp files after an export?
     */
    private boolean keepTempFiles;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a ExportDatabaseCommand.
     * <p>
     * Defaults are:
     * <ul>
     *   <li>temp files are not kept
     * </ul>
     */
    public ExportDatabaseCommand() {
        setKeepTempFiles(true);
    }

    //--------------------------------------------------------------------------
    // Command Interface
    //--------------------------------------------------------------------------

    /*
     * @see toolbox.dbconsole.Command#getName()
     */
    public String getName() {
        return "exportdb";
    }

    /*
     * @see toolbox.dbconsole.Command#getDescription()
     */
    public String getDescription() {
        return "Exports the contents of one database to a flat file.";
    }

    /*
     * @see toolbox.dbconsole.Command#execute(toolbox.dbconsole.TestConsole, java.lang.Object[])
     */
    public void execute(TestConsole console, Object[] args) throws Exception {

        setConsole(console);

        if (!parseArgs((String[]) args))
            return;

        logger.debug(StringUtil.banner(
            "Options:\n"
            + "  keepTempFiles      = " + isKeepTempFiles() + "\n"));

        // Time the whole thing...
        StopWatch watch = new StopWatch();
        watch.start();

        TestDatabase[] dbs = getSourceEnv().getDatabases();
        
        for (int i = 0; i < dbs.length; i++) {
            File f = exportDatabase(dbs[i]);
            console.getPrintStream().println(
                "Exported db "
                    + dbs[i].getName()
                    + " to "
                    + f.getCanonicalPath());
        }

        watch.stop();
        logger.debug("Export db completed in " + watch.toString());
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Exports a database to temporary file and returns the file handle. The
     * caller is responsible for deleting the temp file.
     *
     * @param source Database to export.
     * @return File
     * @throws Exception on error.
     * @throws SQLException on sql related error.
     * @throws DataSetException on dbunit dataset error.
     * @throws IOException on I/O error.
     * @throws FileNotFoundException on missing file error.
     */
    protected File exportDatabase(TestDatabase source)
        throws
            Exception,
            SQLException,
            DataSetException,
            IOException,
            FileNotFoundException {

        logger.debug("Exporting " + source);
        
        StopWatch exportTimer = new StopWatch();
        exportTimer.start();

        // Set the active environment
        new SetEnvCommand().execute(
            getConsole(),
            new String[] { getSourceEnv().getName() });

        Connection jdbcConnection = JDBCSession.getConnection(source.getName());

        IDatabaseConnection connection =
            new OracleConnection(jdbcConnection, source.getAdminUser());

        // Enable batch statements to speedup inserts
        connection.getConfig().setFeature(
            DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);

        // Use forward only result sets for speed...
        connection.getConfig().setProperty(
            DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY,
            new ForwardOnlyResultSetTableFactory());

        connection.getConfig().setFeature(
            DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, false);

        // Use filter to make sure data is exported in order of fk dependencies
        ITableFilter filter = new DatabaseSequenceFilter(connection);

        // Full database export
        IDataSet fullDataSet =
            new FilteredDataSet(filter, connection.createDataSet());

        //
        // BOTTLENECK: Getting row count causes all data to be transferred.
        //             Only use for debugging.

        
//        for (ITableIterator i = fullDataSet.iterator(); i.next(); ) {
//            ITable table = i.getTable();
//
//            logger.debug(
//                "Table: "
//                + table.getTableMetaData().getTableName()
//                + " Rows: "
//                + table.getRowCount());
//        }
        

        File exportFile = FileUtil.createTempFile();
        FileOutputStream fos = new FileOutputStream(exportFile);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        // Print out progress every half meg of data exported...
        CountingOutputStream cos = new CountingOutputStream(bos, 500000);

        logger.debug(StringUtil.banner(
            "Exporting "
            + source.getName()
            + ":"
            + source.getUsername()
            + " to "
            + exportFile + "..."));

        FlatXmlDataSet.write(fullDataSet, cos);
        cos.close();

        exportTimer.stop();

        logger.debug(StringUtil.banner(
            "Exported source database "
            + source.getName()
            + " to "
            + cos.getCount()
            + " bytes in "
            + exportTimer));
        return exportFile;
    }


    /**
     * Parses the command line options and arguments to this command.
     *
     * @param args Array of options and arguments.
     * @throws ParseException on invalid command line arguments.
     */
    protected boolean parseArgs(String args[]) throws ParseException {

        PrintStream out = getConsole().getPrintStream();
        CommandLineParser parser = new PosixParser();
        Options options = new Options();

        Option keepTempFiles = new Option(
            "k",
            "keepTempFiles",
            false,
            "Keeps temp files during export instead of deleting them");

        Option helpOption = new Option(
            "h",
            "help",
            false,
            "Prints usage");

        Option helpOption2 = new Option(
            "?",
            "?",
            false,
            "Prints usage");

        options.addOption(helpOption2);
        options.addOption(helpOption);
        options.addOption(keepTempFiles);

        CommandLine cmdLine = parser.parse(options, args, true);

        for (Iterator i = cmdLine.iterator(); i.hasNext();) {
            
            Option option = (Option) i.next();
            String opt = option.getOpt();

            if (opt.equals(keepTempFiles.getOpt())) {
                setKeepTempFiles(true);
            }
            else if (opt.equals(helpOption.getOpt())
                || opt.equals(helpOption2.getOpt())) {
                
                // printHelp(out);
                printHelp(options);
                
                // throw new ParseException("help");
                return false;
            }
            else {
                throw new IllegalArgumentException("Option "
                    + opt
                    + " not understood.");
            }
        }

        // Parse test environment names after options...
        String[] envArgs = cmdLine.getArgs();

        //logger.debug("Leftover args = " + ArrayUtil.toString(envArgs));

        switch (envArgs.length)
        {
            // Two is the only valid number of args

            case 1  :

                String sourceEnvName = envArgs[0].toString();
                //String destEnvName = envArgs[1].toString();

                // Validate source environment name
                TestEnvironment source =
                    SetEnvCommand.getEnvironment(sourceEnvName);

                if (source == null) {
                    out.println(
                        "Environment '"
                        + sourceEnvName
                        + "' does not exist.");
                    return false;
                }
                else {
                    setSourceEnv(source);
                }

//                // Check for copying onto self
//                if (destEnvName.equals(sourceEnvName)) {
//                    out.println(
//                        "Cannot copy "
//                        + sourceEnvName
//                        + " onto itself.");
//                    return false;
//                }

                break;


            default :

                out.println("ERROR: Invalid test environment names");
                printHelp(options);
                return false;
        }

        return true;
    }

    /**
     * Prints out help for this command to the console.
     *
     * @param options Command line options.
     */
    protected void printHelp(Options options) {

        String header = "\n"
          + "Exports the databases from one test environment               \n"
          + "to another. This operation can be quite lengthy and requires  \n"
          + "that the temp directory on the local machine has plenty of    \n"
          + "space for temporary storage.                                  \n";

        HelpFormatter hf = new HelpFormatter();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        hf.printHelp(
            pw,
            80,
            "copydb <src env> <dest env>",
            header,
            options,
            2,
            1,
            "",
            true);

        getConsole().getPrintStream().println(sw.toString());
    }

   //--------------------------------------------------------------------------
    // Accessors/Mutators
    //--------------------------------------------------------------------------

    /**
     * Returns the console.
     *
     * @return TestConsole
     */
    public TestConsole getConsole() {
        return console;
    }

    /**
     * Sets the console.
     *
     * @param console Test console.
     */
    public void setConsole(TestConsole console) {
        this.console = console;
    }

    /**
     * Returns true to keep temp files generated by exporting a database to
     * file, false otherwise.
     *
     * @return boolean
     */
    public boolean isKeepTempFiles() {
        return keepTempFiles;
    }

    /**
     * Sets flag to keep temp files generated by exporting a database to file.
     *
     * @param b True to keep temp files, false otherwise.
     */
    public void setKeepTempFiles(boolean b) {
        keepTempFiles = b;
    }
    
    /**
     * Returns the source test environment.
     *
     * @return TestEnvironment
     */
    public TestEnvironment getSourceEnv() {
        return sourceEnv;
    }

    /**
     * Sets the source test environment.
     *
     * @param environment Source environment.
     */
    public void setSourceEnv(TestEnvironment environment) {
        sourceEnv = environment;
    }
}