package toolbox.util.io.test;

import java.io.IOException;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.io.NullOutputStream;

/**
 * Unit test for NullOutputStream
 */
public class NullOutputStreamTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(NullOutputStreamTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Entrypoint
     * 
     * @param  args  None
     */
    public static void main(String[] args)
    {
        TestRunner.run(NullOutputStreamTest.class);
    }

    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the constructor
     * 
     * @throws IOException on error
     */
    public void testNullOutputStream() throws IOException
    {
        logger_.info("Running testConstructor...");
        
        assertNotNull(new NullOutputStream());
    }
    
    /**
     * Tests close()
     * 
     * @throws IOException on error
     */
    public void testClose() throws IOException
    {
        logger_.info("Running testClose...");
        
        NullOutputStream nos = new NullOutputStream();
        nos.close();
        nos.close();
    }

    /**
     * Tests flush()
     * 
     * @throws IOException on error
     */
    public void testFlush() throws IOException
    {
        logger_.info("Running testFlush...");
        
        NullOutputStream nos = new NullOutputStream();
        nos.flush();
    }

    /**
     * Tests write(byte)
     * 
     * @throws IOException on error
     */
    public void testWrite() throws IOException
    {
        logger_.info("Running testWrite...");
        
        NullOutputStream nos = new NullOutputStream();
        nos.write("blah".getBytes());
    }
    
    /**
     * Tests operations out of order
     * 
     * @throws IOException on error
     */
    public void testOutOfOrder() throws IOException
    {
        logger_.info("Running testOutOfOrder...");
        
        NullOutputStream nos = new NullOutputStream();
        nos.close();
        nos.flush();
        nos.write(45);
        nos.flush();
        nos.close();
        nos.write(34);
    }
}
