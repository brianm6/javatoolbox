package toolbox.util.io.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ThreadUtil;
import toolbox.util.io.StringInputStream;

/**
 * Unit test for StringInputStream 
 */
public class StringInputStreamTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(StringInputStreamTest.class);
        
    /**
     * Entrypoint
     * 
     * @param args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(StringInputStreamTest.class);
    }
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
        
    /**
     * Constructor for StringInputStreamTest
     * 
     * @param  arg0  Name
     */
    public StringInputStreamTest(String arg0)
    {
        super(arg0);
    }
    
    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
        
    /**
     * Tests the read() method
     * 
     * @throws Exception on error
     */
    public void testRead() throws Exception
    {
        logger_.info("Running testRead...");
        
        String str = "hello";
        StringInputStream sis = new StringInputStream(str);
        byte[] readBuf = new byte[str.length()];
        int numRead = sis.read(readBuf);
        assertEquals("Number of chars read on equals", str.length(), numRead);
        String compare = new String(readBuf);
        assertEquals("String read from stream doesn't match", str, compare);
    }
    
    
    /**
     * Tests the read() method when stream is empty
     * 
     * @throws Exception on error
     */
    public void testReadEmpty() throws Exception
    {
        logger_.info("Running testReadEmpty...");
        
        StringInputStream sis = new StringInputStream("");
        
        assertEquals("should not be able to read from stream", 
            0, sis.available());
            
        assertEquals("read() should return -1", -1, sis.read());
    }

    
    /**
     * Tests available() method
     */
    public void testAvailable() throws Exception
    {
        logger_.info("Running testAvailable...");
        
        // Case zero
        StringInputStream sis = new StringInputStream("");
        assertEquals("available should be zero", 0, sis.available());
        
        // Case one
        sis = new StringInputStream("x");
        assertEquals("available should be one", 1, sis.available());
        
        // Case many 
        String many = "qiwuerpoqierupqiwuerpqowiuerpoqiwuerpqiurp";
        sis = new StringInputStream(many);
        assertEquals("available is incorrect", many.length(), sis.available());
    }
    
    
    /** 
     * Tests read on an empty stream with ignore EOF set to true
     */
    public void testReadEmptyIgnoreEOF() throws Exception
    {
        logger_.info("Running testReadEmptyIgnoreEOF...");
        
        StringInputStream sis = new StringInputStream(true);
 
        int iterations = 3;
        ThreadUtil.run(this, "stuffStream", 
            new Object[] { sis, new Integer(1000), "x", new Integer(iterations)});
        
        for (int i=0; i<iterations; i++)                  
        {
            int c = sis.read();
            logger_.info("Read: " + (char)c);
            assertEquals('x', (char)c);
        }
        
        sis.setIgnoreEOF(false);
        assertEquals(-1, sis.read());
    }   
 
    /**
     * Tests append()
     */
    public void testAppend() throws Exception
    {
        logger_.info("Running testAppend...");
        
        StringInputStream sis = new StringInputStream();
        
        int c = -1;
        
        sis.append("x");
        c = sis.read();
        assertEquals('x', (char)c);
        
        sis.append("ab");
        assertEquals('a', (char)sis.read());
        assertEquals('b', (char)sis.read());
        
    }
 
    //--------------------------------------------------------------------------
    //  Helper Methods
    //--------------------------------------------------------------------------   
    
    public void stuffStream(StringInputStream sis, int delay, String s, 
        int iterations) 
    {
        for (int i=0; i<iterations; i++)
        {
            sis.append(s);
            ThreadUtil.sleep(delay);
        }
    }
}