package toolbox.util.io.test;

import java.io.StringReader;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.io.PositionReader;

/**
 * Unit test for PositionReader
 */
public class PositionReaderTest extends TestCase
{
    /** Logger */
    public static final Logger logger_ =
        Logger.getLogger(PositionReaderTest.class);

    /**
     * Entrypoint
     * 
     * @param  args  None
     */
    public static void main(String[] args)
    {
        TestRunner.run(PositionReaderTest.class);
    }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for PositionReaderTest.
     * 
     * @param arg0  Test name
     */
    public PositionReaderTest(String arg0)
    {
        super(arg0);
    }

    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
        
    /**
     * Test readUntil()
     * 
     * @throws Exception on error
     */    
    public void testReadUntil() throws Exception
    {
        logger_.info("Running testReadUntil...");

        // Read until found mid string        
        StringReader sr = new StringReader("abcdefghijk");
        PositionReader pr = new PositionReader(sr);
        String read = pr.readUntil('f');
        assertEquals("abcdef", read);
        assertEquals(6, pr.getOffset());
        
        // Read until found at end of string
        sr = new StringReader("abcdefghijk");
        pr = new PositionReader(sr);
        read = pr.readUntil('k');
        assertEquals("abcdefghijk", read);
        assertEquals(11, pr.getOffset());
        
        // Read until not found at all
        sr = new StringReader("abcdefghijk");
        pr = new PositionReader(sr);
        read = pr.readUntil('z');
        assertEquals("abcdefghijk", read);
        assertEquals(11, pr.getOffset());
        
        // Read until found at first char
        sr = new StringReader("abcdefghijk");
        pr = new PositionReader(sr);
        read = pr.readUntil('a');
        assertEquals("a", read);
        assertEquals(1, pr.getOffset());
    }
}