package toolbox.util;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * JDBC Utility class that makes it easy to execute SQL statements and see the 
 * results as formatted text in a tabular layout. Connection pooling is turned
 * on by default and overcomes a failure with fast connect/disconnect when
 * using HSQL in server mode.
 * <p>
 * <b>Example:</b>
 * <pre class="snippet">
 * JDBCUtil.init("org.some.JDBCDriver", "jdbc:someurl", "username", "password");
 * String results = JDBCUtil.executeQuery("select * from users where age > 30");
 * System.out.println(results);
 * 
 * int numRows = JDBCUtil.executeUpdate("delete from users where age < 30");
 * System.out.println(numrows + " rows deleted.");
 * JDBCUtil.shutdown();
 * </pre>
 * <p>
 * <b>Sample Output:</b>
 * <pre class="snippet">
 * USERNAME  AGE  TITLE
 * ---------------------------
 * jsmith    23   Consultant
 * abrown    34   Integrator
 * hsimpson  45   Misc.
 *    
 * 4 rows
 * </pre>
 * 
 * @see toolbox.util.JDBCSession 
 */
public final class JDBCUtil
{
    private static final Logger logger_ = Logger.getLogger(JDBCUtil.class);

    //--------------------------------------------------------------------------
    // Connection Pool Constants
    //--------------------------------------------------------------------------

    /**
     * Connection pooling is turned on by default.
     */
    public static final boolean DEFAULT_POOLED = true;
    
    /**
     * Name of the internal session used exclusively by this class.
     */
    public static final String SESSION_NAME = "jdbcutil";
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Prevent construction of this static singleton utility class.
     */
    private JDBCUtil()
    {
    }

    //--------------------------------------------------------------------------
    // Delegates to JDBCSession
    //--------------------------------------------------------------------------

    /**
     * Initialzies the JDBC properties. Must be called before any of the other
     * methods are invoked. Connection pooling is set to the DEFAUILT_POOLED
     * value.
     * 
     * @param driver JDBC driver to use.
     * @param url URL to database resource.
     * @param user Username used for authentication.
     * @param password Password used for authentication.
     * @throws SQLException on SQL error.
     * @throws ClassNotFoundException if the JDBC driver is not found.
     * @throws IllegalAccessException if problems accessing jdbc driver.
     * @throws InstantiationException if problems instantiating the jdbc driver.
     */    
    public static void init(
        String driver, 
        String url, 
        String user, 
        String password) 
        throws ClassNotFoundException,
               SQLException,
               IllegalAccessException,
               InstantiationException 
    {
        JDBCSession.init(
            SESSION_NAME, driver, url, user, password, DEFAULT_POOLED);
    }
    
    
    /**
     * Initializes the JDBC properties. Must be called before any of the other
     * methods are invoked.
     * 
     * @param driver JDBC driver to use.
     * @param url URL to database resource.
     * @param user Username used for authentication.
     * @param password Password used for authentication.
     * @param pooled Set to true to use a connection pool. 
     * @throws SQLException on SQL error.
     * @throws ClassNotFoundException if the JDBC driver is not found.
     * @throws IllegalAccessException if problems accessing jdbc driver.
     * @throws InstantiationException if problems instantiating the jdbc driver.
     */
    public static void init(
        String driver,
        String url,
        String user,
        String password,
        boolean pooled)
        throws ClassNotFoundException,
               SQLException,
               IllegalAccessException,
               InstantiationException 
    {
        JDBCSession.init(
            SESSION_NAME,
            driver,
            url,
            user,
            password,
            pooled);
    }

    
    /**
     * Initializes the JDBC properties using a specific jdbc driver jar file.
     * Must be called before any of the other methods are invoked.
     * 
     * @param jarFile Jar file containing jdbc drivers.
     * @param driver JDBC driver to use.
     * @param url URL to database resource.
     * @param user Username used for authentication.
     * @param password Password used for authentication.
     * @throws SQLException on SQL error.
     * @throws MalformedURLException if jar file URL is invalid.
     * @throws ClassNotFoundException if the JDBC driver is not found.
     * @throws IllegalAccessException if problems accessing jdbc driver.
     * @throws InstantiationException if problems instantiating the jdbc driver.
     */    
    public static void init(
        String jarFile,
        String driver, 
        String url, 
        String user, 
        String password) 
        throws ClassNotFoundException, 
               SQLException, 
               MalformedURLException,
               IllegalAccessException, 
               InstantiationException
    {
        JDBCSession.init(
            SESSION_NAME, 
            new String[] {jarFile}, 
            driver, 
            url, 
            user, 
            password); 
            //false);
    }
    
    
    /**
     * Initializes the JDBC properties using a specific jdbc driver jar file.
     * Must be called before any of the other methods are invoked.
     * 
     * @param jarFiles Jar files containing jdbc drivers.
     * @param driver JDBC driver to use.
     * @param url URL to database resource.
     * @param user Username used for authentication.
     * @param password Password used for authentication.
     * @throws SQLException on SQL error.
     * @throws MalformedURLException if jar file URL is invalid.
     * @throws ClassNotFoundException if the JDBC driver is not found.
     * @throws IllegalAccessException if problems accessing jdbc driver.
     * @throws InstantiationException if problems instantiating the jdbc driver.
     */    
    public static void init(
        String[] jarFiles,
        String driver, 
        String url, 
        String user, 
        String password,
        boolean pooled) 
        throws ClassNotFoundException,
               SQLException, 
               MalformedURLException, 
               IllegalAccessException,
               InstantiationException
    {
        JDBCSession.init(
            SESSION_NAME, 
            jarFiles, 
            driver, 
            url, 
            user, 
            password); 
            //false);
    }
    
    
    /**
     * Returns a connection to the database.
     * 
     * @return Connection ready for use.
     * @throws SQLException on SQL error.
     */
    public static Connection getConnection() throws SQLException
    {
        return JDBCSession.getConnection(SESSION_NAME);
    }

    
    /**
     * Returns a list of the existing tables in the database.
     * 
     * @return String[]
     * @throws SQLException on DB error.
     */    
    public static String[] getTableNames() throws SQLException
    {
        return JDBCSession.getTableNames(SESSION_NAME);
    }


