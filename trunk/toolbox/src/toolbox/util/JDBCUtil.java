package toolbox.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * JDBC Utility class that makes it easy to execute SQL statements and
 * see the results as formatted text in a tabular layout.
 * <p>
 * Typical usage:
 * <p>
 * <pre>
 * 
 * JdbcUtilities.init("driver", "url", "username", "password");
 * 
 * String results = 
 *     JdbcUtilities.executeQuery(
 *         "select * from users where age > 30");
 * 
 * System.out.println(results);
 * 
 * int numRows = 
 *     JdbcUtilities.executeUpdate(
 *         "delete from users where age < 30");
 * 
 * System.out.println(numrows + " rows deleted.");
 * 
 * </pre>
 * <p>
 * Sample Output 
 * <p>
 * <pre>
 * 
 * USERNAME  AGE  TITLE
 * ---------------------------
 * jsmith    23   Consultant
 * abrown    34   Integrator
 * hsimpson  45   Misc.
 *    
 * 4 rows
 * 
 * </pre>
 */
public final class JDBCUtil
{
    public static final Logger logger_ =
        Logger.getLogger(JDBCUtil.class);

    // Clover private constructor workaround
    static { new JDBCUtil(); }

    /** 
     * JDBC connection properties 
     */
    private static Properties connProps_;
        
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Private constructor
     */
    private JDBCUtil()
    {
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------

    /**
     * Initialzies the JDBC properties. Must be called before any of the other
     * methods are use
     * 
     * @param driver JDBC driver to use
     * @param url URL to database resource
     * @param user Username used for authentication
     * @param password Password used for authentication
     * @throws SQLException on SQL error
     * @throws ClassNotFoundException if the JDBC driver is not found
     */    
    public static void init(
        String driver, 
        String url, 
        String user, 
        String password) throws ClassNotFoundException, SQLException
    {
        // Will force DriverManager.register() to get called
        Class.forName(driver);
        connProps_ = new Properties();
        connProps_.put("user"     , user);
        connProps_.put("password" , password);
        connProps_.put("url"      , url);
        
        // Get meta data 
        Connection conn = getConnection();
        DatabaseMetaData meta = conn.getMetaData();
        
        logger_.debug("DB Connect: " + 
            meta.getDatabaseProductName() + meta.getDatabaseProductVersion());
            
        releaseConnection(conn);
    }


    /**
     * Returns a connection to the database
     * 
     * @return Connection ready for use
     * @throws SQLException on SQL error
     */
    public static Connection getConnection() throws SQLException
    {
        if (connProps_ == null)
            throw new IllegalStateException(
                "Must call init() first to set the JDBC configuration.");
        
        Connection conn =        
            DriverManager.getConnection(
                connProps_.getProperty("url"), connProps_);
                        
        return conn;
    }

    
    /**
     * Executes a SQL INSERT, UPDATE, or DELETE statement
     * 
     * @param sql Insert/update/delete sql statement to execute
     * @return Number of rows affected by the execution of the sql statement
     * @throws SQLException on any errors
     */   
    public static int executeUpdate(String sql) throws SQLException
    {
        Connection conn = null;
        int rows = -1;
        
        try 
        {
            conn = getConnection();    
            Statement stmt = conn.createStatement();
            rows = stmt.executeUpdate(sql);
        } 
        finally 
        {
            releaseConnection(conn); 
        }
        
        return rows;
    }   
    
    
    /**
     * Executes a SQL query statement and returns the result in a formatted
     * string.
     * 
     * @param sql Select statement to execute
     * @return Formatted contents of the result set
     * @throws SQLException on any SQL error
     */
    public static String executeQuery(String sql) throws SQLException
    {
        String formattedResults = null;
        Connection conn = null;
        
        try
        {
            conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet results = stmt.executeQuery();
            formattedResults = format(results);
        }
        finally
        {
            releaseConnection(conn); 
        }
        
        return formattedResults;
    } 

    /**
     * Executes a SQL query statement and returns the result in a formatted
     * string.
     * 
     * @param sql Select statement to execute
     * @return Formatted contents of the result set
     * @throws SQLException on any SQL error
     */
    public static Object[][] executeQueryArray(String sql) throws SQLException
    {
        Object[][] data = null;
        Connection conn = null;
        PreparedStatement stmt = null;

        try
        {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            ResultSet results = stmt.executeQuery();
            data = toArray(results);
        }
        finally
        {
            close(stmt);
            releaseConnection(conn);
        }

        return data;
    }


    /**
     * Formats a result set in a table like manner.
     * 
     * @param rs ResultSet to format
     * @return Contents of result set formatted in a table like format
     * @throws SQLException on sql error
     */
    public static Object[][] toArray(ResultSet rs) throws SQLException
    {
        ResultSetMetaData meta = rs.getMetaData();
    
        int numCols = meta.getColumnCount();
        List rows = new ArrayList();
    
        while (rs.next())
        {
            Object[] row = new Object[numCols];
    
            for (int i = 0; i < numCols; i++)
                row[i] = rs.getObject(i + 1);
    
            rows.add(row);
        }

        Object[][] table = new Object[0][0];
        
        if (!rows.isEmpty())
        {    
            table = new Object[numCols][rows.size()];
        
            for (int i = 0, n = rows.size(); i < n; i++)
            {
                Object[] row = (Object[]) rows.get(i);
        
                for (int j = 0; j < row.length; j++)
                    table[j][i] = row[j];
            }
        
            rows.clear();
            rows = null;
        }
    
        return table;
    }


    /**
     * Formats a results set in a table like manner
     * 
     * @param rs ResultSet to format
     * @return Contents of result set formatted in a table like format
     * @throws SQLException on sql error
     */
    public static String format(ResultSet rs) throws SQLException 
    {
        ResultSetMetaData meta = rs.getMetaData();
    
        int    numCols    = meta.getColumnCount();
        String header[]   = new String[numCols];
        int    colWidth[] = new int[numCols];                
        
        // Figure out column headers and width
        for (int i = 1; i <= numCols; i++) 
        {
            String colName = meta.getColumnLabel(i);
            
            colName = 
                (StringUtil.isNullOrBlank(colName) ? "[NULL]" : colName.trim());
                    
            header  [i-1] = colName;
            colWidth[i-1] = colName.length();
        }
        
        int numRows = 0;
        ElapsedTime time = new ElapsedTime();
        List rows = new ArrayList();
        rows.add(header);

        // Rows        
        while (rs.next()) 
        {
            ++numRows;

            String row[] = new String[numCols];
             
            for (int i = 1; i <= numCols; i++) 
            {
                Object obj = null;
                
                try 
                {
                    if (meta.getColumnType(i) == Types.LONGVARBINARY)
                        obj = rs.getBytes(i);
                    else
                        obj = rs.getObject(i);
                    
                    if (obj instanceof byte[])
                        obj = (new String((byte[]) obj));
                }
                catch (NullPointerException e) 
                {
                    obj = "[NULL]";
                }

                String value = null;
                
                if (obj != null)
                    value = obj.toString().trim();
                else
                    value= "[NULL]";
                
                // Add extra spaces at the end    
                colWidth[i-1] = Math.max(value.length(), colWidth[i-1]);// + 2;
                row[i-1] = value;
            }

            rows.add(row);
        }

        time.setEndTime();

        for (int i=0; i<colWidth.length; colWidth[i] = colWidth[i++] + 2);

        String[] dashes = new String[numCols];
        
        for(int i=0; i<numCols; i++)
            dashes[i] = StringUtil.repeat("-", colWidth[i]);

        rows.add(1, dashes);

        StringBuffer sb = new StringBuffer();            
        
        for (Iterator i = rows.iterator(); i.hasNext(); ) 
        {
            String[] row = (String[]) i.next();

            for (int j=0; j < row.length; j++) 
                sb.append(StringUtil.left(row[j], colWidth[j]));
            
            sb.append("\n");
        }

        sb.append("\n");
        sb.append(numRows + " rows\n");
        
        return sb.toString();
    }

    
    /**
     * Returns the size of a given result set. The position of the cursor is
     * not affected.<p>
     * 
     * <pre>
     * NOTE: This may not be an accurate representation of the true size
     *       of a large resultset due to the limitations of the JDBC 
     *       interface.
     * </pre>
     *
     * @param rs Result set
     * @return Size of the result set
     * @throws SQLException if an SQL error occurs
     */
    public static int getSize(ResultSet rs) throws SQLException 
    {
        // - remember current position in result set
        // - goto last row and get row number
        // - restore position in result set and return
        
        int currentPos = rs.getRow();
        rs.last();
        int last = rs.getRow();
        
        if(currentPos == 0)
            rs.first();
        else
            rs.absolute(currentPos);
            
        return last;
    }

    
    /**
     * Drops table w/o any complaints
     * 
     * @param table Table name
     */
    public static void dropTable(String table)
    {
        if (table == null)
            return;
            
        try
        {
            executeUpdate("drop table " + table);
        }
        catch (SQLException e)
        {
            ; // Quiet please
        }
    }

    
    /**
     * Closes a ResultSet w/o any complaining
     * 
     * @param rs Resultset to close
     */
    public static void close(ResultSet rs)
    {
        if (rs == null)
            return;
            
        try
        {
            rs.close();
        }
        catch(SQLException e)
        {
            ; // Quiet please
        }
    }


    /**
     * Convenience method to close a Statement. Handles nulls and cases where
     * an exception is thrown by logging a warning. 
     * 
     * @param stmt Statement to close
     */
    public static void close(Statement stmt)
    {
        if (stmt == null)
            return;

        try
        {
            stmt.close();
            stmt = null;
        }
        catch (SQLException e)
        {
            ; // Ignore
        }
    }

    
    /**
     * Releases a connection. Supresses any problems 
     * 
     * @param connection Connection to release
     */
    public static void releaseConnection(Connection connection) 
    {
        if (connection != null)
        {
            try 
            { 
                if (!connection.isClosed())
                {
                    connection.close();
                    connection = null;
                }
            } 
            catch(SQLException e) 
            {
                ; // Ignore
            }
        }
    }
}