package toolbox.util;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
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

import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

/**
 * JDBC Utility class that makes it easy to execute SQL statements and see the 
 * results as formatted text in a tabular layout. Connection pooling is turned
 * on by default and overcomes a failure with fast connect/disconnect when
 * using HSQL in server mode.
 * <p>
 * Sample usage:
 * <pre>
 * JDBCUtil.init("org.some.JDBCDriver", "jdbc:someurl", "username", "password");
 * String results = JDBCUtil.executeQuery("select * from users where age > 30");
 * System.out.println(results);
 * 
 * int numRows = JDBCUtil.executeUpdate("delete from users where age < 30");
 * System.out.println(numrows + " rows deleted.");
 * JDBCUtil.shutdown();
 * </pre>
 * <p>
 * Sample Output: 
 * <pre>
 * USERNAME  AGE  TITLE
 * ---------------------------
 * jsmith    23   Consultant
 * abrown    34   Integrator
 * hsimpson  45   Misc.
 *    
 * 4 rows
 * </pre>
 * 
 * TODO: Add connection pooling to init methods with jar.
 */
public final class JDBCUtil
{
    private static final Logger logger_ = Logger.getLogger(JDBCUtil.class);

    //--------------------------------------------------------------------------
    // Connection Pool Constants
    //--------------------------------------------------------------------------

    /**
     * Classname of the commons-dbcp pooling JDBC driver.
     */
    public static final String CONN_POOL_DRIVER = 
        "org.apache.commons.dbcp.PoolingDriver";
    
    /**
     * URL prefix of the connection pooling JDBC driver.
     */
    public static final String CONN_POOL_URL_PREFIX = 
        "jdbc:apache:commons:dbcp:"; 

    /**
     * Connection pooling is turned on by default.
     */
    public static final boolean DEFAULT_POOLED = true;
    
    /**
     * Name of the connection pool for use exclusively by this class.
     */	
    private static final String CONN_POOL_NAME = "jdbcutil";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * JDBC connection properties that contains username, password, etc. 
     */
    private static Properties connProps_;
 
    /**
     * Instance of the JDBC driver.
     */   
    private static Driver driver_;

    /**
     * Flag to used the pooled jdbc drviers.
     */
    private static boolean pooled_ = false;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Singleton - prevent construction.
     */
    private JDBCUtil()
    {
    }

