package toolbox.util.io.test;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.io.FixedWidthWriter;

/**
 * Unit test for FixedWidthWriter
 */
public class FixedWidthWriterTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(FixedWidthWriterTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Entry point
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(FixedWidthWriterTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests writeLPad(String, int)
     * 
     * @throws IOException on error
     */
    public void testWriteLPad() throws IOException
    {
        logger_.info("Running testWriteLPad...");
        
        // Exact size
        StringWriter sw = new StringWriter();
        FixedWidthWriter fww = new FixedWidthWriter(sw);
        fww.writeLPad("hello", 5);
        assertEquals("hello", sw.getBuffer().toString());

        // Underflow
        sw = new StringWriter();
        fww = new FixedWidthWriter(sw);
        fww.writeLPad("hel", 5);
        assertEquals("  hel", sw.getBuffer().toString());

        // Overflow
        sw = new StringWriter();
        fww = new FixedWidthWriter(sw);
        fww.writeLPad("helloworld", 5);
        assertEquals("hello", sw.getBuffer().toString());
        
        // Empty
        sw = new StringWriter();
        fww = new FixedWidthWriter(sw);
        fww.writeLPad("", 5);
        assertEquals("     ", sw.getBuffer().toString());
        
        // One
        sw = new StringWriter();
        fww = new FixedWidthWriter(sw);
        fww.writeLPad("h", 5);
        assertEquals("    h", sw.getBuffer().toString());
    }

    /**
     * Tests writeLPad(String, int, char)
     * 
     * @throws IOException on error
     */
    public void testWriteLPadChar() throws IOException
    {
        logger_.info("Running testWriteLPadChar...");
        
        // Exact size
        StringWriter sw = new StringWriter();
        FixedWidthWriter fww = new FixedWidthWriter(sw);
        fww.writeLPad("hello", 5, '.');
        assertEquals("hello", sw.getBuffer().toString());

        // Underflow
        sw = new StringWriter();
        fww = new FixedWidthWriter(sw);
        fww.writeLPad("hel", 5, '.');
        assertEquals("..hel", sw.getBuffer().toString());

        // Overflow
        sw = new StringWriter();
        fww = new FixedWidthWriter(sw);
        fww.writeLPad("helloworld", 5, '.');
        assertEquals("hello", sw.getBuffer().toString());
        
        // Empty
        sw = new StringWriter();
        fww = new FixedWidthWriter(sw);
        fww.writeLPad("", 5, '.');
        assertEquals(".....", sw.getBuffer().toString());
        
        // One
        sw = new StringWriter();
        fww = new FixedWidthWriter(sw);
        fww.writeLPad("h", 5, '.');
        assertEquals("....h", sw.getBuffer().toString());
    }

    /**
     * Tests writeRPad(String, int)
     * 
     * @throws IOException on error
     */
    public void testWriteRPad() throws IOException
    {
        logger_.info("Running testWriteRPad...");
        
        // Exact size
        StringWriter sw = new StringWriter();
        FixedWidthWriter fww = new FixedWidthWriter(sw);
        fww.writeRPad("hello", 5);
        assertEquals("hello", sw.getBuffer().toString());

        // Underflow
        sw = new StringWriter();
        fww = new FixedWidthWriter(sw);
        fww.writeRPad("hel", 5);
        assertEquals("hel  ", sw.getBuffer().toString());

        // Overflow
        sw = new StringWriter();
        fww = new FixedWidthWriter(sw);
        fww.writeRPad("helloworld", 5);
        assertEquals("hello", sw.getBuffer().toString());
        
        // Empty
        sw = new StringWriter();
        fww = new FixedWidthWriter(sw);
        fww.writeRPad("", 5);
        assertEquals("     ", sw.getBuffer().toString());
        
        // One
        sw = new StringWriter();
        fww = new FixedWidthWriter(sw);
        fww.writeRPad("h", 5);
        assertEquals("h    ", sw.getBuffer().toString());
    }

    /**
     * Tests writeRPad(String, int, char)
     * 
     * @throws IOException on error
     */
    public void testWriteRPadChar() throws IOException
    {
        logger_.info("Running testWriteRPadChar...");
        
        // Exact size
        StringWriter sw = new StringWriter();
        FixedWidthWriter fww = new FixedWidthWriter(sw);
        fww.writeRPad("hello", 5, '.');
        assertEquals("hello", sw.getBuffer().toString());

        // Underflow
        sw = new StringWriter();
        fww = new FixedWidthWriter(sw);
        fww.writeRPad("hel", 5, '.');
        assertEquals("hel..", sw.getBuffer().toString());

        // Overflow
        sw = new StringWriter();
        fww = new FixedWidthWriter(sw);
        fww.writeRPad("helloworld", 5, '.');
        assertEquals("hello", sw.getBuffer().toString());
        
        // Empty
        sw = new StringWriter();
        fww = new FixedWidthWriter(sw);
        fww.writeRPad("", 5, '.');
        assertEquals(".....", sw.getBuffer().toString());
        
        // One
        sw = new StringWriter();
        fww = new FixedWidthWriter(sw);
        fww.writeRPad("h", 5, '.');
        assertEquals("h....", sw.getBuffer().toString());
    }
    
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