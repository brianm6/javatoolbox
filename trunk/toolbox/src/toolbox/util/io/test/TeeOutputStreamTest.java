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
     * 
     * @param  args None
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
     * @param arg0  Name
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
     * 
     * @throws Exception on error
     */
    public void testWrite() throws Exception
    {
        logger_.info("Running testWrite...");
        
        String testString = "hello";
        
        StringOutputStream sos1 = new StringOutputStream();
        StringOutputStream sos2 = new StringOutputStream();
        
        TeeOutputStream tos = new TeeOutputStream(sos1, sos2);
        
        tos.write(testString.getBytes());
        tos.write(100);
        
        assertEquals(testString + (char)100, sos1.getBuffer().toString());
        assertEquals(testString + (char)100, sos2.getBuffer().toString());
        
        tos.flush();
        tos.close();
    }
}