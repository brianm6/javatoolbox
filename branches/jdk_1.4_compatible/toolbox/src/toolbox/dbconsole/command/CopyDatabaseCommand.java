package toolbox.dbconsole.command;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

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
import org.xml.sax.InputSource;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.ForwardOnlyResultSetTableFactory;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.dbunit.dataset.stream.StreamingDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.ext.oracle.OracleConnection;
import org.dbunit.operation.DatabaseOperation;

import toolbox.dbconsole.Command;
import toolbox.dbconsole.TestConsole;
import toolbox.dbconsole.TestDatabase;
import toolbox.dbconsole.TestEnvironment;
import toolbox.dbconsole.util.CountingOutputStream;
import toolbox.dbconsole.util.TimedOperation;
import toolbox.util.ArrayUtil;
import toolbox.util.FileUtil;
import toolbox.util.JDBCSession;
import toolbox.util.StringUtil;
import toolbox.util.db.oracle.OracleSequence;
import toolbox.util.db.oracle.OracleUtil;

/**
 * Copies the databases in a given TestEnvironment to another TestEnvironment.
 */
public class CopyDatabaseCommand implements Command {

    // TODO: Refactor to be a composite of the Import and Export database 
    //       commands.
    
    private static final Log logger =
        LogFactory.getLog(CopyDatabaseCommand.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Test environment to copy from.
     */
    private TestEnvironment sourceEnv;

    /**
     * Test environment to copy to.
     */
    private TestEnvironment destEnv;

    /**
     * Reference to the console which invoked this command.
     */
    private TestConsole console;

    /**
     * Keep the temp files after an export?
     */
    private boolean keepTempFiles;

    /**
     * Disable constraints before deleting or truncing tables and restore after.
     */
    private boolean disableConstraints;

    /**
     * Synchronizes database sequences also.
     */
    private boolean syncSequences;

    /**
     * Use truncate instead of delete for clearing out data (truncate is faster)
     * and also implies that the constraints will be disabled.
     */
    private boolean useTruncate;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a CopyDatabaseCommand.
     * <p>
     * Defaults are:
     * <ul>
     *   <li>temp files are not kept
     *   <li>constraints are disabled
     *   <li>sequences are synced
     *   <li>truncate is used instead of delete
     * </ul>
     */
    public CopyDatabaseCommand() {

        // Set the defaults
        setKeepTempFiles(false);
        setDisableConstraints(true);
        setSyncSequences(true);
        setUseTruncate(true);
    }

    //--------------------------------------------------------------------------
    // Command Interface
    //--------------------------------------------------------------------------

    /*
     * @see toolbox.dbconsole.Command#getName()
     */
    public String getName() {
        return "copydb";
    }


    /*
     * @see toolbox.dbconsole.Command#getDescription()
     */
    public String getDescription() {
        return "Copies the contents of one database to another.";
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
            + "  disableConstraints = " + isDisableConstraints() + "\n"
            + "  keepTempFiles      = " + isKeepTempFiles() + "\n"
            + "  syncSequences      = " + isSyncSequences() + "\n"
            + "  useTruncate        = " + isUseTruncate()));


        // Time the whole thing...
        StopWatch watch = new StopWatch();
        watch.start();

        // Copy db
        logger.debug(StringUtil.banner(
            "Copying db: "
            + getSourceEnv().getName() 
            + " -> "
            + getDestEnv().getName()));

        // TODO: Fix to iterate over all dbs
        
        copy(getSourceEnv().getDatabases()[0], getDestEnv().getDatabases()[0]);

        // Synchronize all database sequences between the source and destination
        // environments...
        if (isSyncSequences())
            syncSequences();
        else
            logger.debug("Skipping syncing of sequences...");

