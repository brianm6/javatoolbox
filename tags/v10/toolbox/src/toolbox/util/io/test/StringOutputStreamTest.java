package toolbox.util.io.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import toolbox.util.io.StringOutputStream;

/**
 * Unit test for StringOutputStream
 */
public class StringOutputStreamTest extends TestCase
{
    /**
     * Constructor for StringOutputStreamTest
     */
    public StringOutputStreamTest(String arg0)
    {
        super(arg0);
    }

    /**
     * Entrypoint   
     */
    public static void main(String[] args)
    {
        TestRunner.run(StringOutputStreamTest.class);
    }
    
    /**
     * Tests the write() method
     */
    public void testWrite() throws Exception
    {
        StringOutputStream sos = new StringOutputStream();
        
        String testString = "holy moly!";
        
        sos.write(testString.getBytes());
        
        assertEquals("strings don't match", testString, sos.toString());
    }
}


