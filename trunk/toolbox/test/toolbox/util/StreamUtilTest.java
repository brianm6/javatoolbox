package toolbox.util;

import java.io.StringReader;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.io.StringInputStream;

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
     * Tests readExactly(InputStream).
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
     * Tests readExactly(InputStream) for blocking on input. 
     * 
     * @throws Exception on error
     */
    public void testReadExactlyStreamBlocks() throws Exception
    {
        logger_.info("Running testReadExactlyStreamBlocks...");
        
        //
        // create stream and populate
        //
        
        String contents = "12345";
        final StringInputStream sis = new StringInputStream(contents, true);

        //
        // Delay appending to the stream so that readExactly() will reach 
        // a blocking point.
        //
        
        new Thread(new Runnable() 
        {
            public void run()
            {
                ThreadUtil.sleep(3000);
                sis.append("6789");
            }
        }).start();
        

        //
        // Try to read in all 9 bytes. Should block after first 5 bytes and
        // then continue after 6789 is appended.
        //
        
        byte[] pass1 = StreamUtil.readExactly(sis, 9);
     
        //
        // verify
        //
        
        assertEquals("123456789", new String(pass1));
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
}