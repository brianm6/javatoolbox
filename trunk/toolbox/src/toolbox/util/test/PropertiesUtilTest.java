package toolbox.util.test;

import java.util.Properties;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.PropertiesUtil;

/**
 * Unit Test for PropertiesUtil
 */
public class PropertiesUtilTest extends TestCase
{
    /** Logger **/
    private static final Logger logger_ =
        Logger.getLogger(PropertiesUtilTest.class);
        
    /**
     * Entrypoint
     */
    public static void main(String[] args)
    {
        TestRunner.run(PropertiesUtilTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for PropertiesUtilTest.
     * 
     * @param arg0
     */
    public PropertiesUtilTest(String arg0)
    {
        super(arg0);
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
        assertEquals(Integer.MAX_VALUE, new Integer(props.getProperty("3")).intValue());
        assertEquals(Integer.MIN_VALUE, new Integer(props.getProperty("4")).intValue());
    }
}
