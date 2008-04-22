package toolbox.util.db.oracle;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.JDBCSession;

/**
 * Unit test for {@link toolbox.util.db.oracle.OracleUtil}.
 */
public class OracleUtilTest extends TestCase 
{
    private static final Logger logger_ = Logger.getLogger(OracleUtilTest.class);

    private boolean oracleAvailable;
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Name of the unique database session used by this test.
     */        
    private static final String SESSION = "OracleUtilTest";

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args) 
    {
        TestRunner.run(OracleUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Overrides TestCase
    //--------------------------------------------------------------------------
    
    /**
     * Initializes the database session.
     * 
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception 
    {
    	try {
	        JDBCSession.init(
	            SESSION,
	            "oracle.jdbc.driver.OracleDriver",
	            "jdbc:oracle:thin:fixme!!!",
	            "user_fixme",
	            "password_fixme");
	        oracleAvailable = true;
    	}
    	catch (ClassNotFoundException cnfe) {
    		oracleAvailable = false;
    	}
    }


    /**
     * Destroys the database session.
     * 
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception 
    {
    	if (oracleAvailable)
    		JDBCSession.shutdown(SESSION);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testSetConstraintsEnabled() 
    {
    	if (!oracleAvailable)
    		return;
    }


    /**
     * Tests retrieves a sequence by its name.
     */
    public void testGetSequence() throws Exception 
    {
    	if (!oracleAvailable)
    		return;
    	
        logger_.info("Running testGetSequence...");
        
        String sequenceName = "TEST_GET_SEQUENCE_" + RandomUtils.nextInt();
        
        String createSequence = 
              "CREATE SEQUENCE " + sequenceName 
            + "    MINVALUE 1"
            + "    MAXVALUE 999999"
            + "    START WITH 1"
            + "    INCREMENT BY 1"
            + "    CACHE 20";            
        
        JDBCSession.executeUpdate(SESSION, createSequence);
        
        try 
        {
            OracleSequence sequence = 
                OracleUtil.getSequence(SESSION, sequenceName);
            
            assertEquals(1, sequence.getMinValue());
            assertEquals(999999, sequence.getMaxValue());
            assertEquals(sequenceName, sequence.getName());
            assertEquals(1, sequence.getLastNumber());
            assertEquals(20, sequence.getCacheSize());
        }
        finally 
        {
            JDBCSession.executeUpdate(SESSION, "DROP SEQUENCE " + sequenceName);
        }
    }


    /**
     * Tests retrieving all sequences visible by the user associated with the
     * current database sesssion.
     */
    public void testGetSequences() throws Exception 
    {
    	if (!oracleAvailable)
    		return;

        logger_.info("Running testGetSequences...");
        
        String sequenceName = "TEST_GET_SEQUENCES_" + RandomUtils.nextInt();
        
        String createSequence = 
              "CREATE SEQUENCE " + sequenceName 
            + "    MINVALUE 1"
            + "    MAXVALUE 999999"
            + "    START WITH 1"
            + "    INCREMENT BY 1"
            + "    CACHE 20";            
        
        JDBCSession.executeUpdate(SESSION, createSequence);
        
        try 
        {
            List sequences = OracleUtil.getSequences(SESSION);
            boolean found = false;
            
            for (Iterator iter = sequences.iterator(); iter.hasNext();) 
            {
                OracleSequence seq = (OracleSequence) iter.next();
                
                if (seq.getName().equals(sequenceName))
                    found = true;
                    
                logger_.debug(seq.toString());
            }
            
            assertTrue("Sequence not found in list", found);
        }
        finally 
        {
            JDBCSession.executeUpdate(SESSION, "DROP SEQUENCE " + sequenceName);
        }
    }


    /**
     * Tests that a sequence can be updated to a new value higher than the
     * current value.
     */
    public void testSetSequenceValueHigher() throws Exception 
    {
    	if (!oracleAvailable)
    		return;

        logger_.info("Running testSetSequenceValue...");
        
        String sequenceName = "TSSV" + RandomUtils.nextInt();
        
        String createSequenceSQL = 
              "CREATE SEQUENCE " + sequenceName 
            + "    MINVALUE 1"
            + "    MAXVALUE 999999"
            + "    START WITH 1"
            + "    INCREMENT BY 1"
            + "    CACHE 20";            
        
        int newValue = 500;
        
        JDBCSession.executeUpdate(SESSION, createSequenceSQL);

        List sequences = OracleUtil.getSequences(SESSION);
        logger_.debug(ArrayUtil.toString(sequences.toArray(), true));
        
        // Make sure we can retrieve newly create sequence w/o problems...
        OracleUtil.getSequence(SESSION, sequenceName);
        
        try 
        {
            OracleUtil.setSequenceValue(SESSION, sequenceName, newValue);
            
            OracleSequence updated = 
                OracleUtil.getSequence(SESSION, sequenceName);
                
            logger_.debug("Updated sequence: " + updated);                
            
            assertEquals(updated.getLastNumber(), newValue);
        }
        finally 
        {
            JDBCSession.executeUpdate(SESSION, "DROP SEQUENCE " + sequenceName);
        }
    }
    
    
    /**
     * Tests that a sequence can be updated to a new value lower than the
     * current value.
     */
    public void testSetSequenceValueLower() throws Exception 
    {
    	if (!oracleAvailable)
    		return;

        logger_.info("Running testSetSequenceValueLower...");
        
        String sequenceName = "TSSVL" + RandomUtils.nextInt();
        
        String createSequenceSQL = 
              "CREATE SEQUENCE " + sequenceName 
            + "    MINVALUE 1"
            + "    MAXVALUE 999999"
            + "    START WITH 999"
            + "    INCREMENT BY 1"
            + "    CACHE 20";            
        
        int newValue = 50;
        
        JDBCSession.executeUpdate(SESSION, createSequenceSQL);

        List sequences = OracleUtil.getSequences(SESSION);
        logger_.debug(ArrayUtil.toString(sequences.toArray(), true));
        
        // Make sure we can retrieve newly create sequence w/o problems...
        OracleUtil.getSequence(SESSION, sequenceName);
        
        try 
        {
            OracleUtil.setSequenceValue(SESSION, sequenceName, newValue);
            
            OracleSequence updated = 
                OracleUtil.getSequence(SESSION, sequenceName);
                
            logger_.debug("Updated sequence: " + updated);                
            
            assertEquals(newValue, updated.getLastNumber());
        }
        finally 
        {
            JDBCSession.executeUpdate(SESSION, "DROP SEQUENCE " + sequenceName);
        }
    }    
}