        watch.stop();
        logger.debug("Copy db completed in " + watch.toString());
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Prints out help for this command to the console.
     *
     * @param options Command line options.
     */
    protected void printHelp(Options options) {

        String header =
            ".\n"
          + "Copies the databases from one test environment\n"
          + "to another. This operation can be quite lengthy and requires  \n"
          + "that the temp directory on the local machine has plenty of    \n"
          + "space (~200MB) for temporary storage. Database sequences are  \n"
          + "also synchronized as part of this operation.                  \n"
          + ".                                                             \n";

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


    /**
     * Copies one database to another. Internally exports the source db to a
     * flatfile and them imports it from flatfile to the destination database.
     *
     * @param source Database in the test environment to copy.
     * @param dest Database in the destination test environment to copy to.
     * @throws Exception on error.
     */
    protected void copy(
        TestDatabase source,
        TestDatabase dest)
        throws Exception {

        File exportFile = exportDatabase(source);
        importDatabase(dest, exportFile);

        // Cleanup temp file...
        if (!isKeepTempFiles())
            exportFile.delete();
        else
            getConsole().getPrintStream().println(
                "Keeping "
                + source.getName()
                + " export file as "
                + exportFile.getAbsolutePath());
    }


    /**
     * Import a database from an XML file that was generated via
     * exportDatabase().
     *
     * @param dest Destination database.
     * @param exportFile XML file generated via exportDatabase().
     * @throws Exception on error.
     * @throws SQLException on sql error.
     * @throws DatabaseUnitException on dbunit error.
     */
    protected void importDatabase(
        TestDatabase dest,
        File exportFile)
        throws Exception, SQLException, DatabaseUnitException {

        // Set the active environment
        new SetEnvCommand().execute(
            getConsole(),
            new String[] { getDestEnv().getName() });

        Connection destJdbcConnection =
            JDBCSession.getConnection(dest.getName());

        // Notice admin user must be used...
        IDatabaseConnection destConnection =
            new OracleConnection(destJdbcConnection, dest.getAdminUser());

        // Enable batch statements to speedup inserts...
        destConnection.getConfig().setFeature(
            DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);

        // Use forward only result sets for speed...
        destConnection.getConfig().setProperty(
            DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY,
            new ForwardOnlyResultSetTableFactory());

        IDataSetProducer producer =
            new FlatXmlProducer(new InputSource(exportFile.getAbsolutePath()));

        // Use streaming so the entire dataset is not kept resident in memory...
        StreamingDataSet ds = new StreamingDataSet(producer);

        logger.debug(StringUtil.banner(
            "Importing "
            + exportFile
            + " to "
            + destEnv
            + ":"
            + dest.getName()
            + "..."));

        // Constraints must be disabled if using truncate!
        setDisableConstraints(isUseTruncate());

        if (isDisableConstraints())
            OracleUtil.setConstraintsEnabled(dest.getName(), false);

        if (isUseTruncate()) {
            // Truncate is faster than delete and no need to rollback but
            // requires a few more privileges if the database is locked down.
            new TimedOperation(
                "TRUNCATE",
                 DatabaseOperation.TRUNCATE_TABLE).execute(destConnection, ds);
        }
        else {
            // Delete takes too long even with the ref keys disabled but is
            // failsafe when permissions are a problem.
            new TimedOperation(
                "DELETE_ALL",
                DatabaseOperation.DELETE_ALL).execute(destConnection, ds);
        }

        if (isDisableConstraints())
            OracleUtil.setConstraintsEnabled(dest.getName(), true);

        // Create a new dataset cuz streaming ds can only create one iterator
        // ever

        ds = new StreamingDataSet(producer);

        new TimedOperation(
            "INSERT",
            DatabaseOperation.INSERT).execute(destConnection, ds);
    }


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

        StopWatch exportWatch = new StopWatch();
        exportWatch.start();

        // Set the active environment
        new SetEnvCommand().execute(
            getConsole(),
            new String[] { getSourceEnv().getName() });

        Connection jdbcConnection =JDBCSession.getConnection(source.getName());

        IDatabaseConnection connection =
            new OracleConnection(jdbcConnection, source.getAdminUser());

        // Enable batch statements to speedup inserts
        connection.getConfig().setFeature(
            DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);

        // Use forward only result sets for speed...
        connection.getConfig().setProperty(
            DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY,
            new ForwardOnlyResultSetTableFactory());

        //connection.getConfig().setFeature(
        //    DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);

        // Use filter to make sure data is exported in order of fk dependencies
        ITableFilter filter = new DatabaseSequenceFilter(connection);

        // Full database export
        IDataSet fullDataSet =
            new FilteredDataSet(filter, connection.createDataSet());

        //
        // BOTTLENECK: Getting row count causes all data to be transferred.
        //             Only use for debugging.

        /*
        for (ITableIterator i = fullDataSet.iterator(); i.next(); ) {
            ITable table = i.getTable();

            logger.debug(
                "Table: "
                + table.getTableMetaData().getTableName()
                + " Rows: "
                + table.getRowCount());
        }
        */

        File exportFile = FileUtil.createTempFile();
        FileOutputStream fos = new FileOutputStream(exportFile);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        // Print out progress every half meg of data exported...
        CountingOutputStream cos = new CountingOutputStream(bos, 500000);

        logger.debug(StringUtil.banner(
            "Exporting "
            + sourceEnv
            + ":"
            + source.getUsername()
            + " to "
            + exportFile + "..."));

        FlatXmlDataSet.write(fullDataSet, cos);
        cos.close();

        exportWatch.stop();

        logger.debug(StringUtil.banner(
            "Exported source database "
            + source.getName()
            + " to "
            + cos.getCount()
            + " bytes in "
            + exportWatch));
        return exportFile;
    }


