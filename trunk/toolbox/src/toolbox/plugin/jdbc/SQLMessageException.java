package toolbox.plugin.jdbc;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Tries to make up for the annoying fact that a SQL exception does not
 * include the offending sql statement.
 */
public class SQLMessageException extends SQLException
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * SQL statement that caused this sql exception.
     */
    private String sqlStatement_;
    
    /**
     * Original sql exception that this exception is wrapping and using as 
     * a delegate.
     */
    private SQLException delegate_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SQLMessageException.
     * 
     * @param ex
     * @param sqlStatement
     */
    public SQLMessageException(SQLException ex, String sqlStatement)
    {
        delegate_ = ex;
        setStatement(sqlStatement);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the sql statement.
     * 
     * @return String
     */
    public String getStatement()
    {
        return sqlStatement_;
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Sets the sql statement.
     * 
     * @param stmt SQL statement.
     */
    protected void setStatement(String stmt)
    {
        sqlStatement_ = stmt;
    }
    
    //--------------------------------------------------------------------------
    // Delegate to the real SQLException
    //--------------------------------------------------------------------------
    
    /**
     * @see java.lang.Throwable#fillInStackTrace()
     */
    public synchronized Throwable fillInStackTrace()
    {
        if (delegate_ != null)
            return delegate_.fillInStackTrace();
        else
            return super.fillInStackTrace();
    }
    
    
    /**
     * @see java.lang.Throwable#getCause()
     */
    public Throwable getCause()
    {
        return delegate_.getCause();
    }
    
    
    /**
     * @see java.sql.SQLException#getErrorCode()
     */
    public int getErrorCode()
    {
        return delegate_.getErrorCode();
    }
    
    
    /**
     * @see java.lang.Throwable#getLocalizedMessage()
     */
    public String getLocalizedMessage()
    {
        return delegate_.getLocalizedMessage();
    }
    
    
    /**
     * @see java.lang.Throwable#getMessage()
     */
    public String getMessage()
    {
        return delegate_.getMessage() + "\n" + getStatement();
    }
    
    
    /**
     * @see java.sql.SQLException#getNextException()
     */
    public SQLException getNextException()
    {
        return delegate_.getNextException();
    }
    
    
    /**
     * @see java.sql.SQLException#getSQLState()
     */
    public String getSQLState()
    {
        return delegate_.getSQLState();
    }
    
    
    /**
     * @see java.lang.Throwable#getStackTrace()
     */
    public StackTraceElement[] getStackTrace()
    {
        return delegate_.getStackTrace();
    }
    
    
    /**
     * @see java.lang.Throwable#initCause(java.lang.Throwable)
     */
    public synchronized Throwable initCause(Throwable cause)
    {
        return delegate_.initCause(cause);
    }
    
    
    /**
     * @see java.lang.Throwable#printStackTrace()
     */
    public void printStackTrace()
    {
        delegate_.printStackTrace();
    }
    
    
    /**
     * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
     */
    public void printStackTrace(PrintStream s)
    {
        delegate_.printStackTrace(s);
    }
    
    
    /**
     * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
     */
    public void printStackTrace(PrintWriter s)
    {
        delegate_.printStackTrace(s);
    }
    
    
    /**
     * @see java.sql.SQLException#setNextException(java.sql.SQLException)
     */
    public synchronized void setNextException(SQLException ex)
    {
        delegate_.setNextException(ex);
    }
    
    
    /**
     * @see java.lang.Throwable#setStackTrace(java.lang.StackTraceElement[])
     */
    public void setStackTrace(StackTraceElement[] stackTrace)
    {
        delegate_.setStackTrace(stackTrace);
    }
    
    
    /**
     * @see java.lang.Throwable#toString()
     */
    public String toString()
    {
        return delegate_.toString();
    }
    
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        return delegate_.equals(obj);
    }
 
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return delegate_.hashCode();
    }
}