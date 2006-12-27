package toolbox.dbconsole.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;


import toolbox.dbconsole.TestEnvironment;
import toolbox.util.ResourceUtil;

/**
 * A DataSuite is a uniquely named collection of DataSets.
 * A DataSuite is executed/loaded into a given TestEnvironment.
 * Since two given DataSets need not refer to the same TestDatabase,
 * a DataSuite can load data across multiple TestDatabases in a single
 * TestEnvironment.
 *
 * @see toolbox.dbconsole.util.DataSet
 */
public class DataSuite {
    
    private static final Log logger = LogFactory.getLog(DataSuite.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Unique name of this DataSuite.
     */
    private String name;

    /**
     * Collection of datasets that this suite is comprised of.
     */
    private List datasets;

    /**
     * Context for velocity templating engine.
     */
    private VelocityContext velocityContext;

    /**
     * Properties files containing suite static velocity properties.
     */
    private String[] propFiles;

    /**
     * Test environment in which to execute this DataSuite.
     */
    private TestEnvironment env;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a DataSuite.
     *
     * @param name Unique name of this data suite.
     * @param env Test environment against which to run this data suite against.
     * @param propFiles List of properties files containing velocity properties.
     */
    public DataSuite(String name, TestEnvironment env, String[] propFiles) {
        setName(name);
        this.env = env;
        this.propFiles = propFiles;
        datasets = new ArrayList();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Adds a dataset to this datasuite. Order is important!
     */
    public void addDataSet(DataSet ds) {
        datasets.add(ds);
    }


    /**
     * Adds all datasets in the given suite to this suite.
     *
     * @param suite Suite to copy datasets from.
     */
    public void addDataSets(DataSuite suite) {

        for (Iterator i = suite.getDataSets(); i.hasNext(); ) {
            DataSet ds = (DataSet) i.next();
            addDataSet(ds);
        }
    }


    /**
     * Returns this suites datasets.
     *
     * @return Iterator
     */
    public Iterator getDataSets() {
        return datasets.iterator();
    }


    /**
     * Returns the name of this data suite.
     *
     * @return String
     */
    public String getName() {
        return name;
    }


    /**
     * Sets the name of this data suite.
     *
     * @param string Name
     */
    public void setName(String string) {
        name = string;
    }


    /**
     * Loads data from all contained datasets into their respective databases.
     * The load occurs in the order that the datasets where added to this
     * datasuite.
     */
    public void execute() {

        logger.debug("Executing DataSuite: " + getName());

        try {
            loadProperties();
        }
        catch (Exception e) {
            logger.error("loadProperties", e);
        }

        for (Iterator i = datasets.iterator(); i.hasNext();) {
            DataSet ds = (DataSet) i.next();
            logger.debug("  Applying Dataset to: " + ds.getDb().getName());
            ds.execute(velocityContext);
        }
    }


    /**
     * Loads velocity name value property pairs from the properties files
     * associated with this data suite.
     *
     * @throws Exception on error.
     */
    private void loadProperties() throws Exception {

        // Setup the velocity context

        Velocity.init();
        velocityContext = new VelocityContext();

        // The DB schema names cannot be hardcoded in the properties file
        // since the test environment is determined at runtime. Place vars
        // in the velocity context so they are picked up and replaced.

        // Fish out the properties file(s) from the list of files attached to
        // this dataset and insert the props into the velocity context.

        for (int i = 0; i < propFiles.length; i++) {

            String file = propFiles[i];

            if (file.endsWith(".properties")) {

                Properties p = new Properties();
                p.load(ResourceUtil.getResource(file));

                for (Iterator it = IteratorUtils.asIterator(p.keys());
                     it.hasNext(); ) {

                    String key = (String) it.next();
                    System.setProperty(key, p.getProperty(key));
                    velocityContext.put(key, p.getProperty(key));
                }
            }
        }
    }
}