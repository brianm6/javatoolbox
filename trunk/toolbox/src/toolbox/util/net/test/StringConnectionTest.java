package toolbox.util.net.test;

import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.net.IConnection;
import toolbox.util.net.StringConnection;

/**
 * Unit test for StringConnection
 */
public class StringConnectionTest extends TestCase
{
    /**
     * Entrypoint
     *
     * @param  args  Arguments
     */
    public static void main(String[] args)
    {
        TestRunner.run(StringConnectionTest.class);
    }
    
    /**
     * Constructor for StringConnectionTest.
     * @param name
     */
    public StringConnectionTest(String name)
    {
        super(name);
    }

    /**
     * Tests getInputStream()
     */
    public void testGetInputStream() throws Exception
    {
        IConnection conn = new StringConnection("abcd");
        InputStream is = conn.getInputStream();
        assertNotNull(is);
    }

    /**
     * Tests getOutputStream()
     */
    public void testGetOutputStream() throws Exception
    {
        IConnection conn = new StringConnection("abcd");
        OutputStream os = conn.getOutputStream();
        assertNotNull(os);
    }
    
    /**
     * Tests the isConnected() method
     */
    public void testIsConnected() throws Exception
    {
        IConnection conn = new StringConnection("ping");
        assertTrue(!conn.isConnected());
        conn.close();
        assertTrue(!conn.isConnected());
        conn.connect();
        assertTrue(conn.isConnected());
        conn.close();
    }
}