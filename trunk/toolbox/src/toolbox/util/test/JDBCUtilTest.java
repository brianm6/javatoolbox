package toolbox.util.test;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.showclasspath.Main;
import toolbox.util.FileUtil;
import toolbox.util.JDBCUtil;
import toolbox.util.RandomUtil;
import toolbox.util.StringUtil;
import toolbox.util.io.StringOutputStream;

/**
 * Unit test for JDBCUtil.
 */
public class JDBCUtilTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(JDBCUtilTest.class);
    
    private static String FS = File.separator;
    
    private static String DB_JAR = "hsqldb.jar";
    private static String DB_DRIVER = "org.hsqldb.jdbcDriver";
    private static String DB_USER = "SA";
    private static String DB_PASSWORD = "";
    private static String DB_URL = "jdbc:hsqldb:" + FileUtil.getTempDir()+FS; 
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(JDBCUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------

    /**
     * Tests init()
     */
    public void testInit() throws Exception
    {
        logger_.info("Running testInit...");
        
        try
        {
            JDBCUtil.init(DB_DRIVER, DB_URL, DB_USER, DB_PASSWORD);
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
     * Tests init() loading the jdbc driver from a jar file. 
     * 
     * @throws Exception on error
     */
    public void testInitUsingJar() throws Exception
    {
        logger_.info("Running testInitUsingJar...");
        
        try
        {                
            JDBCUtil.init(getDBJar(), DB_DRIVER, DB_URL, DB_USER, DB_PASSWORD);
        }
        finally
        {
            JDBCUtil.shutdown();
        }
    }    
    
    
    /**
     * Tests getConnection()
     * 
     * @throws Exception on error
     */
    public void testGetConnection() throws Exception
    {
        logger_.info("Running testGetConnection...");
        
        String prefix = "JDBCUtilTest_testGetConnection_" + 
            RandomUtil.nextInt(1000);
        
        try
        {    
            JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
            
            Connection conn = JDBCUtil.getConnection();
            logger_.info("Connection: " + conn);
            logger_.info("Autocommit: " + conn.getAutoCommit());
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
     * Tests executeQuery() on an empty table.
     * 
     * @throws Exception on error
     */
    public void testExecuteQueryZero() throws Exception
    {
        logger_.info("Running testExecuteQueryZero...");
        
        String prefix = "JDBCUtilTest_testQueryExecuteZero_" + 
            RandomUtil.nextInt(1000);
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_zero";
        
        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");
            String results = JDBCUtil.executeQuery("select * from " + table);            
            assertTrue(results.indexOf("0 rows") >= 0);
            logger_.info("\n" + results);
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
     * @throws Exception on error
     */
    public void testExecuteQueryOne() throws Exception
    {
        logger_.info("Running testExecuteQueryOne...");
        
        String prefix = "JDBCUtilTest_testQueryExecuteOne_" + 
            RandomUtil.nextInt(1000);
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_one";
        
        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");
            JDBCUtil.executeUpdate("insert into " +table + "(id) values (999)");
            String results = JDBCUtil.executeQuery("select * from " + table);            
            assertTrue(results.indexOf("1 rows") >= 0);
            assertTrue(results.indexOf("999") >= 0);
            logger_.info("\n" + results);
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
     * @throws Exception on error
     */
    public void testExecuteQueryMany() throws Exception
    {
        logger_.info("Running testExecuteQueryMany...");
        
        String prefix = "JDBCUtilTest_testQueryExecuteMany_" + 
            RandomUtil.nextInt(1000);
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_many";
        
        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");
            
            for (int i=0; i<100; i++)
            {
                JDBCUtil.executeUpdate(
                    "insert into " + table + "(id) " + 
                    "values (" + i + ")");
            }
                    
            String results = JDBCUtil.executeQuery("select * from " + table);            
            assertTrue(results.indexOf("100 rows") >= 0);
            logger_.info("\n" + StringUtil.wrap(results.replace('\n', ' ')));
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
     * @throws Exception on error
     */
    public void testExecuteQueryArrayZero() throws Exception
    {
        logger_.info("Running testExecuteQueryArrayZero...");
        
        String prefix = "JDBCUtilTest_testQueryExecuteArrayZero_" + 
            RandomUtil.nextInt(1000);
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
     * @throws Exception on error
     */
    public void testExecuteQueryArrayOne() throws Exception
    {
        logger_.info("Running testExecuteQueryArrayOne...");
        
        String prefix = "JDBCUtilTest_testQueryExecuteArrayOne_" + 
            RandomUtil.nextInt(1000);
            
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_one";
        
        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");
            JDBCUtil.executeUpdate("insert into " +table + "(id) values (999)");
            
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
     * @throws Exception on error
     */
    public void testExecuteQueryArrayMany() throws Exception
    {
        logger_.info("Running testExecuteQueryArrayMany...");
        
        String prefix = "JDBCUtilTest_testExecuteQueryArrayMany_" + 
            RandomUtil.nextInt(1000);
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_many";
        
        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");
            
            for(int i=0; i<100; i++)
                JDBCUtil.executeUpdate(
                    "insert into " +table + "(id) values (" + i + ")");
                    
            Object[][] results = 
                JDBCUtil.executeQueryArray("select * from " + table);
                            
            assertEquals(1, results.length);
            assertEquals(100, results[0].length);
            
            for (int i=0; i<100; i++)
                assertEquals(new Integer(i), results[0][i]);
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
     * @throws Exception on error
     */
    public void testExecuteUpdate() throws Exception
    {
        logger_.info("Running testExecuteUpdate...");
        
        String prefix = "JDBCUtilTest_testExecuteUpdate_" + 
            RandomUtil.nextInt(1000);
            
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
            logger_.info("\n" + results);
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
     * @throws Exception on error
     */
    public void testGetSizeZero() throws Exception
    {
        logger_.info("Running testGetSizeZero...");
        
        String prefix = "JDBCUtilTest_testGetSizeZero_" + 
            RandomUtil.nextInt(1000);
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
            
            logger_.info("Resultset size: " + size);
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
     * Tests getSize() on a table with one row.
     * 
     * @throws Exception on error
     */
    public void testGetSizeOne() throws Exception
    {
        logger_.info("Running testGetSizeOne...");
        
        String prefix = "JDBCUtilTest_testGetSizeOne_" + 
            RandomUtil.nextInt(1000);
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_one";
        Connection conn = null;
        ResultSet results = null;
        int numRows = 100;
        
        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");

            for(int i=0; i<numRows; i++)
                JDBCUtil.executeUpdate(
                    "insert into " + table + "(id) values (" + i + ")");
            
            conn = JDBCUtil.getConnection();
            
            Statement s = conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, 
                ResultSet.CONCUR_READ_ONLY);
            
            results = s.executeQuery("select * from " + table);
                
            int size = JDBCUtil.getSize(results);
            
            logger_.info("Resultset size: " + size);
            assertEquals("size shold be zero", numRows, size);
                
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
     * @throws Exception on error
     */
    public void testGetSizeMany() throws Exception
    {
        logger_.info("Running testGetSizeMany...");
        
        String prefix = "JDBCUtilTest_testGetSizeMany_" + 
            RandomUtil.nextInt(1000);
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "table_many";
        Connection conn = null;
        ResultSet results = null;
        
        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");
            JDBCUtil.executeUpdate("insert into " +table + "(id) values (999)");            
            
            conn = JDBCUtil.getConnection();
            
            Statement s = conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, 
                ResultSet.CONCUR_READ_ONLY);
            
            results = s.executeQuery("select * from " + table);
                
            int size = JDBCUtil.getSize(results);
            
            logger_.info("Resultset size: " + size);
            assertEquals("size shold be zero", 1, size);
                
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
     * @throws Exception on error
     */
    public void testDropTable() throws Exception
    {
        logger_.info("Running testDropTable...");
        
        String prefix = "JDBCUtilTest_testDropTable_" + RandomUtil.nextInt(999);
        JDBCUtil.init(DB_DRIVER, DB_URL + prefix, DB_USER, DB_PASSWORD);
        String table = "user";
        
        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");
            JDBCUtil.executeUpdate("insert into " +table + "(id) values (999)");
           
            // Make sure table exists
            String contents = JDBCUtil.executeQuery("select * from user");
            //logger_.info("Before drop: " + contents);             
            assertTrue(contents.indexOf("ID") >=0);
                                
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
     * Tests shutdown()
     */
    public void testShutdown() throws Exception
    {
        logger_.info("Running testShutdown...");
        
        JDBCUtil.init(DB_DRIVER, DB_URL, DB_USER, DB_PASSWORD);
        JDBCUtil.shutdown();
    }
    
    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------
    
    /**
     * Cleans up HSQLDB file remnants.
     */
    protected void cleanup(String prefix)
    {
        (new File(FileUtil.getTempDir() + FS + prefix +".properties")).delete();
        (new File(FileUtil.getTempDir() + FS + prefix +".data")).delete();
        (new File(FileUtil.getTempDir() + FS + prefix +".script")).delete();
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
        
        for (int i=0; i<lines.length; i++)
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
}