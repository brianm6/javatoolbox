package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import toolbox.util.StringInputStream;

/**
 * Unit test for StringInputStream 
 */
public class StringInputStreamTest extends TestCase
{
    /**
     * Constructor for StringInputStreamTest
     */
    public StringInputStreamTest(String arg0)
    {
        super(arg0);
    }
	
	/**
	 * Entrypoint
	 */
    public static void main(String[] args)
    {
    	TestRunner tr = new TestRunner();
    	tr.run(StringInputStreamTest.class);
    }
    
    /**
     * Tests the read() method
     */
    public void testRead() throws Exception
    {
    	String str = "hello";
   		StringInputStream sis = new StringInputStream(str); 	
   		
   		byte[] readBuf = new byte[str.length()];
   		int numRead = sis.read(readBuf);
   		assertEquals("Number of chars read on equals", str.length(), numRead);
   		
   		String compare = new String(readBuf);
   		assertEquals("String read from stream doesn't match", str, compare);
    }
    
    /**
     * Tests the read() method when stream is empty
     */
   	public void testReadEmpty() throws Exception
   	{
		StringInputStream sis = new StringInputStream("");
		assertEquals("should not be able to read from stream", 0, sis.available());
		assertEquals("read() should return -1", -1, sis.read());
   	}
}

