package toolbox.util.test;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.JDBCUtil;
import toolbox.util.RandomUtil;

/**
 * Unit test for JDBCUtil
 * 
 * TODO: Unit tests for executeUpdate()
 */
public class JDBCUtilTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(JDBCUtilTest.class);
    
    private static String FS = File.separator;
    
    private static String DRIVER   = "org.hsqldb.jdbcDriver";
    private static String USER     = "SA";
    private static String PASSWORD = "";
    private static String URL      = "jdbc:hsqldb:" + FileUtil.getTempDir()+FS; 
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(JDBCUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    //  public void testFormat()
    //  {
    //  }
    
    //  public void testExecuteUpdate()
    //  {
    //  }

    /**
     * Tests getConnection()
     */
    public void testGetConnection() throws Exception
    {
        logger_.info("Running testGetConnection...");
        
        String prefix = "JDBCUtilTest_testGetConnection_" + 
            RandomUtil.nextInt(1000);
        
        try
        {    
            JDBCUtil.init(DRIVER, URL + prefix, USER, PASSWORD);
            
            Connection conn = JDBCUtil.getConnection();
            logger_.info("Connection: " + conn);
            logger_.info("Autocommit: " + conn.getAutoCommit());
            assertNotNull(conn);
            JDBCUtil.releaseConnection(conn);
        }
        finally
        {
            cleanup(prefix);
        }
        
    }

    /**
     * Tests executeQuery() on an empty table
     */
    public void testExecuteQueryZero() throws Exception
    {
        logger_.info("Running testExecuteQueryZero...");
        
        String prefix = "JDBCUtilTest_testQueryExecuteZero_" + 
            RandomUtil.nextInt(1000);
        JDBCUtil.init(DRIVER, URL + prefix, USER, PASSWORD);
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
            cleanup(prefix);
        }  
    }
    
    /**
     * Tests executeQuery() on table with only one row of data
     */
    public void testExecuteQueryOne() throws Exception
    {
        logger_.info("Running testExecuteQueryOne...");
        
        String prefix = "JDBCUtilTest_testQueryExecuteOne_" + 
            RandomUtil.nextInt(1000);
        JDBCUtil.init(DRIVER, URL + prefix, USER, PASSWORD);
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
            cleanup(prefix);
        }  
    }

    /**
     * Tests executeQuery() on table with many rows of data
     */
    public void testExecuteQueryMany() throws Exception
    {
        logger_.info("Running testExecuteQueryMany...");
        
        String prefix = "JDBCUtilTest_testQueryExecuteMany_" + 
            RandomUtil.nextInt(1000);
        JDBCUtil.init(DRIVER, URL + prefix, USER, PASSWORD);
        String table = "table_many";
        
        try
        {
            JDBCUtil.executeUpdate("create table " + table + "(id integer)");
            
            for(int i=0; i<100; i++)
                JDBCUtil.executeUpdate(
                    "insert into " +table + "(id) values (" + i + ")");
                    
            String results = JDBCUtil.executeQuery("select * from " + table);            
            assertTrue(results.indexOf("100 rows") >= 0);
            logger_.info("\n" + results);
        }
        finally
        {
            JDBCUtil.dropTable(table);
            cleanup(prefix);
        }  
    }

    /**
     * Tests init()
     * 
     * @throws Exception
     */
    public void testInit()
    {
        logger_.info("Running testInit...");
        
        try
        {
            JDBCUtil.init(DRIVER, URL, USER, PASSWORD);
            // No exception thrown = test passed
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Tests getSize() on an empty table
     */
    public void testGetSizeZero() throws Exception
    {
        logger_.info("Running testGetSizeZero...");
        
        String prefix = "JDBCUtilTest_testGetSizeZero_" + 
            RandomUtil.nextInt(1000);
        JDBCUtil.init(DRIVER, URL + prefix, USER, PASSWORD);
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
            cleanup(prefix); 
        }
    }

    /**
     * Tests getSize() on a table with one row
     */
    public void testGetSizeOne() throws Exception
    {
        logger_.info("Running testGetSizeOne...");
        
        String prefix = "JDBCUtilTest_testGetSizeOne_" + 
            RandomUtil.nextInt(1000);
        JDBCUtil.init(DRIVER, URL + prefix, USER, PASSWORD);
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
            cleanup(prefix); 
        }
    }


    /**
     * Tests getSize() on a table with one row
     */
    public void testGetSizeMany() throws Exception
    {
        logger_.info("Running testGetSizeMany...");
        
        String prefix = "JDBCUtilTest_testGetSizeMany_" + 
            RandomUtil.nextInt(1000);
        JDBCUtil.init(DRIVER, URL + prefix, USER, PASSWORD);
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
            cleanup(prefix); 
        }
    }


    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------
    
    /**
     * Cleans up HSQLDB file remnants
     */
    protected void cleanup(String prefix)
    {
        (new File(FileUtil.getTempDir() + FS + prefix +".properties")).delete();
        (new File(FileUtil.getTempDir() + FS + prefix +".data")).delete();
        (new File(FileUtil.getTempDir() + FS + prefix +".script")).delete();
    }
}




//public void testExecuteQueryZero() throws Exception
//{
//    logger_.info("Running testExecuteQueryZero...");
//        
//        
//    String prefix = "JDBCUtilTest_testQueryExecuteZero_" + 
//        RandomUtil.nextInt(1000);
//            
//    JDBCUtil.init(DRIVER, URL + prefix, USER, PASSWORD);
//        
//    logger_.info("URL: " + URL + prefix);
//        
//    String table = "table_zero";
//        
//    try
//    {
//        int rows = JDBCUtil.executeUpdate(
//            "create table " + table + "(id integer)");
//            
//        //logger_.info("Create table rows: " + rows);
//    
//        String results = JDBCUtil.executeQuery("select * from " + table);            
//    
////        JDBCUtil.executeUpdate(
////            "CREATE TABLE sample_table  " +
////            "   ( id INTEGER IDENTITY, str_col VARCHAR(256), num_col INTEGER)");
//    
////          db.update(
////              "INSERT INTO sample_table(str_col,num_col) VALUES('Ford', 100)");
////          db.update(
////              "INSERT INTO sample_table(str_col,num_col) VALUES('Toyota', 200)");
////          db.update(
////              "INSERT INTO sample_table(str_col,num_col) VALUES('Honda', 300)");
////          db.update(
////              "INSERT INTO sample_table(str_col,num_col) VALUES('GM', 400)");
//    
////        String results = JDBCUtil.executeQuery("SELECT * FROM sample_table");    
//                
////        CREATE [TEMP] [CACHED|MEMORY|TEXT] TABLE name 
////        ( columnDefinition [, ...] ) 
////
////        columnDefinition: 
////        column DataType [ [NOT] NULL] [PRIMARY KEY] 
////        DataType: 
////        { INTEGER | DOUBLE | VARCHAR | DATE | TIME |... }            
//            
//        assertNotNull(results);
//        assertTrue(results.indexOf("0 rows") >= 0);
//            
//        logger_.info("\n" + results);
//    }
//    finally
//    {
//        JDBCUtil.dropTable(table);
//        cleanup(prefix);
//    }  
//}

