package toolbox.util.io.test;

import java.io.IOException;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.io.NullWriter;

/**
 * Unit test for NullWriter
 */
public class NullWriterTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(NullWriterTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Entrypoint
     * 
     * @param args None
     */
    public static void main(String[] args)
    {
        TestRunner.run(NullWriterTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the NullWriter
     * 
     * @throws IOException on error
     */
    public void testNullWriter() throws IOException
    {
        logger_.info("Running testNullWriter...");
        
        NullWriter nw = new NullWriter();    
        nw.write("hello".toCharArray());
        nw.write("goodbye".toCharArray(), 1, 2);
        nw.write(34);
        nw.write("foo");
        nw.write("bar", 1, 1);
        nw.flush();
        nw.close();
    }
}
