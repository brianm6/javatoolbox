package toolbox.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public final class JDBCSession
{
    private static final Logger logger_ = Logger.getLogger(JDBCSession.class);

    //--------------------------------------------------------------------------
    // Connection Pool Constants
    //--------------------------------------------------------------------------

    /**
     * Classname of the commons-dbcp pooling JDBC driver.
     */
    private static final String CONN_POOL_DRIVER = 
        "org.apache.commons.dbcp.PoolingDriver";
    
    /**
     * URL prefix of the connection pooling JDBC driver.
     */
    private static final String CONN_POOL_URL_PREFIX = 
        "jdbc:apache:commons:dbcp:"; 

    /**
     * Name of the connection pool for use exclusively by this class.
     */	
    private static final String CONN_POOL_NAME = "jdbcsession_";
    
    /**
     * Connection pooling is turned on by default.
     */
    private static final boolean DEFAULT_POOLED = true;

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private static Map sessionMap_ = new HashMap();

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Singleton - prevent construction.
     */
    private JDBCSession()
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
        String sessionName,
        String driver, 
        String url, 
        String user, 
        String password) 
        throws ClassNotFoundException,
               SQLException,
               IllegalAccessException,
               InstantiationException 
    {
        init(sessionName, driver, url, user, password, DEFAULT_POOLED);
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
        String sessionName,
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
        
        Session session = new Session(sessionName, null, null, pooled);
        
        if (!session.isPooled())
        {
            session.setDriver((Driver) Class.forName(driver).newInstance());
            
            Properties connProps = new Properties();
            connProps.put("user", user);
            connProps.put("password", password);
            connProps.put("url", url);
            session.setConnProps(connProps);
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
	        								  		
            poolDriver.registerPool(CONN_POOL_NAME + sessionName, connPool);
	
            session.setDriver(poolDriver);
	        
            Properties connProps = new Properties();
            connProps.put("user", user);
            connProps.put("password", password);
            connProps.put(
                "url", CONN_POOL_URL_PREFIX + CONN_POOL_NAME + sessionName);
            session.setConnProps(connProps);
        }
        
        sessionMap_.put(sessionName, session);
        
        Connection conn = getConnection(sessionName);
        DatabaseMetaData meta = conn.getMetaData();
        
        logger_.debug("DB Connect: " + 
            meta.getDatabaseProductName() + 
                meta.getDatabaseProductVersion());
            
        JDBCUtil.releaseConnection(conn);
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
        String sessionName,
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
        init(sessionName, new String[] {jarFile}, driver, url, user, password);
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
        String sessionName,
        String[] jarFiles,
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
        
        Properties connProps = new Properties();
        connProps.put("user", user);
        connProps.put("password", password);
        connProps.put("url", url);
        
        Session session = new Session(sessionName, connProps, null, false);

        // jarFiles[] -> jarURLs[]
        URL[] jarURLs = new URL[jarFiles.length];
        for (int i = 0; i< jarFiles.length; i++)
            jarURLs[i] = new File(jarFiles[i]).toURL();
        
        URLClassLoader ucl = new URLClassLoader(jarURLs);
        Driver d = (Driver) Class.forName(driver, true, ucl).newInstance();
        session.setDriver(new JDBCUtil.DriverProxy(d));
        DriverManager.registerDriver(session.getDriver());

        // Blah
        session.getDriver().acceptsURL("jdbc");
        session.getDriver().getMajorVersion();
        session.getDriver().getMinorVersion();
        session.getDriver().jdbcCompliant();
        
        sessionMap_.put(sessionName, session);

        // Verify connection
        Connection conn = getConnection(sessionName);
        DatabaseMetaData meta = conn.getMetaData();
        
        logger_.debug("Connected to " + 
            meta.getDatabaseProductName() + 
                meta.getDatabaseProductVersion());
            
        JDBCUtil.releaseConnection(conn);
    }
    
    
    /**
     * Returns a connection to the database.
     * 
     * @return Connection ready for use.
     * @throws SQLException on SQL error.
     */
    public static Connection getConnection(String sessionName) throws SQLException
    {
        Session session = getSession(sessionName);
        
        Connection conn =        
            DriverManager.getConnection(
                session.getConnProps().getProperty("url"), 
                session.getConnProps());
                        
        return conn;
    }

    
    /**
     * Returns a list of the existing tables in the database.
     * 
     * @return String[]
     * @throws SQLException on DB error.
     */    
    public static String[] getTableNames(String sessionName) throws SQLException
    {
        Connection conn = getConnection(sessionName);
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
    public static int executeUpdate(String sessionName, String sql) 
        throws SQLException
    {
        Connection conn = null;
        int rows = -1;
        
        try 
        {
            conn = getConnection(sessionName);    
            Statement stmt = conn.createStatement();
            rows = stmt.executeUpdate(sql);
        } 
        finally 
        {
            JDBCUtil.releaseConnection(conn); 
        }
        
        return rows;
    }   

    
    /**
     * Retrives the given session from the session map.
     * 
     * @param sessionName
     * @return
     */
    public static Session getSession(String sessionName)
    {
        Session session = (Session) sessionMap_.get(sessionName);
        
        if (session == null)
            throw new IllegalArgumentException(
                "Session " + sessionName + " not found.");
        else
            return session;
    }
    
    
    /**
     * Retrives the given session from the session map.
     * 
     * @param sessionName
     * @return
     */
    public static int getSessionCount()
    {
        return sessionMap_.size();
    }

    
    /**
     * Returns the count from a sql count statement.
     * 
     * @param sqlCountStmt Something like <code>select count(*) from user</code>
     * @return Count returned by the sql statement.
     * @throws SQLException on SQL error.
     * @throws IllegalArgumentException on invalid sql statement.
     */
    public static int executeCount(String sessionName, String sqlCountStmt) 
        throws SQLException, IllegalArgumentException
    {
        // Validate that the sql stmt is selecting a count
        String[] tokens = StringUtils.split(sqlCountStmt);
        Validate.isTrue(tokens.length > 2, "Not a valid SQL count statement");
        Validate.isTrue(tokens[0].equalsIgnoreCase("select"));
        Validate.isTrue(tokens[1].toLowerCase().startsWith("count"));
        
        Object[][] results = executeQueryArray(sessionName, sqlCountStmt);
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
    public static String executeQuery(String sessionName, String sql) 
        throws SQLException
    {
        String formattedResults = null;
        Connection conn = null;
        
        try
        {
            conn = getConnection(sessionName);
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet results = stmt.executeQuery();
            formattedResults = JDBCUtil.format(results);
        }
        finally
        {
            JDBCUtil.releaseConnection(conn); 
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
    public static Object[][] executeQueryArray(String sessionName, String sql) 
        throws SQLException
    {
        Object[][] data = null;
        Connection conn = null;
        PreparedStatement stmt = null;

        try
        {
            conn = getConnection(sessionName);
            stmt = conn.prepareStatement(sql);
            ResultSet results = stmt.executeQuery();
            data = JDBCUtil.toArray(results);
        }
        finally
        {
            JDBCUtil.close(stmt);
            JDBCUtil.releaseConnection(conn);
        }

        return data;
    }


    /**
     * Drops table w/o any complaints.
     * 
     * @param table Name of table to drop.
     */
    public static void dropTable(String sessionName, String table)
    {
        if (table == null)
            return;
            
        try
        {
            executeUpdate(sessionName, "drop table " + table);
        }
        catch (SQLException e)
        {
            ; // Quiet please
        }
    }

    
    /**
     * Deregisters the current driver. Must call init() to use again.
     * 
     * @throws SQLException on SQL error.
     */
    public static void shutdown(String sessionName) throws SQLException 
    {
        Session session = getSession(sessionName);
        
        if (session == null)
            logger_.warn("Shutting down non-existant session " + sessionName);
        else 
        {
            if (session.isPooled()) 
            {
                PoolingDriver pd = (PoolingDriver) session.getDriver();
                
                try 
                {
                    pd.closePool(CONN_POOL_NAME + sessionName);
                }
                catch (Exception e) 
                {
                    logger_.error(e);
                    throw new SQLException(e.getMessage());
                }
            }
        
            session.setDriver(null);
            session.setConnProps(null);
            sessionMap_.remove(sessionName);
        }
    }
    
    //--------------------------------------------------------------------------
    // SessionInfo
    //--------------------------------------------------------------------------
    
    static class Session
    {
        private String name_;
        private Properties connProps_;
        private Driver driver_;
        private boolean pooled_;
     
        
        /**
         * Creates a SessionInfo.
         * 
         * @param name
         * @param connProps
         * @param driver
         * @param pooled
         */
        public Session(String name, Properties connProps, Driver driver,
            boolean pooled)
        {
            name_ = name;
            connProps_ = connProps;
            driver_ = driver;
            pooled_ = pooled;
        }
        
        /**
         * Returns the connProps.
         * 
         * @return Properties
         */
        public Properties getConnProps()
        {
            return connProps_;
        }
        /**
         * Sets the connProps.
         * 
         * @param connProps The connProps to set.
         */
        public void setConnProps(Properties connProps)
        {
            connProps_ = connProps;
        }
        /**
         * Returns the driver.
         * 
         * @return Driver
         */
        public Driver getDriver()
        {
            return driver_;
        }
        /**
         * Sets the driver.
         * 
         * @param driver The driver to set.
         */
        public void setDriver(Driver driver)
        {
            driver_ = driver;
        }
        
        
        /**
         * Returns the pooled.
         * 
         * @return boolean
         */
        public boolean isPooled()
        {
            return pooled_;
        }
        /**
         * Sets the pooled.
         * 
         * @param pooled The pooled to set.
         */
        public void setPooled(boolean pooled)
        {
            pooled_ = pooled;
        }
        /**
         * Returns the name.
         * 
         * @return String
         */
        public String getName()
        {
            return name_;
        }
        /**
         * Sets the name.
         * 
         * @param name The name to set.
         */
        public void setName(String name)
        {
            name_ = name;
        }
    }
    

}