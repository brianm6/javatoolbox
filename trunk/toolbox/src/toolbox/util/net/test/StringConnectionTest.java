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
     * Constructor for StringConnectionTest.
     * @param name
     */
    public StringConnectionTest(String name)
    {
        super(name);
    }

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
}
