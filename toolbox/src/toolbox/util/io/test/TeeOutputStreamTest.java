package toolbox.util.io.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.io.StringOutputStream;
import toolbox.util.io.TeeOutputStream;

/**
 * Unit test for TeeOutputStream
 */
public class TeeOutputStreamTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(TeeOutputStreamTest.class);
        
    /**
     * Entrypoint
     */
    public static void main(String[] args)
    {
        TestRunner.run(TeeOutputStreamTest.class);
    }
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
        
    /**
     * Constructor for TeeOutputStreamTest.
     * 
     * @param arg0
     */
    public TeeOutputStreamTest(String arg0)
    {
        super(arg0);
    }
    
    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests write()
     */
    public void testWrite() throws Exception
    {
        logger_.info("Running testWrite...");
        
        String testString = "hello";
        
        StringOutputStream sos1 = new StringOutputStream();
        StringOutputStream sos2 = new StringOutputStream();
        
        TeeOutputStream tos = new TeeOutputStream(sos1, sos2);
        
        tos.write(testString.getBytes());
        
        assertEquals(testString, sos1.getBuffer().toString());
        assertEquals(testString, sos2.getBuffer().toString());
    }
}
