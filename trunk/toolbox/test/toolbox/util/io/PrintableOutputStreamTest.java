package toolbox.util.io;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for PrintableOutputStream.
 */
public class PrintableOutputStreamTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(PrintableOutputStreamTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(PrintableOutputStreamTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the constructors.
     */
    public void testConstructors()
    {
        logger_.info("Running testConstructors...");
        
        PrintableOutputStream pos = 
            new PrintableOutputStream(new StringOutputStream());
        
        assertNotNull(pos);
        
        pos = new PrintableOutputStream(new StringOutputStream(), true, "x");
        assertNotNull(pos);
    }
    
    
    /**
     * Tests the write() method for a string that passes through unchanged.
     * 
     * @throws Exception on error.
     */
    public void testWritePassThrough() throws Exception
    {
        logger_.info("Running testWritePassThrough...");
        
        StringOutputStream sos = new StringOutputStream();
        PrintableOutputStream pos = new PrintableOutputStream(sos);
        String input = "abc123";
        pos.write(input.getBytes());
        String expected = input;
        assertEquals(expected, sos.toString());
    }
    
    
    /**
     * Tests the write() method for a string that filters binary chars.
     * 
     * @throws Exception on error.
     */
    public void testWriteFilter() throws Exception
    {
        logger_.info("Running testWriteFilter...");
        
        StringOutputStream sos = new StringOutputStream();
        
        //Try with a dot for the replacement.
        PrintableOutputStream pos = 
            new PrintableOutputStream(sos, true, ".");
        
        String expected = "....abc....123....";
        byte[] input = new byte[] { 
            1, 2, 3, 4, 
            "a".getBytes()[0], 
            "b".getBytes()[0],
            "c".getBytes()[0],
            5, 6, 7, 8,
            "1".getBytes()[0], 
            "2".getBytes()[0],
            "3".getBytes()[0],
            1, 2, 3, 4};
        
        pos.write(input);
        logger_.info("output:'" + sos.toString() + "'");
        assertEquals(expected, sos.toString());

        // Try with an empty replacement
        pos.setReplacement("");
        sos.getBuffer().setLength(0);
        pos.write(input);
        expected = "abc123";
        logger_.info("output:'" + sos.toString() + "'");
        assertEquals(expected, sos.toString());
    }

    
    /**
     * Tests to make sure tabs and newlines are preserved.
     * 
     * @throws Exception on error.
     */
    public void testWriteTabsNewlinesPreserved() throws Exception
    {
        logger_.info("Running testWriteTabsNewlinesPreserved...");
        
        StringOutputStream sos = new StringOutputStream();
        PrintableOutputStream pos = new PrintableOutputStream(sos);
        String input = "\n\t\n\t\n";
        pos.write(input.getBytes());
        assertEquals(input, sos.toString());
    }
}