package toolbox.util.io.test;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.io.FixedWidthWriter;

/**
 * Unit Test for FixedWidthWriter
 */
public class FixedWidthWriterTest extends TestCase
{
    /** Logger **/
    private static final Logger logger_ =
        Logger.getLogger(FixedWidthWriterTest.class);
        
    /**
     * Entry point
     * 
     * @param  args  None
     */
    public static void main(String[] args)
    {
        TestRunner.run(FixedWidthWriterTest.class);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for FixedWidthWriterTest.
     * 
     * @param arg0  Test name
     */
    public FixedWidthWriterTest(String arg0)
    {
        super(arg0);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the write() method
     * 
     * @throws  IOException on error
     */
    public void testWrite() throws IOException
    {
        logger_.info("Running testWrite...");
        
        StringWriter sw = new StringWriter();
        FixedWidthWriter fwr = new FixedWidthWriter(sw);
        
        fwr.write("testWrite", 20, '.', false);

        assertEquals("Strings don't match", 
            "...........testWrite", sw.getBuffer().toString());
    }
}
