package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import toolbox.util.StreamUtil;
import toolbox.util.io.StringInputStream;

/**
 * Unit test for StreamUtil
 */
public class StreamUtilTest extends TestCase
{
    /**
     * Entrypoint
     */
    public static void main(String[] args)
    {
        TestRunner.run(StreamUtilTest.class);
    }
    
    /**
     * Constructor for StreamUtilTest
     */
    public StreamUtilTest(String arg0)
    {
        super(arg0);
    }
    
    /**
     * Tests the toString() method
     */
    public void testToString() throws Exception
    {
        String testString = "this is a test string for toString()";
        
        StringInputStream sis = new StringInputStream(testString);
        String toString = StreamUtil.asString(sis);
        
        assertEquals("toString() does not match original string", 
            testString, toString);
    }
}


