package toolbox.util.test;

import java.io.StringReader;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.StreamUtil;
import toolbox.util.io.StringInputStream;

/**
 * Unit test for StreamUtil
 */
public class StreamUtilTest extends TestCase
{
    /** Logger */
    private static final Logger logger_ =
        Logger.getLogger(StreamUtilTest.class);
        
    /**
     * Entrypoint
     * 
     * @param  args  None
     */
    public static void main(String[] args)
    {
        TestRunner.run(StreamUtilTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for StreamUtilTest
     * 
     * @param  arg0  Name
     */
    public StreamUtilTest(String arg0)
    {
        super(arg0);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
        
    /**
     * Tests the toString() method
     * 
     * @throws  Exception on error
     */
    public void testToString() throws Exception
    {
        logger_.info("Running testToString...");
        
        String testString = "this is a test string for toString()";
        
        StringInputStream sis = new StringInputStream(testString);
        String toString = StreamUtil.asString(sis);
        
        assertEquals("toString() does not match original string", 
            testString, toString);
    }
    
    /**
     * Tests empty()
     * 
     * @throws  Exception on error
     */
    public void testEmpty() throws Exception
    {
        logger_.info("Running testEmpty...");
                
        // Create stream and populate
        String contents = "testing";
        StringInputStream sis = new StringInputStream(contents);
        
        // Verify available
        assertEquals("stream should have contents", contents.length(),
            sis.available());
        
        // Empty the stream
        StreamUtil.empty(sis);
     
        // Verify empty 
        assertEquals("available() should be 0", 0, sis.available());
        assertEquals("read() should be -1", -1, sis.read());
    }
    
    /**
     * Tests readExactly() for a stream
     * 
     * @throws  Exception on error
     */
    public void testReadExactlyStream() throws Exception
    {
        logger_.info("Running testReadExactlyStream...");
        
        // create stream and populate
        String contents = "testing";
        StringInputStream sis = new StringInputStream(contents);
        
        // read in 2 passes, 4 bytes first then 3 bytes next 
        byte[] pass1 = StreamUtil.readExactly(sis, 4);
        byte[] pass2 = StreamUtil.readExactly(sis, 3); 
     
        // verify
        assertEquals("first pass does not match", "test", new String(pass1));
        assertEquals("second pass does not match", "ing", new String(pass2));
    }
    
    /**
     * Tests readExactly() for a reader
     * 
     * @throws  Exception on error
     */
    public void testReadExactlyReader() throws Exception
    {
        logger_.info("Running testReadExactlyReader...");
        
        // create stream and populate
        String contents = "testing";
        StringReader sr = new StringReader(contents);
        
        // read in 2 passes, 4 bytes first then 3 bytes next
        String pass1 = StreamUtil.readExactly(sr, 4);
        String pass2 = StreamUtil.readExactly(sr, 3); 
     
        // verify
        assertEquals("first pass does not match", "test", pass1);
        assertEquals("second pass does not match", "ing", pass2);
    }
}