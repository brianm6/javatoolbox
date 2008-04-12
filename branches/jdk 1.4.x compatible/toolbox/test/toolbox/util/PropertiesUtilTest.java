package toolbox.util;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.Properties;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit Test for {@link toolbox.util.PropertiesUtil}.
 */
public class PropertiesUtilTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(PropertiesUtilTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
            
    public static void main(String[] args)
    {
        TestRunner.run(PropertiesUtilTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests getBoolean()
     */
    public void testGetBoolean()
    {
        logger_.info("Running testGetBoolean...");
        
        Properties props = new Properties();
        
        // lower case
        props.setProperty("1", "true");
        props.setProperty("2", "false");
        
        // upper case
        props.setProperty("3", "TRUE");
        props.setProperty("4", "FALSE");
        
        // mixed case
        props.setProperty("5", "True");
        props.setProperty("6", "False");
        
        // defaults
        props.setProperty("7", "test_default_true");
        props.setProperty("8", "test_default_false");        
        
        // extra spaces
        props.setProperty("9", " True ");
        props.setProperty("10", " False ");
        
        assertTrue(PropertiesUtil.getBoolean(props,  "1", false));
        assertTrue(!PropertiesUtil.getBoolean(props, "2", false));
        assertTrue(PropertiesUtil.getBoolean(props,  "3", false));
        assertTrue(!PropertiesUtil.getBoolean(props, "4", false));
        assertTrue(PropertiesUtil.getBoolean(props,  "5", false));
        assertTrue(!PropertiesUtil.getBoolean(props, "6", false));
        assertTrue(PropertiesUtil.getBoolean(props,  "7", true));
        assertTrue(!PropertiesUtil.getBoolean(props, "8", false));
        assertTrue(PropertiesUtil.getBoolean(props,  "9", true));
        assertTrue(!PropertiesUtil.getBoolean(props, "10", false));
        assertTrue(PropertiesUtil.getBoolean(props,  "bad key", true));
    }

    
    /**
     * Tests setBoolean()
     */
    public void testSetBoolean()
    {
        logger_.info("Running testSetBoolean...");
        
        Properties props = new Properties();
        
        PropertiesUtil.setBoolean(props, "1", true);
        PropertiesUtil.setBoolean(props, "2", false);

        assertTrue(new Boolean(props.getProperty("1")).booleanValue());
        assertTrue(!new Boolean(props.getProperty("2")).booleanValue());
    }
    
    
    /**
     * Tests getInteger()
     */
    public void testGetInteger()
    {
        logger_.info("Running testGetInteger...");
        
        Properties props = new Properties();
        
        props.setProperty("1", "100");
        props.setProperty("2", "-100");
        props.setProperty("3", "0");
        props.setProperty("4", "56.34");
        props.setProperty("5", "");
        
        assertEquals(100, PropertiesUtil.getInteger(props, "1", -1));
        assertEquals(-100, PropertiesUtil.getInteger(props, "2", -1));
        assertEquals(0, PropertiesUtil.getInteger(props, "3", -1));
        assertEquals(-1, PropertiesUtil.getInteger(props, "4", -1));
        assertEquals(-1, PropertiesUtil.getInteger(props, "5", -1));
        assertEquals(-1, PropertiesUtil.getInteger(props, "doesnt exist", -1));
    }   

    
    /**
     * Tests getLong()
     */
    public void testGetLong()
    {
        logger_.info("Running testGetLong...");
        
        Properties props = new Properties();
        
        props.setProperty("1", "1234567890");
        props.setProperty("2", "-1234567890");
        props.setProperty("3", "0");
        props.setProperty("4", "56.34");
        props.setProperty("5", "");
        
        assertEquals(1234567890, PropertiesUtil.getLong(props, "1", -1));
        assertEquals(-1234567890, PropertiesUtil.getLong(props, "2", -1));
        assertEquals(0   , PropertiesUtil.getLong(props, "3", -1));
        assertEquals(-1  , PropertiesUtil.getLong(props, "4", -1));
        assertEquals(-1  , PropertiesUtil.getLong(props, "5", -1));
        assertEquals(-1  , PropertiesUtil.getLong(props, "doesnt exist", -1));
    }   
    
    
    /**
     * Tests setInteger()
     */
    public void testSetInteger()
    {
        logger_.info("Running testSetInteger...");
        
        Properties props = new Properties();
        
        PropertiesUtil.setInteger(props, "0", 0);
        PropertiesUtil.setInteger(props, "1", 100);
        PropertiesUtil.setInteger(props, "2", -100);
        PropertiesUtil.setInteger(props, "3", Integer.MAX_VALUE);
        PropertiesUtil.setInteger(props, "4", Integer.MIN_VALUE);

        assertEquals(0, new Integer(props.getProperty("0")).intValue());
        assertEquals(100, new Integer(props.getProperty("1")).intValue());
        assertEquals(-100, new Integer(props.getProperty("2")).intValue());
        
        assertEquals(Integer.MAX_VALUE, 
            new Integer(props.getProperty("3")).intValue());
            
        assertEquals(Integer.MIN_VALUE, 
            new Integer(props.getProperty("4")).intValue());
    }
    
    
    public void testToString_Zero() throws IOException 
    {
		logger_.info("Running testToString_Zero...");

		Properties p = new Properties();
		String s = PropertiesUtil.toString(p);
		logger_.debug(s);
		assertEquals("", s);
	}

	public void testToString_One() throws IOException 
	{
		logger_.info("Running testToString_One...");

		// Setup
		Properties p = new Properties();
		p.setProperty("alpha.centauri", "rhombus");

		// Test
		String s = PropertiesUtil.toString(p);

		// Verify
		logger_.debug(s);
		LineNumberReader lnr = new LineNumberReader(new StringReader(s));
		assertTrue(lnr.readLine().startsWith("alpha.centauri"));
		assertNull(lnr.readLine());
	}

	public void testToString_Many() throws IOException 
	{
	    logger_.info("Running testToString_Many...");

        // Setup
        Properties p = new Properties();
        p.setProperty("alpha.centauri", "rhombus");
        p.setProperty("alpha.beta", "cube");
        p.setProperty("zulu.tango", "trapezoid");
        p.setProperty("foxtrot.charlie", "torus");

        // Test
        String s = PropertiesUtil.toString(p);

        // Verify - props sorted
        logger_.debug(s);
        LineNumberReader lnr = new LineNumberReader(new StringReader(s));
        assertTrue(lnr.readLine().startsWith("alpha.beta"));
        assertTrue(lnr.readLine().startsWith("alpha.centauri"));
        assertTrue(lnr.readLine().startsWith("foxtrot.charlie"));
        assertTrue(lnr.readLine().startsWith("zulu.tango"));
        assertNull(lnr.readLine());
    }
}