package toolbox.util;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.util.JDBCSession}. This test case uses an 
 * in-process file instance of HSQLDB for a test database.
 */
public class JDBCSessionTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JDBCSessionTest.class);

    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    private static String FS          = File.separator;
    private static String DB_JAR      = "hsqldb.jar";
    private static String DB_DRIVER   = "org.hsqldb.jdbcDriver";
    private static String DB_USER     = "SA";
    private static String DB_PASSWORD = "";
    private static String DB_URL = "jdbc:hsqldb:" + FileUtil.getTempDir() + FS;

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    /**
     * Entrypoint.
     *
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(JDBCSessionTest.class);
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

        String name = "testInit" + RandomUtils.nextInt();
        
        try
        {
            JDBCSession.init(
                name, DB_DRIVER, DB_URL, DB_USER, DB_PASSWORD, false);
            
            assertNotNull(JDBCSession.getSession(name));
        }
        finally
        {
            JDBCSession.shutdown(name);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
    }


    /**
     * Tests init() with connection pooling
     * 
     * @throws Exception on error.
     */
    public void testInitWithPooling() throws Exception 
    {
        logger_.info("Running testInitWithPooling...");

        
        String name = "testInit" + RandomUtils.nextInt();
        
        try
        {
            JDBCSession.init(
                name, DB_DRIVER, DB_URL, DB_USER, DB_PASSWORD, true);
            
            assertNotNull(JDBCSession.getSession(name));
        }
        finally
        {
            JDBCSession.shutdown(name);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
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
        
        for (int i = 0; i < max; i++) {
            String table = "table99";
            String prefix = "JDBCSessionTest-StressInit-" + RandomUtils.nextInt();
            String name = prefix;
            
            JDBCSession.init(
                name, DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD, true);
            
            JDBCSession.executeUpdate(
                name, "create table " + table + "(id integer)");
            
            JDBCSession.executeUpdate(
                name, "insert into " + table + " (id)values(99)");
            
            JDBCSession.executeUpdate(
                name, "update " + table + " set id=999 where id=99");
            
            JDBCSession.executeUpdate(
                name, "delete from " + table + " where id=999");
            
            JDBCSession.executeQuery(name, "select * from " + table);
            JDBCSession.dropTable(name, table);
            JDBCSession.shutdown(name);
            cleanup(prefix);
            logger_.debug("Stress loop : " + i);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
    }
    
    
    /**
     * Tests init() loading the jdbc driver from a jar file.
     *
     * @throws Exception on error.
     */
    public void testInitUsingJar() throws Exception
    {
        logger_.info("Running testInitUsingJar...");

        String prefix = "JDBCSessionTest-StressInit-" + RandomUtils.nextInt();
        String name = prefix;

        try
        {
            JDBCSession.init(
                name, getDBJar(), DB_DRIVER, DB_URL + prefix, DB_USER, 
                DB_PASSWORD);
        }
        finally
        {
            JDBCSession.shutdown(name);
            cleanup(prefix);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
    }

    
    /**
     * Tests init() loading the jdbc driver from a jar file and connection 
     * pooling.
     *
     * @throws Exception on error.
     */
    public void testInitUsingJarAndPooling() throws Exception
    {
        logger_.info("Running testInitUsingJarAndPooling...");

        String session = "JDBCSessionTest-" + RandomUtils.nextInt();

        try
        {
            JDBCSession.init(
                session, 
                getDBJar(), 
                DB_DRIVER, 
                DB_URL + session, 
                DB_USER, 
                DB_PASSWORD, 
                true);
            
            String[] tables = JDBCSession.getTableNames(session);
            
            //logger_.debug(ArrayUtil.toString(tables, true));
            
            assertTrue(tables.length > 0);
            assertEquals(1, JDBCSession.getSessionCount());
            
            for (int i = 0; i < 100; i++)
            {
                Connection conn1 = JDBCSession.getConnection(session);
                Connection conn2 = JDBCSession.getConnection(session);
                Connection conn3 = JDBCSession.getConnection(session);
                
                //logger_.debug("Connection: " + conn1);
                //logger_.debug("Autocommit: " + conn1.getAutoCommit());
                
                assertNotNull(conn1);
                assertNotNull(conn2);
                assertNotNull(conn3);
                JDBCUtil.releaseConnection(conn1);
                JDBCUtil.releaseConnection(conn2);
                JDBCUtil.releaseConnection(conn3);
            }
        }
        catch (Throwable t)
        {
            logger_.error("erro", t);
        }
        finally
        {
            JDBCSession.shutdown(session);
            cleanup(session);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
    }

    
    /**
     * Tests init() for failure that should result from initing the same
     * session twice.
     *
     * @throws Exception on error.
     */
    public void testInitSameSessionFailure() throws Exception
    {
        logger_.info("Running testInitSameSessionFailure...");

        String prefix = "JDBCSessionTest-SameSession-" + RandomUtils.nextInt();
        String name = "SessionInitedTwice";

        try
        {
            JDBCSession.init(
                name, 
                DB_DRIVER, 
                DB_URL + prefix, 
                DB_USER, DB_PASSWORD);
         
            try 
            {
                JDBCSession.init(
                    name, 
                    DB_DRIVER, 
                    DB_URL + prefix, 
                    DB_USER, DB_PASSWORD);
            }
            catch (IllegalArgumentException iae)
            {
                logger_.debug("SUCCESS: " + iae.getMessage());
            }
        }
        finally
        {
            JDBCSession.shutdown(name);
            cleanup(prefix);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
    }


    /**
     * Tests getConnection()
     *
     * @throws Exception on error.
     */
    public void testGetConnection() throws Exception
    {
        logger_.info("Running testGetConnection...");

        String prefix = "JDBCSessionTest_GetConnection" + RandomUtils.nextInt();
        String name = prefix;

        try
        {
            JDBCSession.init(
                name, DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);

            Connection conn = JDBCSession.getConnection(name);
            logger_.debug("Connection: " + conn);
            logger_.debug("Autocommit: " + conn.getAutoCommit());
            assertNotNull(conn);
            JDBCUtil.releaseConnection(conn);
        }
        finally
        {
            JDBCSession.shutdown(name);
            cleanup(prefix);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
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
            JDBCSession.getConnection("blah");
            fail("getConnection() should have failed because not init()'ed");
        }
        catch (IllegalArgumentException ise)
        {
            // Success
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

        String prefix = "JDBCSessionTest_QueryExecZero" + RandomUtils.nextInt();
        String name = prefix;
        JDBCSession.init(name, DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_zero";

        try
        {
            JDBCSession.executeUpdate(
                name, "create table " + table + "(id integer)");
            
            String results = 
                JDBCSession.executeQuery(name, "select * from " + table);
            
            assertTrue(results.indexOf("0 rows") >= 0);
            logger_.debug("\n" + results);
        }
        finally
        {
            JDBCSession.dropTable(name, table);
            JDBCSession.shutdown(name);
            cleanup(prefix);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
    }


    /**
     * Tests executeQuery() on table with only one row of data.
     *
     * @throws Exception on error.
     */
    public void testExecuteQueryOne() throws Exception
    {
        logger_.info("Running testExecuteQueryOne...");

        String prefix = "JDBCSessionTest_QueryExecOne_" + RandomUtils.nextInt();
        String name = prefix;
        JDBCSession.init(name, DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_one";

        try
        {
            JDBCSession.executeUpdate(
                name, "create table " + table + "(id integer)");
            
            JDBCSession.executeUpdate(
                name, "insert into " + table + "(id) values(333)");
            
            String results = JDBCSession.executeQuery(
                name, "select * from " + table);
            
            assertTrue(results.indexOf("1 rows") >= 0);
            assertTrue(results.indexOf("333") >= 0);
            logger_.debug("\n" + results);
        }
        finally
        {
            JDBCSession.dropTable(name, table);
            JDBCSession.shutdown(name);
            cleanup(prefix);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
    }


    /**
     * Tests executeQuery() on table with many rows of data.
     *
     * @throws Exception on error.
     */
    public void testExecuteQueryMany() throws Exception
    {
        logger_.info("Running testExecuteQueryMany...");

        String prefix = "JDBCSessionTest_QueryExecMany" + RandomUtils.nextInt();
        String name = prefix;
        JDBCSession.init(name, DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_many";

        try
        {
            JDBCSession.executeUpdate(
                name, "create table " + table + "(id integer)");

            for (int i = 0; i < 100; i++)
                JDBCSession.executeUpdate(
                    name, "insert into " + table + "(id) values (" + i + ")");

            String results = JDBCSession.executeQuery(
                name, "select * from " + table);
            
            assertTrue(results.indexOf("100 rows") >= 0);
            logger_.debug("\n" + StringUtil.wrap(results.replace('\n', ' ')));
        }
        finally
        {
            JDBCSession.dropTable(name, table);
            JDBCSession.shutdown(name);
            cleanup(prefix);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
    }


    /**
     * Tests executeQueryArray() on an empty table.
     *
     * @throws Exception on error.
     */
    public void testExecuteQueryArrayZero() throws Exception
    {
        logger_.info("Running testExecuteQueryArrayZero...");

        String pre = "JDBCSessionTest_QryExecAryZero" + RandomUtils.nextInt();
        String name = pre;
        
        JDBCSession.init(name, DB_DRIVER, DB_URL + pre, DB_USER, DB_PASSWORD);
        String table = "table_zero";

        try
        {
            JDBCSession.executeUpdate(name, 
                "create table " + table + "(id integer)");

            Object[][] results =
                JDBCSession.executeQueryArray(name, "select * from " + table);

            // Column headers still occupy one row
            assertEquals(0, results.length);
        }
        finally
        {
            JDBCSession.dropTable(name, table);
            JDBCSession.shutdown(name);
            cleanup(pre);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
    }


    /**
     * Tests executeQueryArray() on table with only one row of data.
     *
     * @throws Exception on error.
     */
    public void testExecuteQueryArrayOne() throws Exception
    {
        logger_.info("Running testExecuteQueryArrayOne...");

        String prefix = "JDBCSessionTest_QryExecAryOne" + RandomUtils.nextInt();
        String name = prefix;
        JDBCSession.init(name, DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_one";

        try
        {
            JDBCSession.executeUpdate(name, 
                "create table " + table + "(id integer)");
            
            JDBCSession.executeUpdate(name,
                "insert into " + table + "(id) values (333)");

            Object[][] results =
                JDBCSession.executeQueryArray(name, "select * from " + table);

            assertEquals(1, results.length);
            assertEquals(1, results[0].length);
            assertEquals(new Integer(333), results[0][0]);
        }
        finally
        {
            JDBCSession.dropTable(name, table);
            JDBCSession.shutdown(name);
            cleanup(prefix);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
    }


    /**
     * Tests executeQueryArray() on table with many rows of data.
     *
     * @throws Exception on error.
     */
    public void testExecuteQueryArrayMany() throws Exception
    {
        logger_.info("Running testExecuteQueryArrayMany...");

        String prefix = "JDBCSessionTest_ExecQryAryMny" + RandomUtils.nextInt();
        String name = prefix;
        JDBCSession.init(name, DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_many";

        try
        {
            JDBCSession.executeUpdate(name, 
                "create table " + table + "(id integer)");

            for (int i = 0; i < 100; i++)
                JDBCSession.executeUpdate(name,
                    "insert into " + table + "(id) values (" + i + ")");

            Object[][] results =
                JDBCSession.executeQueryArray(name, "select * from " + table);

            assertEquals(100, results.length);
            assertEquals(1, results[0].length);

            for (int i = 0; i < 100; i++)
                assertEquals(new Integer(i), results[i][0]);
        }
        finally
        {
            JDBCSession.dropTable(name, table);
            JDBCSession.shutdown(name);
            cleanup(prefix);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
    }


    /**
     * Tests executeUpdate() for INSERT, UPDATE, and DELETE.
     *
     * @throws Exception on error.
     */
    public void testExecuteUpdate() throws Exception
    {
        logger_.info("Running testExecuteUpdate...");

        String prefix = "JDBCSessionTest_ExecUpdates" + RandomUtils.nextInt();
        String name = prefix;
        
        JDBCSession.init(name, 
            DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        
        String table = "table_execute_update";

        try
        {
            JDBCSession.executeUpdate(name, 
                "create table " + table + "(id integer)");

            assertEquals(1,
                JDBCSession.executeUpdate(name,
                    "insert into " + table + " (id) values(100)"));

            assertEquals(1,
                JDBCSession.executeUpdate(name,
                    "update " + table + " set id=999 where id=100"));

            assertEquals(1,
                JDBCSession.executeUpdate(name,
                    "delete from " + table + " where id=999"));

            String results = JDBCSession.executeQuery(
                name, "select * from " + table);
            assertTrue(results.indexOf("0 rows") >= 0);
            logger_.debug("\n" + results);
        }
        finally
        {
            JDBCSession.dropTable(name, table);
            JDBCSession.shutdown(name);
            cleanup(prefix);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
    }


    /**
     * Tests getSize() on an empty table.
     *
     * @throws Exception on error.
     */
    public void testGetSizeZero() throws Exception
    {
        logger_.info("Running testGetSizeZero...");

        String prefix = "JDBCSessionTest_GetSizeZero_" + RandomUtils.nextInt();
        String name = prefix;
        JDBCSession.init(name, DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_zero";
        Connection conn = null;
        ResultSet results = null;

        try
        {
            JDBCSession.executeUpdate(name, 
                "create table " + table + "(id integer)");
            
            conn = JDBCSession.getConnection(name);

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
            JDBCSession.dropTable(name, table);
            JDBCSession.shutdown(name);
            cleanup(prefix);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
    }

    
    /**
     * Tests executeCount()
     *
     * @throws Exception on error.
     */
    public void testExecuteCount() throws Exception
    {
        logger_.info("Running testExecuteCount...");

        String prefix = "JDBCSessionTest_ExecuteCount" + RandomUtils.nextInt();
        String name = prefix;
        JDBCSession.init(
            name, DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        
        String table = "table_exec_cnt";

        try
        {
            // Count == 0
            JDBCSession.executeUpdate(name, 
                "create table " + table + "(id integer)");
            
            assertEquals(
                0, JDBCSession.executeCount(name, 
                    "select count(*) from " + table));
            
            JDBCSession.executeUpdate(name, 
                "insert into " + table + "(id) values(333)");
            
            // Count == 1
            assertEquals(
                1, JDBCSession.executeCount(name, 
                    "select count(*) from " + table));                

            // Count == many
            int many = RandomUtils.nextInt(500);
            
            for (int i = 0; i < many; i++) 
            {
                JDBCSession.executeUpdate(name, 
                    "insert into " + table + "(id) " + 
                    "values(" + RandomUtils.nextInt(many) + ")");
            }
            
            assertEquals(
                many+1, JDBCSession.executeCount(name, 
                    "select count(*) from " + table));
        }
        finally
        {
            JDBCSession.dropTable(name, table);
            JDBCSession.shutdown(name);
            cleanup(prefix);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
    }


    /**
     * Tests getSize() on a table with many rows.
     *
     * @throws Exception on error.
     */
    public void testGetSizeMany() throws Exception
    {
        logger_.info("Running testGetSizeMany...");

        String prefix = "JDBCSessionTest_GetSizeMany" + RandomUtils.nextInt();
        String name = prefix;
        
        JDBCSession.init(name, 
            DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        
        String table = "table_many";
        Connection conn = null;
        ResultSet results = null;
        int numRows = 100;

        try
        {
            JDBCSession.executeUpdate(name, 
                "create table " + table + "(id integer)");

            for (int i = 0; i < numRows; i++)
                JDBCSession.executeUpdate(name,
                    "insert into " + table + "(id) values (" + i + ")");

            conn = JDBCSession.getConnection(name);

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
            JDBCSession.dropTable(name, table);
            JDBCSession.shutdown(name);
            cleanup(prefix);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
    }


    /**
     * Tests getSize() on a table with one row.
     *
     * @throws Exception on error.
     */
    public void testGetSizeOne() throws Exception
    {
        logger_.info("Running testGetSizeOne...");

        String prefix = "JDBCSessionTest_GetSizeOne" + RandomUtils.nextInt();
        String name = prefix;
            
        JDBCSession.init(name, 
            DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        
        String table = "table_one";
        Connection conn = null;
        ResultSet results = null;

        try
        {
            JDBCSession.executeUpdate(name, 
                "create table " + table + "(id integer)");
            
            JDBCSession.executeUpdate(name, 
                "insert into " + table + "(id) values(333)");

            conn = JDBCSession.getConnection(name);

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
            JDBCSession.dropTable(name, table);
            JDBCSession.shutdown(name);
            cleanup(prefix);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
    }


    /**
     * Tests getSize() to make sure a non-zero cursor position is restored.
     *
     * @throws Exception on error.
     */
    public void testGetSizeNonZeroCursorPos() throws Exception
    {
        logger_.info("Running testGetSizeNonZeroCursorPos...");

        String prefix = 
            "JDBCSessionTest_GetSizeNonZeroCursorPos" + 
            RandomUtils.nextInt();
        String name = prefix;
            
        JDBCSession.init(name,
            DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        
        String table = "table_size";
        Connection conn = null;
        ResultSet results = null;
        int numRows = 50;

        try
        {
            JDBCSession.executeUpdate(name, 
                "create table " + table + "(id integer)");

            for (int i = 0; i < numRows; i++)
                JDBCSession.executeUpdate(name,
                    "insert into " + table + "(id) values (" + i + ")");

            conn = JDBCSession.getConnection(name);

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
            JDBCSession.dropTable(name, table);
            JDBCSession.shutdown(name);
            cleanup(prefix);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
    }


    /**
     * Tests dropTable()
     *
     * @throws Exception on error.
     */
    public void testDropTable() throws Exception
    {
        logger_.info("Running testDropTable...");

        String prefix = "JDBCSessionTest_DropTable" + RandomUtils.nextInt();
        String name = prefix;
        JDBCSession.init(name, 
            DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "user";

        try
        {
            JDBCSession.executeUpdate(name,
                "create table " + table + "(id integer)");
            
            JDBCSession.executeUpdate(name,
                "insert into " + table + "(id) values (333)");

            // Make sure table exists
            String contents = JDBCSession.executeQuery(name, 
                "select * from user");
            
            //logger_.debug("Before drop: " + contents);
            assertTrue(contents.indexOf("ID") >= 0);

            JDBCSession.dropTable(name, "user");

            // Make sure table does not exist
            try
            {
                JDBCSession.executeQuery(name, "select * from user");
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
            JDBCSession.shutdown(name);
            cleanup(prefix);
        }
        
        assertEquals(0, JDBCSession.getSessionCount());
    }


    /**
     * Tests dropTable() for failures: null, non-existent table.
     *
     * @throws Exception on error.
     */
    public void testDropTableFailure() throws Exception
    {
        logger_.info("Running testDropTableFailure...");

        String prefix = 
            "JDBCSessionTest_DropTblFailure" + RandomUtils.nextInt();
        
        String name = prefix;
        JDBCSession.init(name, 
            DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);

        try
        {
            JDBCSession.dropTable(name, null);
            JDBCSession.dropTable(name, "");
            JDBCSession.dropTable(name, "invalid_table");

            // Success
        }
        catch (Exception e)
        {
            fail("Dropping troublesome table should not throw an exception");
        }
        finally
        {
            JDBCSession.shutdown(name);
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

        // Had to make url different because of HSQLDB file mode sharing
        JDBCSession.init("shutdown",
            DB_DRIVER, DB_URL + "shutdown", DB_USER, DB_PASSWORD);
        
        JDBCSession.shutdown("shutdown");
    }


    /**
     * Tests getTableNames()
     *
     * @throws Exception on error.
     */
    public void testGetTableNames() throws Exception
    {
        logger_.info("Running testGetTableNames...");

        String prefix = 
            "JDBCSessionTest_GetTableNames" + RandomUtils.nextInt();
        
        String name = prefix;
        
        JDBCSession.init(name, 
            DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        
        String table = "table_gettablenames";

        try
        {
            JDBCSession.executeUpdate(name, 
                "create table " + table + "(id integer)");
            
            String[] tables = JDBCSession.getTableNames(name);
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
            JDBCSession.dropTable(name, table);
            JDBCSession.shutdown(name);
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
    }


    /**
     * Returns the absolute path of the test db driver jar file we're using.
     *
     * @return String
     */
    protected String getDBJar()
    {
        List files = FileUtil.find(System.getProperty("user.dir"), 
            new FilenameFilter()
            {
                public boolean accept(File dir, String name)
                {
                    return name.equals(DB_JAR);
                }
            });
    
        assertEquals("jdbc driver jar file not found!", 1, files.size());
        
        return files.get(0).toString();
    }
}