package toolbox.dbconsole.command;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;

import toolbox.dbconsole.Command;
import toolbox.dbconsole.TestConsole;
import toolbox.dbconsole.TestEnvironment;
import toolbox.dbconsole.util.DataSuite;

/**
 * Loads test data
 */
public class LoadCommand implements Command {

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
    public LoadCommand() {
        suites = new HashMap();
    }

    //--------------------------------------------------------------------------
    // Command Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.dbconsole.Command#getName()
     */
    public String getName() {
        return "load";
    }


    /**
     * @see toolbox.dbconsole.Command#getDescription()
     */
    public String getDescription() {
        return "Loads a given set of test data (round1, round2, etc).";
    }


    /**
     * @see toolbox.dbconsole.Command#execute(
     *      toolbox.dbconsole.TestConsole, java.lang.Object[])
     */
    public void execute(TestConsole console, Object[] args) throws Exception {

        ps = console.getPrintStream();
        testEnv = console.getTestEnv();

        if (testEnv == null) {
            ps.println("Set the test environment first using 'setenv'");
            return;
        }

        // Initialize the files that make up the datasets
        buildDataSets();

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
    protected void buildDataSets() {

        // Directories in an archive cannot be queried from a WebStart app
        // so the file names and paths have to be predetermined and specified
        // explicitly.

//        // ---------------------------------------------------------------------
//        // Static Dataset
//        // ---------------------------------------------------------------------
//
//        DataSet resourcesStaticDataset =
//            new DataSet(testEnv, testEnv.getresourcesDatabase(), new String[] {
//				"/resources/data/static/00_DELETE_DATA.sql",
//				"/resources/data/static/01_AUCT_ST.sql",
//				"/resources/data/static/02_CRIT_TYPE.sql",
//				"/resources/data/static/03_BID_ST.sql",
//				"/resources/data/base/01_AUCT_SRCE.sql",
//               "/resources/data/static/00_DELETE_DATA.sql",
//               "/resources/data/static/01_STATIC_CODES.sql",
//               });
//
//        DataSuite staticSuite = new DataSuite("static", testEnv, new String[0]);
//        staticSuite.addDataSet(resourcesStaticDataset);
//		suites.put(staticSuite.getName(), staticSuite);
//
//        // ---------------------------------------------------------------------
//        // Base Dataset
//        // ---------------------------------------------------------------------
//
//        DataSet resourcesBaseDataset =
//            new DataSet(testEnv, testEnv.getresourcesDatabase(), new String[] {
////			"/resources/data/base/01_AUCT_SRCE.sql",
//			"/resources/data/base/02_AUCT_RULE_CRIT.sql",
//			"/resources/data/base/03_CFG_CRIT.sql",
//			"/resources/data/base/04_AUCT.sql",
//			"/resources/data/base/05_AUCT_CFG_CRIT.sql",
//			"/resources/data/base/06_AUCT_RULE_CRIT__AUCT.sql",
//            "/resources/data/base/01_VCNY_AUCT.sql",
//            "/resources/data/base/02_VCNY_AUCT_PARM.sql",
//            "/resources/data/base/03_VCNY_AUCT_PARM_VCNY_AUCT.sql"});
//
//        DataSuite baseSuite = new DataSuite("base", testEnv,
//			new String[] {"/resources/data/base/base.properties"});
//		baseSuite.addDataSets(staticSuite);
//        baseSuite.addDataSet(resourcesBaseDataset);
//        suites.put(baseSuite.getName(), baseSuite);
//
//		// ---------------------------------------------------------------------
//		// Award Base Dataset
//		// ---------------------------------------------------------------------
//
//		DataSet awardBaseDataset =
//			new DataSet(testEnv, testEnv.getresourcesDatabase(), new String[] {
//			"/resources/data/awardbase/01_AUCT.sql",
//			"/resources/data/awardbase/01a_AUCT_RULE_CRIT.sql",
//			"/resources/data/awardbase/01b_CFG_CRIT.sql",
//			"/resources/data/awardbase/02_AUCT_CFG_CRIT.sql",
//			"/resources/data/awardbase/03_AUCT_RULE_CRIT__AUCT.sql",
//			"/resources/data/awardbase/04_BID_ITM.sql",
//			"/resources/data/awardbase/05_OFF_BID.sql",
//			"/resources/data/awardbase/06_OFF_BID_ST.sql",
//			"/resources/data/awardbase/00_DELETE_STN.sql",
//			"/resources/data/awardbase/01_VCNY_AUCT.sql",
//			"/resources/data/awardbase/01a_VCNY_AUCT_PARM.sql",
//			"/resources/data/awardbase/02_VCNY_AUCT_PARM_VCNY_AUCT.sql",
//			"/resources/data/awardbase/03_WRKR.sql",
//			"/resources/data/awardbase/04_VCNY_BID_ITM.sql",
//			"/resources/data/awardbase/05_VCNY_OFF_BID.sql",
//			"/resources/data/awardbase/06_WRKR_PROF.sql",
//			"/resources/data/awardbase/07_WRKR_VCNY_STAT.sql",
//			"/resources/data/awardbase/07root_WRKR_VCNY_STAT.sql",
//			"/resources/data/awardbase/08_VCNY_OFF_BID_ITM.sql",
//			"/resources/data/awardbase/09_VCNY_DMCL_POP.sql"
//			});
//
//		DataSuite awardbaseSuite = new DataSuite("awardbase", testEnv,
//			new String[] {"/resources/data/bigpop/bigpop.properties"});
//		awardbaseSuite.addDataSets(staticSuite);
//		awardbaseSuite.addDataSet(resourcesBaseDataset);
//		awardbaseSuite.addDataSet(awardBaseDataset);
//		suites.put(awardbaseSuite.getName(), awardbaseSuite);
    };


    /**
     * Loads the given datasuites into the database.
     *
     * @param dataSuites Names of the data suite.
     */
    protected void handleCommand(Object[] dataSuites) {

        StopWatch watch = new StopWatch();
        watch.start();

        // Verify all data set names are valid
        for (int i = 0; i < dataSuites.length; i++) {
            String suiteName = dataSuites[i].toString().toLowerCase();

            if (!suites.containsKey(suiteName)) {
                ps.println("ERROR: Invalid dataset: " + suiteName);
                printHelp(ps);
                return;
            }
        }

        // Execute as an single unit
        for (int i = 0; i < dataSuites.length; i++) {
            String suiteName = dataSuites[i].toString().toLowerCase();
            DataSuite suite = (DataSuite) suites.get(suiteName);
            suite.execute();
        }

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
        ps.println("Loads test data into the database.");
        ps.println("The static and base sets of data are loaded by default.");
        ps.println("Usage    : load <dataset1> [<datasetx>]");
        ps.println("Example  : load round2");
        ps.println("Datasets : ");

        List names = new ArrayList(suites.keySet());
        Collections.sort(names);

        for (Iterator i = names.iterator(); i.hasNext();) {
            ps.println("           " + i.next());
        }
    }
}