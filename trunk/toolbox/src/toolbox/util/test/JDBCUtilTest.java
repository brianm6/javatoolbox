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
    
    
    public void testFormatSQL()
    {
        logger_.info("Running testFormatSQL...");
        
        String sql = "select * from user";
        String fsql = JDBCUtil.formatSQL(sql);
        
        assertNotNull(fsql);
        logger_.info("\n" + fsql);
        
        sql = "select one, two, three from user where name like 'A%' and id = 34533 group by lastName";
        fsql = JDBCUtil.formatSQL(sql);
        
        assertNotNull(fsql);
        logger_.info("\n" + fsql);
        
        sql = "CREATE TABLE Location (id CHAR(8) NOT NULL, sequence CHAR(8) NOT NULL, version CHAR(8) NOT NULL, capability VARCHAR(16), sequence CHAR(8) NOT NULL, version CHAR(8) NOT NULL, capability VARCHAR(16), applyPwswTiny CHAR(1), locationNumber VARCHAR(32), localCompanyName VARCHAR(100), localCompanyNamePron VARCHAR(100), companyName VARCHAR(100), dbaName VARCHAR(100), contactAddress_addrLineOne VARCHAR(60), contactAddress_addrLineTwo VARCHAR(60), contactAddress_addrLineThree VARCHAR(60), contactAddress_addrLineFour VARCHAR(60), contactAddress_city VARCHAR(35), contactAddress_country CHAR(2), contactAddress_country_L VARCHAR(35), contactAddress_state CHAR(6), contactAddress_state_L VARCHAR(35), contactAddress_zip VARCHAR(16), hasReturnedMail CHAR(1), voiceOne_ctryCode VARCHAR(100), voiceOne_number VARCHAR(32), faxOne_ctryCode VARCHAR(100), faxOne_number VARCHAR(32), internetInfo_locationEmail VARCHAR(100), internetInfo_locationURL VARCHAR(150), parentID VARCHAR(100), ibmCustNum VARCHAR(10), federalTaxID VARCHAR(50), corporationNumber VARCHAR(8), ppaID VARCHAR(10), numberLocations INTEGER, yearStarted INTEGER, geo CHAR(2), geo_L VARCHAR(35), geoRegion CHAR(6), geoRegion_L VARCHAR(35), numEmployees INTEGER, bpdbID VARCHAR(26), cmrAction CHAR(6), cmrAction_L VARCHAR(35), cmrRetry CHAR(1), cmr_customerNumber VARCHAR(10), cmr_date DATE, cmr_existing CHAR(1), cmr_firstName VARCHAR(100), cmr_lastName VARCHAR(100), cmr_phone VARCHAR(100), cmr_email VARCHAR(100), cmr_contactNumber VARCHAR(10), cmr_deleted CHAR(1), cmr_deniedParty CHAR(1), cmr_badAddress CHAR(1), cmr_duplicate CHAR(1), cmr_duplicateNumber VARCHAR(10), cmr_duplicateName VARCHAR(35), cmr_success CHAR(1), cmr_message VARCHAR(35), profileUpdateUser VARCHAR(40), profileUpdateTimestamp DATE, dplCheck CHAR(6), dplCheck_L VARCHAR(35), supplierLists CHAR(6), supplierLists_L VARCHAR(35), russianFederation CHAR(6), russianFederation_L VARCHAR(35), PRIMARY KEY(id));";
        fsql = JDBCUtil.formatSQL(sql);
        
        logger_.info("\n" + fsql);
        
        sql =
        "        CREATE TABLE SAPCateg.SAPCategoryType("
        + "                id CHAR(8) NOT NULL,"
        + "                sequence CHAR(8) NOT NULL,"
        + "                version CHAR(8) NOT NULL,"
        + "                domain VARCHAR(50),"
        + "                PRIMARY KEY(id))"
        + "        in DATA01 index in INX01;"
        + "        COMMENT ON TABLE SAPCateg.SAPCategoryType is '';"
        + "        COMMENT ON COLUMN SAPCateg.SAPCategoryType.domain is ' ';"
        + "        CREATE TABLE SAPCateg.keywordEntry("
        + "                id CHAR(8) NOT NULL,"
        + "                child_id CHAR(8) NOT NULL,"
        + "                code VARCHAR(100),"
        + "                description VARCHAR(100),"
        + "                categoryType CHAR(6),"
        + "                categoryType_L VARCHAR(35),"
        + "                PRIMARY KEY(id,child_id),"
        + "                FOREIGN KEY(id) REFERENCES SAPCateg.SAPCategoryType(id))"
        + "        in DATA01 index in INX01;"
        + "        COMMENT ON TABLE SAPCateg.keywordEntry is ' ';"
        + "        COMMENT ON COLUMN SAPCateg.keywordEntry.code is ' ';"
        + "        COMMENT ON COLUMN SAPCateg.keywordEntry.description is ' ';"
        + "        COMMENT ON COLUMN SAPCateg.keywordEntry.categoryType is ' ';"
        + "        COMMENT ON COLUMN SAPCateg.keywordEntry.categoryType_L is 'null (L10N)';";        
        
        fsql = JDBCUtil.formatSQL(sql);
        logger_.info("\n" + fsql);
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