    /**
     * Copies one database table to another.
     *
     * @param source Source database.
     * @param dest Destination database.
     * @param table Table name.
     * @throws Exception on error.
     */
    protected void copyTable(
        TestDatabase source,
        TestDatabase dest,
        String table)
        throws Exception {

        // Export

        Connection sourceJdbcConnection =
            JDBCSession.getConnection(source.getName());

        IDatabaseConnection sourceConnection =
            new OracleConnection(sourceJdbcConnection, source.getAdminUser());

        // Partial database export
        QueryDataSet partialDataSet = new QueryDataSet(sourceConnection);
        partialDataSet.addTable(table);

        // Export table - can get very large so export to a file
        File exportFile = FileUtil.createTempFile();
        Writer sw = new BufferedWriter(new FileWriter(exportFile));
        FlatXmlDataSet.write(partialDataSet, sw);
        sw.close();

        logger.debug("Wrote " + exportFile.length() + " bytes to " + exportFile);

        // Import

        Connection destJdbcConnection =
            JDBCSession.getConnection(dest.getName());

        IDatabaseConnection destConnection =
            new OracleConnection(destJdbcConnection, dest.getAdminUser());

        // Enable batch statements to speedup inserts
        destConnection.getConfig().setFeature(
            DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);

        FlatXmlDataSet ds = new FlatXmlDataSet(exportFile);
        DatabaseOperation.DELETE_ALL.execute(destConnection, ds);
        DatabaseOperation.INSERT.execute(destConnection, ds);
    }


