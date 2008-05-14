package toolbox.util;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import toolbox.showclasspath.Main;
import toolbox.util.io.StringOutputStream;
import toolbox.util.random.IntSequence;
import toolbox.util.random.SequenceEndedException;

/**
 * Unit test for {@link toolbox.util.JDBCUtil}. This test case uses an 
 * in-process file instance of HSQLDB for a test database.
 */
public class JDBCUtilTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(JDBCUtilTest.class);

    // --------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    private static String FS          = File.separator;
    private static String DB_JAR      = "hsqldb.jar";
    private static String DB_DRIVER   = "org.hsqldb.jdbcDriver";
    private static String DB_USER     = "SA";
    private static String DB_PASSWORD = "";
    private static String DB_URL      = "jdbc:hsqldb:" + FileUtil.getTempDir() + FS;
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    private IntSequence rand_ = new IntSequence(1, 1000, true);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    public static void main(String[] args)
    {
        TestRunner.run(JDBCUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests init() without connection pooling.
     *
     * @throws Exception on error.
     */
    public void testInit() throws Exception
    {
        logger_.info("Running testInit...");

        try
        {
            JDBCUtil.init(DB_DRIVER, DB_URL, DB_USER, DB_PASSWORD, false);
            // No exception thrown = test passed
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
        finally
        {
            JDBCUtil.shutdown();
        }
    }

    
    /**
     * Tests init() with connection pooling
     * 
     * @throws Exception on error.
     */
    public void testInitWithPooling() throws Exception 
    {
        logger_.info("Running testInitWithPooling...");

        String prefix = nextPrefix("initWithPooling");
        
        try 
        {
            JDBCUtil.init(
                DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD, true);
            
            // No exception thrown = test passed
        }
        catch (Exception e) 
        {
            fail(e.getMessage());
        }
        finally 
        {
            JDBCUtil.shutdown();
            cleanup(prefix);
        }
    }
    

    /**
     * Stress tests init()/shutdown() with connection pooling
     * 
     * @throws Exception on error.
     */
    public void testStressInitShutdownWithPooling() throws Exception 
    {
        logger_.info("Running testInitShutdWithPooling...");

        int max = 100;
        
        for (int i = 0; i < max; i++) 
        {
            String table = "table99";
            String prefix = nextPrefix("JDBCUtilTest-StressInit-");
            
            JDBCUtil.init(
                DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD, true);
            
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");
            JDBCUtil.executeUpdate("insert into " + table + " (id)values(99)");
            
            JDBCUtil.executeUpdate(
                "update " + table + " set id=999 where id=99");
            
            JDBCUtil.executeUpdate("delete from " + table + " where id=999");
            JDBCUtil.executeQuery("select * from " + table);
            JDBCUtil.dropTable(table);
            JDBCUtil.shutdown();
            cleanup(prefix);
            logger_.debug("Stress loop : " + i);
        }
    }
    
    
    /**
     * Tests init() loading the jdbc driver from a jar file.
     */
    public void testInitUsingJar() throws Exception {
        logger_.info("Running testInitUsingJar...");
        String prefix = nextPrefix("initUsingJar");
        String dbJarFile = getDBJar();

        // Skip test if jar file not found..
        if (dbJarFile == null) {
            logger_.debug("Skipping testInitUsingJar...");
        }
        else {
            try {
                logger_.debug("Test jar file = " + dbJarFile);
                JDBCUtil.init(dbJarFile, DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
            }
            finally {
                JDBCUtil.shutdown();
                cleanup(prefix);
            }
        }
    }

    public void testGetConnection() throws Exception
    {
        logger_.info("Running testGetConnection...");

        String prefix = nextPrefix("JDBCUtilTest_GetConnection");

        try
        {
            JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
            Connection conn = JDBCUtil.getConnection();
            logger_.debug("Connection: " + conn);
            logger_.debug("Autocommit: " + conn.getAutoCommit());
            assertNotNull(conn);
            JDBCUtil.releaseConnection(conn);
        }
        finally
        {
            JDBCUtil.shutdown();
            cleanup(prefix);
        }
    }


    /**
     * Tests getConnection() failure because not initialized.
     *
     * @throws Exception on error.
     */
    public void testGetConnectionFailure() throws Exception
    {
        logger_.info("Running testGetConnectionFailure...");

        try
        {
            JDBCUtil.getConnection();
            fail("getConnection() should have failed because not init()'ed");
        }
        catch (IllegalArgumentException ise)
        {
            //
            // Check error message for mentioning of the init() method
            //
            //assertTrue(ise.getMessage().indexOf("init()") >= 0);
        }
    }


    /**
     * Tests executeQuery() on an empty table.
     *
     * @throws Exception on error.
     */
    public void testExecuteQueryZero() throws Exception
    {
        logger_.info("Running testExecuteQueryZero...");

        String prefix = nextPrefix("JDBCUtilTest_QueryExecZero");
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_zero";

        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");
            String results = JDBCUtil.executeQuery("select * from " + table);
            assertTrue(results.indexOf("0 rows") >= 0);
            logger_.debug("\n" + results);
        }
        finally
        {
            JDBCUtil.dropTable(table);
            JDBCUtil.shutdown();
            cleanup(prefix);
        }
    }


    /**
     * Tests executeQuery() on table with only one row of data.
     *
     * @throws Exception on error.
     */
    public void testExecuteQueryOne() throws Exception
    {
        logger_.info("Running testExecuteQueryOne...");

        String prefix = nextPrefix("JDBCUtilTest_QueryExecOne_");
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_one";

        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");
            JDBCUtil.executeUpdate("insert into " + table + "(id) values(999)");
            String results = JDBCUtil.executeQuery("select * from " + table);
            assertTrue(results.indexOf("1 rows") >= 0);
            assertTrue(results.indexOf("999") >= 0);
            logger_.debug("\n" + results);
        }
        finally
        {
            JDBCUtil.dropTable(table);
            JDBCUtil.shutdown();
            cleanup(prefix);
        }
    }


    /**
     * Tests executeQuery() on table with many rows of data.
     *
     * @throws Exception on error.
     */
    public void testExecuteQueryMany() throws Exception
    {
        logger_.info("Running testExecuteQueryMany...");

        String prefix = nextPrefix("JDBCUtilTest_QueryExecMany");
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_many";

        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");

            for (int i = 0; i < 100; i++)
                JDBCUtil.executeUpdate(
                    "insert into " + table + "(id) values (" + i + ")");

            String results = JDBCUtil.executeQuery("select * from " + table);
            assertTrue(results.indexOf("100 rows") >= 0);
            logger_.debug("\n" + StringUtil.wrap(results.replace('\n', ' ')));
        }
        finally
        {
            JDBCUtil.dropTable(table);
            JDBCUtil.shutdown();
            cleanup(prefix);
        }
    }


    /**
     * Tests executeQueryArray() on an empty table.
     *
     * @throws Exception on error.
     */
    public void testExecuteQueryArrayZero() throws Exception
    {
        logger_.info("Running testExecuteQueryArrayZero...");

        String prefix = nextPrefix("JDBCUtilTest_QryExecAryZero");
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_zero";

        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");

            Object[][] results =
                JDBCUtil.executeQueryArray("select * from " + table);

            // Column headers still occupy one row
            assertEquals(0, results.length);
        }
        finally
        {
            JDBCUtil.dropTable(table);
            JDBCUtil.shutdown();
            cleanup(prefix);
        }
    }


    /**
     * Tests executeQueryArray() on table with only one row of data.
     *
     * @throws Exception on error.
     */
    public void testExecuteQueryArrayOne() throws Exception
    {
        logger_.info("Running testExecuteQueryArrayOne...");

        String prefix = nextPrefix("JDBCUtilTest_QryExecAryOne");
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_one";

        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");
            
            JDBCUtil.executeUpdate(
                "insert into " + table + "(id) values (999)");

            Object[][] results =
                JDBCUtil.executeQueryArray("select * from " + table);

            assertEquals(1, results.length);
            assertEquals(1, results[0].length);
            assertEquals(new Integer(999), results[0][0]);
        }
        finally
        {
            JDBCUtil.dropTable(table);
            JDBCUtil.shutdown();
            cleanup(prefix);
        }
    }


    /**
     * Tests executeQueryArray() on table with many rows of data.
     *
     * @throws Exception on error.
     */
    public void testExecuteQueryArrayMany() throws Exception
    {
        logger_.info("Running testExecuteQueryArrayMany...");

        String prefix = nextPrefix("JDBCUtilTest_ExecQryAryMany");
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_many";
        int MAX = 100;
        
        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");

            for (int i = 0; i < MAX; i++)
                JDBCUtil.executeUpdate(
                    "insert into " + table + "(id) values (" + i + ")");

            Object[][] results =
                JDBCUtil.executeQueryArray("select * from " + table);

            assertEquals(MAX, results.length);

            for (int i = 0; i < MAX; i++)
            {
                assertEquals(1, results[i].length);
                assertEquals(new Integer(i), results[i][0]);
            }
        }
        finally
        {
            JDBCUtil.dropTable(table);
            JDBCUtil.shutdown();
            cleanup(prefix);
        }
    }


    /**
     * Tests executeUpdate() for INSERT, UPDATE, and DELETE.
     *
     * @throws Exception on error.
     */
    public void testExecuteUpdate() throws Exception
    {
        logger_.info("Running testExecuteUpdate...");

        String prefix = nextPrefix("JDBCUtilTest_ExecUpdates");
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_execute_update";

        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");

            assertEquals(1,
                JDBCUtil.executeUpdate(
                    "insert into " + table + " (id) values(100)"));

            assertEquals(1,
                JDBCUtil.executeUpdate(
                    "update " + table + " set id=999 where id=100"));

            assertEquals(1,
                JDBCUtil.executeUpdate(
                    "delete from " + table + " where id=999"));

            String results = JDBCUtil.executeQuery("select * from " + table);
            assertTrue(results.indexOf("0 rows") >= 0);
            logger_.debug("\n" + results);
        }
        finally
        {
            JDBCUtil.dropTable(table);
            JDBCUtil.shutdown();
            cleanup(prefix);
        }
    }
    

    /**
     * Tests getSize() on an empty table.
     *
     * @throws Exception on error.
     */
    public void testGetSizeZero() throws Exception
    {
        logger_.info("Running testGetSizeZero...");

        String prefix = nextPrefix("JDBCUtilTest_GetSizeZero_");
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_zero";
        Connection conn = null;
        ResultSet results = null;

        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");
            conn = JDBCUtil.getConnection();

            Statement s = conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

            results = s.executeQuery("select * from " + table);

            int size = JDBCUtil.getSize(results);

            logger_.debug("Resultset size: " + size);
            assertEquals("size shold be zero", 0, size);

        }
        finally
        {
            JDBCUtil.close(results);
            JDBCUtil.releaseConnection(conn);
            JDBCUtil.dropTable(table);
            JDBCUtil.shutdown();
            cleanup(prefix);
        }
    }

    
    /**
     * Tests executeCount()
     *
     * @throws Exception on error.
     */
    public void testExecuteCount() throws Exception
    {
        logger_.info("Running testExecuteCount...");

        String prefix = nextPrefix("JDBCUtilTest_ExecuteCount");
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_exec_cnt";

        try
        {
            // Count == 0
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");
            
            assertEquals(
                0, JDBCUtil.executeCount("select count(*) from " + table));
            
            JDBCUtil.executeUpdate("insert into " + table + "(id) values(999)");
            
            // Count == 1
            assertEquals(
                1, JDBCUtil.executeCount("select count(*) from " + table));                

            // Count == many
            int many = RandomUtils.nextInt(999);
            
            for (int i = 0; i < many; i++) 
            {
                JDBCUtil.executeUpdate(
                    "insert into " + table + "(id) " + 
                    "values(" + RandomUtils.nextInt(many) + ")");
            }
            
            assertEquals(
                many+1, JDBCUtil.executeCount("select count(*) from " + table));
        }
        finally
        {
            JDBCUtil.dropTable(table);
            JDBCUtil.shutdown();
            cleanup(prefix);
        }
    }


    /**
     * Tests getSize() on a table with many rows.
     *
     * @throws Exception on error.
     */
    public void testGetSizeMany() throws Exception
    {
        logger_.info("Running testGetSizeMany...");

        String prefix = nextPrefix("JDBCUtilTest_GetSizeMany");
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_many";
        Connection conn = null;
        ResultSet results = null;
        int numRows = 100;

        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");

            for (int i = 0; i < numRows; i++)
                JDBCUtil.executeUpdate(
                    "insert into " + table + "(id) values (" + i + ")");

            conn = JDBCUtil.getConnection();

            Statement s = conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

            results = s.executeQuery("select * from " + table);

            int cursorPos = results.getRow();
            int size = JDBCUtil.getSize(results);

            logger_.debug("Resultset size: " + size);
            assertEquals("size mismatch", numRows, size);

            //
            // Make sure position of cursor is unchanged.
            //
            assertEquals(cursorPos == 0 ? 1 : cursorPos, results.getRow());

        }
        finally
        {
            JDBCUtil.close(results);
            JDBCUtil.releaseConnection(conn);
            JDBCUtil.dropTable(table);
            JDBCUtil.shutdown();
            cleanup(prefix);
        }
    }


    /**
     * Tests getSize() on a table with one row.
     *
     * @throws Exception on error.
     */
    public void testGetSizeOne() throws Exception
    {
        logger_.info("Running testGetSizeOne...");

        String prefix = nextPrefix("JDBCUtilTest_GetSizeOne");
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_one";
        Connection conn = null;
        ResultSet results = null;

        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");
            JDBCUtil.executeUpdate("insert into " + table + "(id) values(999)");

            conn = JDBCUtil.getConnection();

            Statement s = conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

            results = s.executeQuery("select * from " + table);

            int cursorPos = results.getRow();
            int size = JDBCUtil.getSize(results);

            logger_.debug("Resultset size: " + size);
            assertEquals("size should be one", 1, size);

            //
            // Make sure position of cursor is unchanged.
            //
            assertEquals(cursorPos == 0 ? 1 : cursorPos, results.getRow());

        }
        finally
        {
            JDBCUtil.close(results);
            JDBCUtil.releaseConnection(conn);
            JDBCUtil.dropTable(table);
            JDBCUtil.shutdown();
            cleanup(prefix);
        }
    }


    /**
     * Tests getSize() to make sure a non-zero cursor position is restored.
     *
     * @throws Exception on error.
     */
    public void testGetSizeNonZeroCursorPos() throws Exception
    {
        logger_.info("Running testGetSizeNonZeroCursorPos...");

        String prefix = nextPrefix("JDBCUtilTest_GetSizeNonZeroCursorPos");
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_size";
        Connection conn = null;
        ResultSet results = null;
        int numRows = 50;

        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");

            for (int i = 0; i < numRows; i++)
                JDBCUtil.executeUpdate(
                    "insert into " + table + "(id) values (" + i + ")");

            conn = JDBCUtil.getConnection();

            Statement s = conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

            results = s.executeQuery("select * from " + table);

            //
            // Move cursor
            //
            results.absolute(numRows / 10);

            int cursorPos = results.getRow();
            logger_.debug("Restored cursor position: " + cursorPos);
            assertEquals(cursorPos, results.getRow());
        }
        finally
        {
            JDBCUtil.close(results);
            JDBCUtil.releaseConnection(conn);
            JDBCUtil.dropTable(table);
            JDBCUtil.shutdown();
            cleanup(prefix);
        }
    }


    /**
     * Tests dropTable()
     *
     * @throws Exception on error.
     */
    public void testDropTable() throws Exception
    {
        logger_.info("Running testDropTable...");

        String prefix = nextPrefix("JDBCUtilTest_DropTable");
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "user";

        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");
            
            JDBCUtil.executeUpdate(
                "insert into " + table + "(id) values (999)");

            // Make sure table exists
            String contents = JDBCUtil.executeQuery("select * from user");
            //logger_.debug("Before drop: " + contents);
            assertTrue(contents.indexOf("ID") >= 0);

            JDBCUtil.dropTable("user");

            // Make sure table does not exist
            try
            {
                JDBCUtil.executeQuery("select * from user");
                fail("Should not be able to select from dropped table.");
            }
            catch (SQLException se)
            {
                assertTrue(
                    se.getLocalizedMessage().indexOf("Table not found") >= 0);
            }
        }
        finally
        {
            JDBCUtil.shutdown();
            cleanup(prefix);
        }
    }


    /**
     * Tests dropTable() for failures: null, non-existant table.
     *
     * @throws Exception on error.
     */
    public void testDropTableFailure() throws Exception
    {
        logger_.info("Running testDropTableFailure...");

        String prefix = nextPrefix("JDBCUtilTest_DropTblFailure");
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);

        try
        {
            JDBCUtil.dropTable(null);
            JDBCUtil.dropTable("");
            JDBCUtil.dropTable("invalid_table");

            // Success
        }
        catch (Exception e)
        {
            fail("Dropping troublesome table should not throw an exception");
        }
        finally
        {
            JDBCUtil.shutdown();
            cleanup(prefix);
        }
    }


    /**
     * Tests shutdown()
     *
     * @throws Exception on error.
     */
    public void testShutdown() throws Exception
    {
        logger_.info("Running testShutdown...");

        String prefix = nextPrefix("shutdown");
        
        try
        {
            JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
            JDBCUtil.shutdown();
        }
        finally
        {
            cleanup(prefix);
        }
    }


    /**
     * Tests close(ResultSet) for a null ResultSet.
     *
     * @throws Exception on error.
     */
    public void testCloseNullResultSet() throws Exception
    {
        logger_.info("Running testCloseNullResultSet...");

        try
        {
            ResultSet rs = null;
            JDBCUtil.close(rs);

            // Success
        }
        catch (Exception e)
        {
            fail("Closing null ResultSet should not fail");
        }
    }


    /**
     * Tests close(Statement) for a null statement.
     *
     * @throws Exception on error.
     */
    public void testCloseNullStatement() throws Exception
    {
        logger_.info("Running testCloseNullStatement...");

        try
        {
            Statement stmt = null;
            JDBCUtil.close(stmt);

            // Success
        }
        catch (Exception e)
        {
            fail("Closing null Statement should not fail");
        }
    }


    /**
     * Tests release(Connection) for a null connection.
     *
     * @throws Exception on error.
     */
    public void testReleaseNullConnection() throws Exception
    {
        logger_.info("Running testReleaseNullConnection...");

        try
        {
            Connection conn = null;
            JDBCUtil.releaseConnection(conn);

            // Success
        }
        catch (Exception e)
        {
            fail("Releasing null Connection should not fail");
        }
    }


    /**
     * Tests getTableNames()
     *
     * @throws Exception on error.
     */
    public void testGetTableNames() throws Exception
    {
        logger_.info("Running testGetTableNames...");

        String prefix = nextPrefix("JDBCUtilTest_GetTableNames");
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_gettablenames";

        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");
            String[] tables = JDBCUtil.getTableNames();
            logger_.debug("Tables =\n" + ArrayUtil.toString(tables, true));

            //
            // The table names may not be in the same case so the search has
            // to be case insensitive.
            //

            assertTrue(
                "Table " + table + " should be in list of table names returned",
                ArrayUtil.contains(
                    tables,
                    table,
                    String.CASE_INSENSITIVE_ORDER));
        }
        finally
        {
            JDBCUtil.dropTable(table);
            JDBCUtil.shutdown();
            cleanup(prefix);
        }
    }

    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------

    /**
     * Cleans up HSQLDB file remnants.
     *
     * @param prefx Name prefix.
     */
    protected void cleanup(String prefx)
    {
        (new File(FileUtil.getTempDir() + FS + prefx + ".properties")).delete();
        (new File(FileUtil.getTempDir() + FS + prefx + ".data")).delete();
        (new File(FileUtil.getTempDir() + FS + prefx + ".script")).delete();
        (new File(FileUtil.getTempDir() + FS + prefx + ".log")).delete();
    }


    /**
     * Returns the absolute path of the test db driver jar file we're using.
     *
     * @return String
     */
    protected String getDBJar()
    {
        String jarFile = null;
        StringOutputStream sos = new StringOutputStream();
        Main.showPath(sos);
        String[] lines = StringUtil.tokenize(sos.toString(), "\n");

        for (int i = 0; i < lines.length; i++)
        {
            if (lines[i].indexOf(DB_JAR) >= 0)
            {
                jarFile = StringUtil.tokenize(lines[i], " ")[0];
                logger_.info("Found file: " + jarFile);
                break;
            }
        }

        return jarFile;
    }
    
    
    /**
     * Returns next unique prefix.
     * 
     * @param prefix Stem
     * @return String
     * @throws SequenceEndedException on end of sequence.
     */
    protected String nextPrefix(String prefix) throws SequenceEndedException
    {
        return prefix + rand_.nextInt();
    }
}