    /**
     * Executes a SQL INSERT, UPDATE, or DELETE statement.
     * 
     * @param sql Insert/update/delete sql statement to execute.
     * @return Number of rows affected by the execution of the sql statement.
     * @throws SQLException on any errors.
     **/   
    public static int executeUpdate(String sql) throws SQLException
    {
        return JDBCSession.executeUpdate(SESSION_NAME, sql);
    }   
    
    
    /**
     * Returns the count from a sql count statement.
     * 
     * @param sqlCountStmt Something like <code>select count(*) from user</code>
     * @return Count returned by the sql statement.
     * @throws SQLException on SQL error.
     * @throws IllegalArgumentException on invalid sql statement.
     */
    public static int executeCount(String sqlCountStmt) 
        throws SQLException, IllegalArgumentException
    {
        return JDBCSession.executeCount(SESSION_NAME, sqlCountStmt);
    }
    
    
    /**
     * Executes a SQL query statement and returns the result in a formatted
     * string.
     * 
     * @param sql Select statement to execute.
     * @return Formatted contents of the result set.
     * @throws SQLException on any SQL error.
     */
    public static String executeQuery(String sql) throws SQLException
    {
        return JDBCSession.executeQuery(SESSION_NAME, sql);
    } 

    
    /**
     * Executes a SQL query statement and returns the result in a two 
     * dimensional array.
     * 
     * @param sql Select statement to execute.
     * @return Array of the resultset's contents.
     * @throws SQLException on any SQL error.
     */
    public static Object[][] executeQueryArray(String sql) throws SQLException
    {
        return JDBCSession.executeQueryArray(SESSION_NAME, sql);
    }

    
    /**
     * Drops table w/o any complaints.
     * 
     * @param table Name of table to drop.
     */
    public static void dropTable(String table)
    {
        JDBCSession.dropTable(SESSION_NAME, table);
    }

    
    /**
     * Deregisters the current driver. Must call init() to use again.
     * 
     * @throws SQLException on SQL error.
     */
    public static void shutdown() throws SQLException 
    {
        JDBCSession.shutdown(SESSION_NAME);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Converts the contents of a result set into a two-dimentional array of
     * data. Each cell in the table contains an instance of the data type
     * returned from called ResultSet#getObject(). By default, column names
     * are not included in the returned array.
     * 
     * @param rs ResultSet to convert to an array.
     * @return Object[][]
     * @throws SQLException on sql error.
     * @see #toArray(ResultSet, boolean)
     */
    public static Object[][] toArray(ResultSet rs) throws SQLException
    {
        return toArray(rs, false);
    }
    
    
    /**
     * Converts the contents of a result set into a two-dimentional array of
     * data. Each cell in the table contains an instance of the data type
     * returned from called ResultSet#getObject().
     * 
     * @param rs ResultSet to convert to an array.
     * @param includeColumnHeader True to include column names as the first row
     *        of the returned two-dimensional array, false otherwise.
     * @return Object[][]
     * @throws SQLException on sql error.
     * @see #toArray(ResultSet)
     */
    public static Object[][] toArray(ResultSet rs, boolean includeColumnHeader) 
        throws SQLException
    {
        int numCols = rs.getMetaData().getColumnCount();
        List rows = new ArrayList();

        if (includeColumnHeader)
            rows.add(getColumnNames(rs));
        
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
            table = new Object[rows.size()][numCols];
        
            for (int i = 0, n = rows.size(); i < n; i++)
                table[i] = (Object[]) rows.get(i);
        
            rows.clear();
            rows = null;
        }
    
        return table;
    }
    

