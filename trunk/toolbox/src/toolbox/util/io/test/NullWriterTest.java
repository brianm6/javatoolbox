package toolbox.util.io.test;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import toolbox.util.io.NullWriter;

/**
 * Unit test for NullWriter
 */
public class NullWriterTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(NullWriterTest.class);
        
    /**
     * Entrypoint
     */
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(NullWriterTest.class);
    }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for NullWriterTest.
     * 
     * @param arg0
     */
    public NullWriterTest(String arg0)
    {
        super(arg0);
    }

    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the NullWriter
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
