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
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for StringOutputStreamTest
     * 
     * @param  arg0  Name
     */
    public StringOutputStreamTest(String arg0)
    {
        super(arg0);
    }

    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
        
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
}


