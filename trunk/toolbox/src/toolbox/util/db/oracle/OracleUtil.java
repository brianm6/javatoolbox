package toolbox.util.db.oracle;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import toolbox.util.JDBCSession;

/**
 * Oracle specific database utility methods. Includes methods to manipulate
 * constraints and sequences.
 */
public class OracleUtil 
{
    private static final Logger logger_ = Logger.getLogger(OracleUtil.class);
    
    //--------------------------------------------------------------------------
    // SQL Statements
    //--------------------------------------------------------------------------
    
    /**
     * Queries all foreign key constraints.
     */
    private static final String SQL_QUERY_CONSTRAINTS = 
          "select constraint_name,          "
        + "       table_name,               "
        + "       status                    "
        + "  from user_constraints          "
        + " where r_constraint_name in      "         
        + "       (select constraint_name   "
        + "          from user_constraints) ";

    /**
     * Disables a single foreign key constraint.
     */
    private static final String SQL_DISABLE_FOREIGN_KEY = 
        "ALTER TABLE {0} DISABLE CONSTRAINT {1}";

    /**
     * Enables a single foreign key constraint.
     */
    private static final String SQL_ENABLE_FOREIGN_KEY = 
        "ALTER TABLE {0} ENABLE CONSTRAINT {1}";
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Enables/disables all the foreign key constraints in a given database.
     * 
     * @param session Name of database session obtained via 
     *        <code>JDBCSession.init()</code>.
     * @param enabled True to enable all constraints, false otherwise.
     * @throws SQLException on database error.
     */
    public static void setConstraintsEnabled(String session, boolean enabled)
        throws SQLException
    {
        Object[][] results = 
            JDBCSession.executeQueryArray(session, SQL_QUERY_CONSTRAINTS);
        
        for (int i = 0; i < results.length; i++) {
            
            Object[] row = results[i];
            String constraint = row[0].toString();
            String table = row[1].toString();
            String status = row[2].toString();
            
            String sql = 
                enabled ? SQL_ENABLE_FOREIGN_KEY : SQL_DISABLE_FOREIGN_KEY;
                
            sql = MessageFormat.format(sql, new String[] {table, constraint});
            
            int j = JDBCSession.executeUpdate(session, sql);
            logger_.debug("Executed: " + j + " - " + sql);
        }
    }


    /**
     * Returns the given sequence from the database.
     * 
     * @param session Name of database session obtained via 
     *        <code>JDBCSession.init()</code>.
     * @param name Name of the sequence to retrieve.
     * @return OracleSequence
     * @throws IllegalArgumentException if the sequence does not exist.
     * @throws SQLException on database error.
     */    
    public static OracleSequence getSequence(String session, String name)
        throws SQLException, IllegalArgumentException 
    {
        Object[][] results = 
            JDBCSession.executeQueryArray(session,
                  "select sequence_owner, "
                + "       sequence_name,  "
                + "       min_value,      "
                + "       max_value,      "
                + "       increment_by,   "
                + "       cycle_flag,     "
                + "       order_flag,     "
                + "       cache_size,     "
                + "       last_number     "
                + "  from all_sequences   "
                + " where sequence_name = '" + name + "'");

        if (results.length == 0)
            throw new IllegalArgumentException(
                "Sequence " + name + " not found.");
                
        if (results.length > 1)
            throw new IllegalArgumentException(
                "Multiple sequences returned: " + results.length);

        Object[] row = results[0];
        
        //logger.debug(ArrayUtil.toString(row, true));
        
        OracleSequence sequence = new OracleSequence();
        int i = 0;
        
        sequence.setOwner(row[i++].toString());
        sequence.setName(row[i++].toString());
        sequence.setMinValue(Integer.parseInt(row[i++].toString()));
        sequence.setMaxValue(Integer.parseInt(row[i++].toString()));
        sequence.setIncrementBy(Integer.parseInt(row[i++].toString()));
        i++; // TODO: sequence.setCycleFlag(); 
        i++; // TODO: sequence.setOrderFlag();
        sequence.setCacheSize(Integer.parseInt(row[i++].toString()));
        sequence.setLastNumber(Integer.parseInt(row[i++].toString()));
        return sequence;
    }
    
    
    /**
     * Returns all sequences accessible by the given authenticated session.
     * 
     * @param session Name of database session obtained via 
     *        <code>JDBCSession.init()</code>.
     * @return List<OracleSequence>
     * @throws SQLException on database error.
     */
    public static List getSequences(String session) throws SQLException 
    {
        Object[][] results = JDBCSession.executeQueryArray(
            session, "select sequence_name from all_sequences");

        List sequences = new ArrayList(results.length);

        for (int i = 0; i < results.length; i++) {
            Object[] row = results[i];
            String name = row[0].toString();
            sequences.add(getSequence(session, name));
        }
                
        return sequences;
    }
    
    
    /**
     * Sets the last value of the given sequence.
     * 
     * @param session Name of database session obtained via 
     *        <code>JDBCSession.init()</code>.
     * @param sequenceName Name of the sequence to update.
     * @param value Value to update the sequence to.
     * @throws SQLException on database error.
     */
    public static void setSequenceValue(
        String session, 
        String sequenceName, 
        int newValue) throws SQLException 
    {
        OracleSequence sequence = getSequence(session, sequenceName);
        
        // Nothing to do
        if (sequence.getLastNumber() == newValue)
            return;
        
        int diff = newValue - sequence.getLastNumber();     
        
        if (diff > 0) 
        {
            JDBCSession.executeUpdate(session,
                "alter sequence " + sequenceName + " increment by " + diff);
                
            JDBCSession.executeQuery(session,
                "select " + sequenceName + ".nextval from dual");

            JDBCSession.executeUpdate(session,
                "alter sequence " + sequenceName + " increment by " 
                + sequence.getIncrementBy());
        }
        else 
        {
            JDBCSession.executeUpdate(session,
                "alter sequence " + sequenceName + " increment by " + (diff));
                
            JDBCSession.executeQuery(session,
                "select " + sequenceName + ".nextval from dual");

            JDBCSession.executeUpdate(session,
                "alter sequence " + sequenceName + " increment by " 
                + sequence.getIncrementBy());
        }
        
        OracleSequence updatedSequence = getSequence(session, sequenceName);
        
        logger_.debug("Sequence old value: " + sequence.getLastNumber());
        logger_.debug("Sequence new value: " + updatedSequence.getLastNumber());
    }
}