package toolbox.util.io.test;

import java.io.IOException;

import junit.framework.TestCase;

import toolbox.util.io.NullWriter;

/**
 * Unit test for NullWriter
 */
public class NullWriterTest extends TestCase
{
    /**
     * Entrypoint
     */
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(NullWriterTest.class);
    }


    /**
     * Constructor for NullWriterTest.
     * 
     * @param arg0
     */
    public NullWriterTest(String arg0)
    {
        super(arg0);
    }


    /**
     * Tests the NullWriter
     */
    public void testNullWriter() throws IOException
    {
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