    /**
     * SynchronizeSequences between the two test environments.
     *
     * @throws Exception on synchronization error.
     */
    protected void syncSequences() throws Exception {

        logger.debug(StringUtil.banner("Synchronizing sequences..."));

        TestDatabase[] sourceDbs = getSourceEnv().getDatabases();
        TestDatabase[] destDbs = getDestEnv().getDatabases();

        // Iterate over each source db and see if there are sequences to sync..
        for (int i = 0; i < sourceDbs.length; i++) {

            // Set the source env
            new SetEnvCommand().execute(
                getConsole(),
                new String[] { sourceEnv.getName() });

            TestDatabase sourceDb = sourceDbs[i];
            List sourceSequences = OracleUtil.getSequences(sourceDb.getName());

            // For each sequence found..retrieve from dest db and update value
            for (Iterator iter = sourceSequences.iterator(); iter.hasNext();) {
                OracleSequence sourceSequence = (OracleSequence) iter.next();

                // HACK ALERT: There is nothing that associates two databases
                //             between the two environments so they cannot be
                //             picked correctly. Assume the order of db
                //             returned from getDatabases() is the valid order
                //             for now.

                //TestDatabase destDb = destEnv.getDatabase(sourceDb.getName());
                TestDatabase destDb = destEnv.getDatabases()[i];

                if (destDb == null) {

                    logger.debug(ArrayUtil.toString(
                        destEnv.getDatabases(),
                        true));

                    throw new IllegalArgumentException(
                        "Destination db matching "
                        + sourceDb.getName()
                        + " could not be found!");
                }

                // Were're done with source db queries..switch over to the dest
                // env
                new SetEnvCommand().execute(
                    getConsole(),
                    new String[] { destEnv.getName() });

                // Retrieve the matching dest sequence
                OracleSequence destSequence =
                    OracleUtil.getSequence(
                        destDb.getName(),
                        sourceSequence.getName());

                logger.debug(StringUtil.banner(
                    "Updating sequence "
                    + destSequence.getName()
                    + " from "
                    + destSequence.getLastNumber()
                    + " to "
                    + sourceSequence.getLastNumber()
                    + " in "
                    + "[ENV:"
                    + destEnv.getName()
                    + "] [DB:"
                    + destDb.getName()
                    + "] [USER:"
                    + destDb.getUsername()
                    + "]..."));

                // Sync the dest to match the source
                OracleUtil.setSequenceValue(
                    destDb.getName(),
                    destSequence.getName(),
                    sourceSequence.getLastNumber());
            }
        }
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

        Option noTruncate = new Option(
            "t",
            "noTruncate",
            false,
            "Use delete instead of truncate to clear data");

        Option dontSyncSequences = new Option(
            "s",
            "dontSyncSequences",
            false,
            "Do not synchronize sequences between the databases");

        Option dontDisableConstraints = new Option(
            "c",
            "dontDisableConstraints",
            false,
            "Don't disable foreign key constraints when 'delete' is used to "
            + "clear the tables. Disregarded if truncate is enabled.");

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
        options.addOption(noTruncate);
        options.addOption(dontSyncSequences);
        options.addOption(dontDisableConstraints);

        CommandLine cmdLine = parser.parse(options, args, true);

        for (Iterator i = cmdLine.iterator(); i.hasNext();) {
            
            Option option = (Option) i.next();
            String opt = option.getOpt();

            if (opt.equals(keepTempFiles.getOpt())) {
                setKeepTempFiles(true);
            }
            else if (opt.equals(noTruncate.getOpt())) {
                setUseTruncate(false);
            }
            else if (opt.equals(dontSyncSequences.getOpt())) {
                setSyncSequences(false);
            }
            else if (opt.equals(dontDisableConstraints.getOpt())) {
                setDisableConstraints(false);
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

        switch (envArgs.length)  {
            
            // Two is the only valid number of args

            case 2  :

                String sourceEnvName = envArgs[0].toString();
                String destEnvName = envArgs[1].toString();

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

                // Validate destination environment name
                TestEnvironment dest =
                    SetEnvCommand.getEnvironment(destEnvName);

                if (dest == null) {
                    out.println(
                        "Environment '"
                        + destEnvName
                        + "' does not exist.");
                    return false;
                }
                else {
                    setDestEnv(dest);
                }

                // Check for copying onto self
                if (destEnvName.equals(sourceEnvName)) {
                    out.println(
                        "Cannot copy "
                        + sourceEnvName
                        + " onto itself.");
                    return false;
                }

                break;


            default :

                out.println("ERROR: Invalid test environment names: " + envArgs.length);
                printHelp(options);
                return false;
        }

        return true;
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
     * Returns the destination test environment.
     *
     * @return TestEnvironment
     */
    public TestEnvironment getDestEnv() {
        return destEnv;
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
     * Sets the destination test environment.
     *
     * @param environment Destination test environment.
     */
    public void setDestEnv(TestEnvironment environment) {
        destEnv = environment;
    }


    /**
     * Sets the source test environment.
     *
     * @param environment Source environment.
     */
    public void setSourceEnv(TestEnvironment environment) {
        sourceEnv = environment;
    }


    /**
     * Returns true if foreign key constraints are disabled in the destination
     * database before clearing all existing data via either a delete or
     * truncate sql operation, false otherwise.
     *
     * @return boolean
     */
    public boolean isDisableConstraints() {
        return disableConstraints;
    }


    /**
     * Returns true if oracle sequences are to be synchronized between the
     * source and destionation test environments, false otherwise.
     *
     * @return boolean
     */
    public boolean isSyncSequences() {
        return syncSequences;
    }


    /**
     * Returns true to use sql truncate instead of sql delete to clear data out
     * of the destination database. Delete requires less permissions than
     * truncate but truncate is faster.
     *
     * @return boolean
     */
    public boolean isUseTruncate() {
        return useTruncate;
    }


    /**
     * Sets the flag to disable forign key constraints during the delete or
     * truncate operation.
     *
     * @param b True to disable constraints, false otherwise.
     */
    public void setDisableConstraints(boolean b) {
        disableConstraints = b;
    }


    /**
     * Sets the flag to synchronize sequences between the test environments.
     *
     * @param b True to sync sequences, false otherwise.
     */
    public void setSyncSequences(boolean b) {
        syncSequences = b;
    }


    /**
     * Sets the flag to use truncate instead of delete to empty the destination
     * database.
     *
     * @param b True to use truncate, false othewise.
     */
    public void setUseTruncate(boolean b) {
        useTruncate = b;
    }
}