    //--------------------------------------------------------------------------
    //  Public
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
        init(driver, url, user, password, DEFAULT_POOLED);
    }

    
    /**
     * Initialzies the JDBC properties. Must be called before any of the other
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
        pooled_ = pooled;
        
        if (!pooled_)
        {
            driver_ = (Driver) Class.forName(driver).newInstance();
            
            connProps_ = new Properties();
            connProps_.put("user", user);
            connProps_.put("password", password);
            connProps_.put("url", url);
        }
        else
        {
            Class.forName(driver);
            ObjectPool connPool = new GenericObjectPool(null);
        
            PoolableConnectionFactory pconnFactory = 
                new PoolableConnectionFactory(
                    new DriverManagerConnectionFactory(url, user, password), 
                    connPool, 
                    null, 
                    null, 
                    false,  // readonly
                    true);  // autocommit
	
            Class.forName(CONN_POOL_DRIVER);
	        
            PoolingDriver poolDriver = (PoolingDriver)
                DriverManager.getDriver(CONN_POOL_URL_PREFIX);
	        								  		
            poolDriver.registerPool(CONN_POOL_NAME, connPool);
	
            driver_ = poolDriver;
	        
            connProps_ = new Properties();
            connProps_.put("user", user);
            connProps_.put("password", password);
            connProps_.put("url", CONN_POOL_URL_PREFIX + CONN_POOL_NAME); 
        }
        
        Connection conn = getConnection();
        DatabaseMetaData meta = conn.getMetaData();
        
        logger_.debug("DB Connect: " + 
            meta.getDatabaseProductName() + 
                meta.getDatabaseProductVersion());
            
        releaseConnection(conn);
    }

    
    /**
     * Initialzies the JDBC properties using a specific jdbc driver jar file.
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
        init(new String[] {jarFile}, driver, url, user, password, false);
    }
    
    
    /**
     * Initialzies the JDBC properties using a specific jdbc driver jar file.
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
        pooled_ = pooled;
        
        // jarFiles[] -> jarURLs[]
        URL[] jarURLs = new URL[jarFiles.length];
        for (int i = 0; i< jarFiles.length; i++)
            jarURLs[i] = new File(jarFiles[i]).toURL();
        
        URLClassLoader ucl = new URLClassLoader(jarURLs);
        Driver d = (Driver) Class.forName(driver, true, ucl).newInstance();
        driver_ = new JDBCUtil.DriverProxy(d);
        DriverManager.registerDriver(driver_);

        // Blah
        driver_.acceptsURL("jdbc");
        driver_.getMajorVersion();
        driver_.getMinorVersion();
        driver_.jdbcCompliant();
        
        connProps_ = new Properties();
        connProps_.put("user", user);
        connProps_.put("password", password);
        connProps_.put("url", url);
        
        Connection conn = getConnection();
        DatabaseMetaData meta = conn.getMetaData();
        
        logger_.debug("Connected to " + 
            meta.getDatabaseProductName() + 
                meta.getDatabaseProductVersion());
            
        releaseConnection(conn);
    }
    
    
    /**
     * Returns a connection to the database.
     * 
     * @return Connection ready for use.
     * @throws SQLException on SQL error.
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
     * Returns a list of the existing tables in the database.
     * 
     * @return String[]
     * @throws SQLException on DB error.
     */    
    public static String[] getTableNames() throws SQLException
    {
        Connection conn = JDBCUtil.getConnection();
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = meta.getTables(null, null, null, null);
        ResultSetMetaData rsmeta = rs.getMetaData();
        int cnt = rsmeta.getColumnCount();
        List tables = new ArrayList();
        
        while (rs.next())
        {
            String tableName = rs.getString("TABLE_NAME");
            tables.add(tableName);
        }        
        
        JDBCUtil.releaseConnection(conn);
        return (String[]) tables.toArray(new String[0]);
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
        Connection conn = null;
        int rows = -1;
        
        try 
        {
            conn = getConnection();    
            Statement stmt = conn.createStatement();
            rows = stmt.executeUpdate(prepSQL(sql));
        } 
        finally 
        {
            releaseConnection(conn); 
        }
        
        return rows;
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
        // Validate that the sql stmt is selecting a count
        String[] tokens = StringUtils.split(sqlCountStmt);
        Validate.isTrue(tokens.length > 2, "Not a valid SQL count statement");
        Validate.isTrue(tokens[0].equalsIgnoreCase("select"));
        Validate.isTrue(tokens[1].toLowerCase().startsWith("count"));
        
        Object[][] results = executeQueryArray(prepSQL(sqlCountStmt));
        return Integer.parseInt(results[0][0].toString());
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
        String formattedResults = null;
        Connection conn = null;
            
        try
        {
            conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(prepSQL(sql));
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
     * Executes a SQL query statement and returns the result in a two 
     * dimensional array.
     * 
     * @param sql Select statement to execute.
     * @return Array of the resultset's contents.
     * @throws SQLException on any SQL error.
     */
    public static Object[][] executeQueryArray(String sql) throws SQLException
    {
        Object[][] data = null;
        Connection conn = null;
        PreparedStatement stmt = null;

        try
        {
            conn = getConnection();
            stmt = conn.prepareStatement(prepSQL(sql));
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
     * @param rs ResultSet to format.
     * @return Contents of result set formatted in a table like format.
     * @throws SQLException on sql error.
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
                    value = "[NULL]";
   
                colWidth[i - 1] = Math.max(value.length(), colWidth[i - 1]);
                row[i - 1] = value;
            }

            rows.add(row);
        }

        time.setEndTime();

        for (int i = 0; i < colWidth.length; colWidth[i] = colWidth[i++] + 2);

        String[] dashes = new String[numCols];
        
        for (int i = 0; i < numCols; i++)
            dashes[i] = StringUtils.repeat("-", colWidth[i]);

        rows.add(1, dashes);

        StringBuffer sb = new StringBuffer();            
        
        for (Iterator i = rows.iterator(); i.hasNext();) 
        {
            String[] row = (String[]) i.next();

            for (int j = 0; j < row.length; j++) 
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
     * Drops table w/o any complaints.
     * 
     * @param table Name of table to drop.
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
     * Deregisters the current driver. Must call init() to use again.
     * 
     * @throws SQLException on SQL error.
     */
    public static void shutdown() throws SQLException 
    {
        if (pooled_) 
        {
            PoolingDriver pd = (PoolingDriver) driver_;
            
            try 
            {
                pd.closePool(CONN_POOL_NAME);
            }
            catch (Exception e) 
            {
                logger_.error(e);
            }
        }
        
        driver_ = null;
        connProps_ = null;        
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
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Trims whitespace and remove trailing semicolon if any from the given
     * sql statement.
     * 
     * @param sql SQL statement to prepare for execution.
     * @return String
     */
    private static final String prepSQL(String sql) 
    {
        return sql = StringUtils.chomp(sql.trim(), ";");
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