package toolbox.util.io.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.io.StringOutputStream;

/**
 * Unit test for StringOutputStream
 */
public class StringOutputStreamTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(StringOutputStreamTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Entrypoint   
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(StringOutputStreamTest.class);
    }

    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
       
    /**
     * Tests the constructors
     */
    public void testConstructors()
    {
        logger_.info("Running testConstructors...");
        
        StringOutputStream sos = new StringOutputStream();
        assertNotNull(sos);
    }
        
    /**
     * Tests the write() method
     * 
     * @throws Exception on error
     */
    public void testWrite() throws Exception
    {
        logger_.info("Running testWrite...");
        
        StringOutputStream sos = new StringOutputStream();
        String testString = "holy moly!";
        sos.write(testString.getBytes());
        
        assertEquals("strings don't match", testString, sos.toString());
    }
    
    /**
     * Tests the close() method
     * 
     * @throws Exception on error
     */
    public void testClose() throws Exception
    {
        logger_.info("Running testClose...");
        
        StringOutputStream sos = new StringOutputStream();
        sos.close();
        StringOutputStream sos2 = new StringOutputStream();
        sos2.write("hello".getBytes());
        sos2.close();
    }
}