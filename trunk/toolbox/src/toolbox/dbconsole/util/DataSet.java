package toolbox.dbconsole.util;

import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;


import toolbox.dbconsole.TestDatabase;
import toolbox.dbconsole.TestEnvironment;
import toolbox.util.JDBCSession;
import toolbox.util.ResourceUtil;
import toolbox.util.StringUtil;

/**
 * DataSet refers to a collection of files containing SQL statments to execute
 * against a single test database.
 *
 * @see toolbox.dbconsole.util.DataSuite
 */
public class DataSet
{
    private static final Log logger = LogFactory.getLog(DataSet.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Database to apply the sql statements to.
     */
    private TestDatabase db;

    /**
     * List of files containing the sql statements to execute.
     */
    private List files;

    /**
     * Velocity context that performs variable substitution.
     */
    private VelocityContext velocityContext;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a DataSet.
     *
     * @param env Test environment.
     * @param db Database to load the data into.
     * @param sqlFiles Array of filenames in absolute resource form containing
     *        sql statements to execute.
     */
    public DataSet(TestEnvironment env, TestDatabase db, String[] sqlFiles) {

        setDb(db);
        files = new ArrayList();

        for (int i = 0; i < sqlFiles.length; i++)
            addFile(sqlFiles[i]);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Adds a file to the list of files to execute.
     *
     * @param file File containing sql to execute.
     */
    public void addFile(String file) {
        files.add(file);
    }


    /**
     * Loads the dataset into the database.
     */
    public void execute(VelocityContext context) {

        velocityContext = context;

        try {
            for (Iterator i = files.iterator(); i.hasNext();)
                loadSQL(i.next().toString());
        }
        catch (Exception e) {
            logger.error("failed to load properties and sql", e);
        }
    }


    /**
     * loadSQL
     *
     * @param file
     */
    private void loadSQL(String file) {

        String contents = ResourceUtil.getResourceAsString(file);

        // Apply the velocity template to the SQL file
        try {
            contents = applyTemplate(contents);
        }
        catch (Exception e) {
            logger.error("DataSet::execute()", e);
        }

        //logger.debug(StringUtil.banner(contents));

        // Split file up into individual sql statements

        for (StringTokenizer st = new StringTokenizer(contents, ";");
             st.hasMoreTokens(); ) {

            String stmt = null;

            try {
                stmt = st.nextToken().trim();

                // Filter out comments
                StringBuffer sb = new StringBuffer();

                StringTokenizer lt = new StringTokenizer(stmt, "\n");

                while (lt.hasMoreTokens()) {

                    String line = lt.nextToken();
                    if (!StringUtils.isBlank(line) && !line.startsWith("--"))
                        sb.append(line + "\n");
                }

                stmt = sb.toString();

                if (StringUtils.isBlank(stmt))
                    continue;

                //logger.debug("||| " + stmt);
                JDBCSession.executeUpdate(db.getName(), stmt);
            }
            catch (SQLException se) {

                String s =
                    se.getMessage()
                    + "\n\nDB  : "
                    + db.getName()
                    + "\nFile: "
                    + file
                    + "\n\n"
                    + stmt;

                logger.error(StringUtil.banner(s));
            }
        }
    }


    /**
     * Applies the velocity template to the given string and returns the
     * resulting string.
     *
     * @param contents String to apply the template to.
     * @return String
     */
    public String applyTemplate(String contents) throws Exception {

        StringWriter output = new StringWriter();

        //logger.debug(StringUtil.banner("IN\n" + contents));

        boolean success =
            Velocity.evaluate(velocityContext, output, "dataset",  contents);

        //logger.debug(StringUtil.banner("OUT\n" + output.toString()));

        if (!success)
            logger.error("Application of velocity template failed!");

        return output.toString();
    }


    /**
     * Returns the test database to load the dataset into.
     *
     * @return TestDatabase
     */
    public TestDatabase getDb() {
        return db;
    }


    /**
     * Sets the test database to load the dataset into.
     *
     * @param database Test Database.
     */
    public void setDb(TestDatabase database) {
        db = database;
    }
}