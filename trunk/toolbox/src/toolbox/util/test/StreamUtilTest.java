package toolbox.util.test;

import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.StreamUtil;
import toolbox.util.io.StringInputStream;
import toolbox.util.io.StringOutputStream;

/**
 * Unit test for StreamUtil.
 */
public class StreamUtilTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(StreamUtilTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
            
    /**
     * Entrypoint.
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(StreamUtilTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
        
    /**
     * Tests the asString() method.
     * 
     * @throws Exception on error
     */
    public void testAsString() throws Exception
    {
        logger_.info("Running testAsString...");
        
        String testString = "this is a test string for asString()";
        StringInputStream sis = new StringInputStream(testString);
        String asString = StreamUtil.asString(sis);
        assertEquals(testString, asString);
    }

    
    /**
     * Tests toBytes()
     * 
     * @throws Exception on error
     */
    public void testToBytes() throws Exception
    {
        logger_.info("Running testToBytes...");
        
        String testString = "this is a test string for toBytes()";
        StringInputStream sis = new StringInputStream(testString);
        byte[] bytes = StreamUtil.toBytes(sis);
        assertEquals(testString, new String(bytes));
    }
    
    
    /**
     * Tests empty()
     * 
     * @throws Exception on error
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
     * Tests readExactly() for a stream.
     * 
     * @throws Exception on error
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
     * Tests readExactly() for a reader.
     * 
     * @throws Exception on error
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
    
    
    /**
     * Tests close(OutputStream)
     * 
     * @throws Exception on error
     */
    public void testCloseOutputStream() throws Exception
    {
        logger_.info("Running testCloseOutputStream...");
        
        StringOutputStream sos = new StringOutputStream();
        sos.write("testing".getBytes());
        StreamUtil.close(sos);
        StreamUtil.close((OutputStream) null);
    }
   
    
    /**
     * Tests close(InputStream)
     * 
     * @throws Exception on error
     */
    public void testCloseInputStream() throws Exception
    {
       logger_.info("Running testCloseOutputStream...");
        
       StringOutputStream sos = new StringOutputStream();
       sos.write("testing".getBytes());
       StreamUtil.close(sos);
       StreamUtil.close((OutputStream) null);
    }
    
    
    /**
     * Tests close(Writer)
     * 
     * @throws Exception on error
     */
    public void testCloseWriter() throws Exception
    {
        logger_.info("Running testCloseWriter...");
        
        StringWriter w = new StringWriter();
        w.write("testing");
        StreamUtil.close(w);
    }
    
    
    /**
     * Tests close(Writer) when passing in a null value. Expected behavior is
     * to ignore and not raise any error conditions.
     * 
     * @throws Exception on error.
     */
    public void testCloseWriterNull() throws Exception
    {
        logger_.info("Running testCloseWriterNull...");
        
        Writer w = null;
        
        try
        {
          StreamUtil.close(w);
          
          // Success.
          assertTrue(true);
        }
        catch (Exception e)
        {
            fail("Null writer should not generate exception");
        }
        
    }    
}