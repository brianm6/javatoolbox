package toolbox.util.net.test;

import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.net.IConnection;
import toolbox.util.net.StringConnection;

/**
 * Unit test for StringConnection
 */
public class StringConnectionTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(StringConnectionTest.class);

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
        TestRunner.run(StringConnectionTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests getInputStream()
     * 
     * @throws  Exception on error
     */
    public void testGetInputStream() throws Exception
    {
        logger_.info("Running testGetInputStream...");
        
        StringConnection conn = new StringConnection("abcd");
        conn.setName("StringConnection1");
        InputStream is = conn.getInputStream();
        assertNotNull(is);
    }

    /**
     * Tests getOutputStream()
     * 
     * @throws  Exception on error
     */
    public void testGetOutputStream() throws Exception
    {
        logger_.info("Running testGetOutputStream...");
        
        IConnection conn = new StringConnection("abcd");
        OutputStream os = conn.getOutputStream();
        assertNotNull(os);
    }
    
    /**
     * Tests the isConnected() method
     * 
     * @throws  Exception on error
     */
    public void testIsConnected() throws Exception
    {
        logger_.info("Running testIsConnected...");
        
        IConnection conn = new StringConnection("ping");
        assertTrue(!conn.isConnected());
        conn.close();
        assertTrue(!conn.isConnected());
        conn.connect();
        assertTrue(conn.isConnected());
        conn.close();
    }
}