    /**
     * Formats a results set in a table like manner.
     * 
     * @param rs ResultSet to format.
     * @return Contents of result set formatted in a table like format.
     * @throws SQLException on sql error.
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
                (StringUtils.isBlank(colName) ? "[NULL]" : colName.trim());
                    
            header  [i - 1] = colName;
            colWidth[i - 1] = colName.length();
        }
        
        int numRows = 0;
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
                    value = "[NULL]";
   
                colWidth[i - 1] = Math.max(value.length(), colWidth[i - 1]);
                row[i - 1] = value;
            }

            rows.add(row);
        }

        for (int i = 0; i < colWidth.length; colWidth[i] = colWidth[i++] + 2);

        String[] divider = new String[numCols];
        
        for (int i = 0; i < numCols; i++)
            divider[i] = StringUtils.repeat("=", colWidth[i]);

        rows.add(0, divider);
        rows.add(2, divider);
        
        StringBuffer sb = new StringBuffer();            
        
        for (Iterator i = rows.iterator(); i.hasNext();) 
        {
            String[] row = (String[]) i.next();

            for (int j = 0; j < row.length; j++) 
                sb.append(StringUtils.rightPad(row[j], colWidth[j]));
            
            sb.append("\n");
        }

        sb.append("\n");
        sb.append(numRows + " rows\n");
        return sb.toString();
    }

    
    /**
     * Returns the names of the columns in a given ResultSet.
     * 
     * @param rs Result set.
     * @return String[]
     * @throws SQLException on error.
     */
    public static String[] getColumnNames(ResultSet rs) throws SQLException
    {
        int numCols = rs.getMetaData().getColumnCount();
        String[] colNames = new String[numCols];
        
        for (int i = 1; i <= numCols; i++) 
        {
            String colName = rs.getMetaData().getColumnLabel(i).trim();
            colNames[i-1] = StringUtils.isBlank(colName) ? "[NULL]" : colName;
        }
        
        return colNames;
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
     * @param rs Result set.
     * @return Size of the result set.
     * @throws SQLException if an SQL error occurs.
     */
    public static int getSize(ResultSet rs) throws SQLException 
    {
        // - remember current position in result set
        // - goto last row and get row number
        // - restore position in result set and return
        
        int currentPos = rs.getRow();
        rs.last();
        int last = rs.getRow();
        
        if (currentPos == 0)
            rs.first();
        else
            rs.absolute(currentPos);
            
        return last;
    }

    
    /**
     * Closes a ResultSet w/o any complaining.
     * 
     * @param rs Resultset to close.
     */
    public static void close(ResultSet rs)
    {
        if (rs == null)
            return;
            
        try
        {
            rs.close();
        }
        catch (SQLException e)
        {
            ; // Quiet please
        }
    }


    /**
     * Convenience method to close a Statement. Handles nulls and cases where
     * an exception is thrown by logging a warning. 
     * 
     * @param stmt Statement to close.
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
     * Releases a connection. Supresses any problems. 
     * 
     * @param connection Connection to release.
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
            catch (SQLException e) 
            {
                ; // Ignore
            }
        }
    }
    
    
    /**
     * Sends DriverManager debug output to System.out.
     * 
     * @param b True to turn debug on, false otherwise.
     */
    public static void setDebug(boolean b) {
    
        if (b)
            DriverManager.setLogWriter(
                new PrintWriter(new OutputStreamWriter(System.out)));
        else
            DriverManager.setLogWriter(null);
    }

    //--------------------------------------------------------------------------
    // Driver Proxy
    //--------------------------------------------------------------------------

    /**
     * DriverProxy is used to add an additional layer of method calls to load 
     * up a JDBC driver dynamically at run time. Basically, the class that is
     * loading the driver needs to be loadded by the new classloader itself.
     */    
    static class DriverProxy implements Driver
    {
        private Driver driver_;
    
        /**
         * Creates a DriverProxy.
         * 
         * @param d Driver
         */
        public DriverProxy(Driver d)
        {
            driver_ = d;
        }
    
        
        /**
         * @see java.sql.Driver#acceptsURL(java.lang.String)
         */
        public boolean acceptsURL(String u) throws SQLException
        {
            return driver_.acceptsURL(u);
        }
    
        
        /**
         * @see java.sql.Driver#connect(java.lang.String, java.util.Properties)
         */
        public Connection connect(String u, Properties p) throws SQLException
        {
            return driver_.connect(u, p);
        }
    
        
        /**
         * @see java.sql.Driver#getMajorVersion()
         */
        public int getMajorVersion()
        {
            return driver_.getMajorVersion();
        }
    
        
        /**
         * @see java.sql.Driver#getMinorVersion()
         */
        public int getMinorVersion()
        {
            return driver_.getMinorVersion();
        }
    
        
        /**
         * @see java.sql.Driver#getPropertyInfo(java.lang.String, 
         *      java.util.Properties)
         */
        public DriverPropertyInfo[] getPropertyInfo(String u, Properties p)
            throws SQLException
        {
            return driver_.getPropertyInfo(u, p);
        }
    
        
        /**
         * @see java.sql.Driver#jdbcCompliant()
         */
        public boolean jdbcCompliant()
        {
            return driver_.jdbcCompliant();
        }
    }    
}