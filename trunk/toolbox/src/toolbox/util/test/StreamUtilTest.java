package toolbox.util.test;

import java.io.StringReader;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import toolbox.util.StreamUtil;
import toolbox.util.io.StringInputStream;

/**
 * Unit test for StreamUtil
 */
public class StreamUtilTest extends TestCase
{
    /**
     * Entrypoint
     */
    public static void main(String[] args)
    {
        TestRunner.run(StreamUtilTest.class);
    }
    
    /**
     * Constructor for StreamUtilTest
     */
    public StreamUtilTest(String arg0)
    {
        super(arg0);
    }
    
    /**
     * Tests the toString() method
     */
    public void testToString() throws Exception
    {
        String testString = "this is a test string for toString()";
        
        StringInputStream sis = new StringInputStream(testString);
        String toString = StreamUtil.asString(sis);
        
        assertEquals("toString() does not match original string", 
            testString, toString);
    }
    
    /**
     * Tests empty()
     */
    public void testEmpty() throws Exception
    {
        /* create stream and populate */
        String contents = "testing";
        StringInputStream sis = new StringInputStream(contents);
        
        /* verify available */
        assertEquals("stream should have contents", contents.length(),
            sis.available());
        
        /* empty the stream */
        StreamUtil.empty(sis);
     
        /* verify empty */
        assertEquals("available() should be 0", 0, sis.available());
        assertEquals("read() should be -1", -1, sis.read());
    }
    
    /**
     * Tests readExactly() for a stream
     */
    public void testReadExactlyStream() throws Exception
    {
        /* create stream and populate */
        String contents = "testing";
        StringInputStream sis = new StringInputStream(contents);
        
        /* read in 2 passes, 4 bytes first then 3 bytes next */
        byte[] pass1 = StreamUtil.readExactly(sis, 4);
        byte[] pass2 = StreamUtil.readExactly(sis, 3); 
     
        /* verify */
        assertEquals("first pass does not match", "test", new String(pass1));
        assertEquals("second pass does not match", "ing", new String(pass2));
    }
    
    /**
     * Tests readExactly() for a reader
     */
    public void testReadExactlyReader() throws Exception
    {
        /* create stream and populate */
        String contents = "testing";
        StringReader sr = new StringReader(contents);
        
        /* read in 2 passes, 4 bytes first then 3 bytes next */
        String pass1 = StreamUtil.readExactly(sr, 4);
        String pass2 = StreamUtil.readExactly(sr, 3); 
     
        /* verify */
        assertEquals("first pass does not match", "test", pass1);
        assertEquals("second pass does not match", "ing", pass2);
    }
    
    
}


