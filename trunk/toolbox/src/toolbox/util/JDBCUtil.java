package toolbox.util;

import java.sql.Connection;
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
 * JDBC Utility class
 */
public class JDBCUtil
{
    /** Logger **/
    public static final Logger logger_ =
        Logger.getLogger(JDBCUtil.class);

    /** JDBC connection properties **/
    private static Properties connProps_;
        
        
    /**
     * Constructor for JDBCUtil.
     */
    private JDBCUtil()
    {
    }

    
    /**
     * Formats a results set in a table like manner
     * 
     * @param   rs  ResultSet to format
     * @return  Contents of result set formatted in a table like format
     * @throws  SQLException on sql error
     */
    public static String format(ResultSet rs) throws SQLException 
    {
        ResultSetMetaData m = rs.getMetaData();
    
        int    nCols    = m.getColumnCount();
        String header[] = new String[nCols];
        int    max[]    = new int[nCols];                
        
        // Column headers
        for (int i = 1; i <= nCols; i++) 
        {
            String label = m.getColumnLabel(i);
            
            if (label == null)
                label = "[NULL]";
                
            int colLen = label.length();
            max[i-1] = colLen;
            header[i-1] = label;
        }
        
        int numRows = 0;
        long time = System.currentTimeMillis();
        List rows = new ArrayList();
        rows.add(header);

        // Rows        
        while (rs.next()) 
        {
            ++numRows;

            String row[] = new String[nCols];
             
            for (int i = 1; i <= nCols; i++) 
            {
                Object obj = null;
                
                try 
                {
                    if (m.getColumnType(i) == Types.LONGVARBINARY)
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
                    value = obj.toString();
                else
                    value= "[NULL]";
                
                // Add extra spaces at the end    
                max[i-1] = Math.max(value.length(), max[i-1]) + 2;
                row[i-1] = value;
            }

            rows.add(row);
        }

        time = System.currentTimeMillis() - time;

        String[] dashes = new String[nCols];
        
        for(int i=0; i<nCols; i++)
            dashes[i] = StringUtil.repeat("-", max[i]);

        rows.add(1, dashes);

        StringBuffer sb = new StringBuffer();            
        
        for (Iterator i = rows.iterator(); i.hasNext(); ) 
        {
            String[] row = (String[])i.next();

            for(int j=0; j<row.length; j++) 
                sb.append(StringUtil.left(row[j], max[j]));
            
            sb.append("\n");
        }

        sb.append("\n");
        sb.append(numRows + " rows/" + time + "ms\n");
        
        return sb.toString();
    }

 
    /**
     * Returns a connection to the TURBO database
     * 
     * @return  Connection ready for use
     * @throws  SQLException on SQL error
     */
    public static Connection getConnection() throws SQLException
    {
        if (connProps_ == null)
            throw new IllegalStateException(
                "Must call init() first to set the JDBC configuration.");
                
        return DriverManager.getConnection(
            connProps_.getProperty("url"), connProps_);
    }


    /**
     * Releases a connection. Supresses any problems 
     * 
     * @param  connection  Connection to release
     */
    private static void releaseConnection(Connection connection) 
    {
        if (connection != null)
        {
            try 
            { 
                connection.close();
            } 
            catch(SQLException e) 
            {
                // Ignore
            }
        }
    }

 
    /**
     * Executes a SQL INSERT, UPDATE, or DELETE statement
     * 
     * @param   sql     Insert/update/delete sql statement to execute
     * @return  Number of rows affected by the execution of the sql statement
     * @throws  SQLException on any errors
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
     * @param   sql  Select statement to execute
     * @return  Formatted contents of the result set
     * @throws  SQLException on any SQL error
     */
    public static String executeAndFormatQuery(String sql) throws SQLException
    {
        String formattedResults = null;
        Connection conn = null;
        
        try
        {
            conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet results = stmt.executeQuery();
            formattedResults = JDBCUtil.format(results);
        }
        finally
        {
            releaseConnection(conn); 
        }
        
        return formattedResults;
    } 


    /**
     * Initialzies the JDBC properties. Must be called before any of the other
     * methods are use
     * 
     * @param  driver   JDBC driver to use
     * @param  url      URL to database resource
     * @param  user     Username used for authentication
     * @param  password Password used for authentication
     * @throws ClassNotFoundException if the JDBC driver is not found
     */    
    public static void init(
        String driver, 
        String url, 
        String user, 
        String password) throws ClassNotFoundException
    {
        // Will force DriverManager.register() to get called
        Class.forName(driver);
        connProps_ = new Properties();
        connProps_.put("user", user);
        connProps_.put("password", password);
        connProps_.put("url", url);
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
     * @param         rs    Result set
     * @return        The size of the result set
     * @exception     SQLException if an SQL error occurs
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
}