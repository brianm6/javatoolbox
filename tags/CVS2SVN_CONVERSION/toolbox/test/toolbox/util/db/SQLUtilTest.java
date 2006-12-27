package toolbox.util.db;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.util.db.SQLUtil}.
 */
public class SQLUtilTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(SQLUtilTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(SQLUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests parseSQL(String) for a variety of empty sql statements.
     */
    public void testParseSQL_EmptyStatement() throws Exception {
        
        logger_.info("Running testParseSQL_EmptyStatement...");
        
        String sql = null;
        String[] stmts = null;

        // Empty sql statement
        sql = "";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 0);

        // Empty sql statement with just a terminator
        sql = ";";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 0);

        // Empty sql statement with just two terminators
        sql = ";;";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 0);

        // Empty sql statement with space separated terminators
        sql = "; ;";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 0);
        
        // Empty sql statement with line separated terminators
        sql = ";\n;\n;";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 0);
    }
    
    /**
     * Tests parseSQL(String) for empty sql statements with comments.
     */
    public void testParseSQL_EmptyStatementWithComments() throws Exception {
        
        logger_.info("Running testParseSQL_EmptyStatementWithComments...");
        
        String sql = null;
        String[] stmts = null;

        // Just a comment
        sql = "-- hello";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 0);
        
        // Just a comment with preceeding whitespace on previous line
        sql = "   \n-- hello";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 0);
        
        // Just a comment with suffixed whitespace on next line
        sql = "-- hello\n    ";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 0);
        
        // Just a comment with preceeding whitespace on line
        sql = "   \n-- hello";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 0);
    }
    
    /**
     * Tests parseSQL(String) for one single line sql statement.
     */
    public void testParseSQL_OneSingleLineStatement() throws Exception {
        
        logger_.info("Running testParseSQL_OneSingleLineStatement...");
        
        String sql = null;
        String[] stmts = null;

        // Simplest single line sql statement
        sql = "delete from some_table;";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 1);
        assertEquals(sql, stmts[0]);
        
        // Single line sql statement with spaces in front and rear
        sql = "    delete from some_table;      ";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 1);
        assertEquals(sql.trim(), stmts[0]);
        
        // Single sql statement with new lines
        sql = "\n    delete from some_table;  \n  \n  ";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 1);
        assertEquals(sql.trim(), stmts[0]);
    }    
    
    /**
     * Tests parseSQL(String) for a one multiline sql statement.
     */
    public void testParseSQL_OneMultilineStatement() throws Exception {
        
        logger_.info("Running testParseSQL_OneMultilineStatement...");
        
        String sql = null;
        String[] stmts = null;
        
        // Multiline single sql statement
        sql = "delete\n from\n some_table;";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 1);
        assertEquals(sql, stmts[0]);
        
        // Multiline single sql statement with spaces in front and rear
        sql = "    delete\n from\n some_table;    ";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 1);
        assertEquals(sql.trim(), stmts[0]);
        
        // Multiline single sql statement with new lines in front and rear
        sql = " \n \n    delete\n from\n some_table;   \n  \n ";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 1);
        assertEquals(sql.trim(), stmts[0]);
    }    
    
    /**
     * Tests parseSQL(String) for one single line sql statement with comments.
     */
    public void testParseSQL_OneSingleLineStatementWithComments() 
        throws Exception {
        
        logger_
            .info("Running testParseSQL_OneSingleLineStatementWithComments...");
        
        String sql = null;
        String[] stmts = null;

        // Simplest single line sql statement with comment before 
        sql = "-- This is a comment\ndelete from some_table;";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 1);
        assertEquals("delete from some_table;", stmts[0]);
        
        // Single line sql statement with spaces in front and rear and comments
        sql = "    \n-- This is a comment   \ndelete from some_table;      ";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 1);
        assertEquals("delete from some_table;", stmts[0]);
        
        // Single sql statement with new lines and comments
        sql = "--This is a comment\n    delete from some_table;  \n  \n  ";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 1);
        assertEquals("delete from some_table;", stmts[0]);
        
        // Simplest single line sql statement with comment before and after 
        sql = "-- Comment before\ndelete from some_table;\n-- Comment after";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 1);
        assertEquals("delete from some_table;", stmts[0]);
    }    
    
    /**
     * Tests parseSQL(String) for a one multiline sql statment with comments.
     */
    public void testParseSQL_OneMultilineStatementWithComments() 
        throws Exception {
        
        logger_
            .info("Running testParseSQL_OneMultilineStatementWithComments...");
        
        String sql = null;
        String[] stmts = null;
        
        // Multiline sql statement with comment before 
        sql = "-- This is a comment\ndelete\n from\n some_table;";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 1);
        assertEquals("delete\n from\n some_table;", stmts[0]);
        
        // Multiline sql statement with spaces in front and rear and comments
        sql = "    \n-- This is a comment   \ndelete\n from\n some_table;   ";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 1);
        assertEquals("delete\n from\n some_table;", stmts[0]);
        
        // Multiline sql statement with new lines and comments
        sql = "--This is a comment\n    delete\n from\n some_table;  \n  \n  ";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 1);
        assertEquals("delete\n from\n some_table;", stmts[0]);
        
        // Multiline line sql statement with comment before and after 
        sql = "-- Comment before\ndelete\n from\n some_table;\n-- after";
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 1);
        assertEquals("delete\n from\n some_table;", stmts[0]);
    }
    
    /**
     * Tests parseSQL(String) for many single line sql statements.
     */
    public void testParseSQL_ManySingleLineStatements() throws Exception {
        
        logger_.info("Running testParseSQL_ManySingleLineStatements...");
        
        String sql = null;
        String[] stmts = null;

        // Multiple single line sql statements
        sql = "delete from some_table; \n" 
            + "select * from this_table;";
        
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 2);
        assertEquals("delete from some_table;", stmts[0]);
        assertEquals("select * from this_table;", stmts[1]);
    }    
    
    /**
     * Tests parseSQL(String) for many single line sql statements with comments.
     */
    public void testParseSQL_ManySingleLineStatementsWithComments() 
        throws Exception {
        
        logger_.info(
            "Running testParseSQL_ManySingleLineStatementsWithComments...");
        
        String sql = null;
        String[] stmts = null;

        // Multiple single line sql statements with interspersed comments
        sql = "-- Comment1 \n" 
            + "delete from some_table; \n" 
            + "-- Comment2 \n"
            + "select * from this_table;";
        
        stmts = SQLUtil.parseSQL(sql);
        assertTrue(stmts.length == 2);
        assertEquals("delete from some_table;", stmts[0]);
        assertEquals("select * from this_table;", stmts[1]);
    }

    /**
     * Tests parseSQL(String) for many multiline sql statements.
     */
    public void testParseSQL_ManyMultilineStatements() 
        throws Exception {
        
        logger_.info("Running testParseSQL_ManyMultilineStatements...");
        
        String sql = null;
        String[] stmts = null;

        // Multiple multiline sql statements
        sql = "delete\n from some_table; \n" 
            + "select *\n from this_table;\n";
       
       stmts = SQLUtil.parseSQL(sql);
       assertEquals(2, stmts.length);
       assertEquals("delete\n from some_table;", stmts[0]);
       assertEquals("select *\n from this_table;", stmts[1]);
    }

    /**
     * Tests parseSQL(String) for many multiline sql statements with comments.
     */
    public void testParseSQL_ManyMultilineStatementsWithComments() 
        throws Exception {
        
        logger_.info(
            "Running testParseSQL_ManyMultilineStatementsWithComments...");
        
        String sql = null;
        String[] stmts = null;

        // Multiple multiline sql statements with interspersed comments
        sql = "-- Comment3.1 \n" 
           + "delete\n from some_table; \n" 
           + "-- Comment3.2 \n"
           + "select *\n from this_table;\n"
           + "-- Comment3.3";
       
       stmts = SQLUtil.parseSQL(sql);
       assertEquals(2, stmts.length);
       assertEquals("delete\n from some_table;", stmts[0]);
       assertEquals("select *\n from this_table;", stmts[1]);
    }
}