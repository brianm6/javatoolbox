package toolbox.util.io.test;

import java.io.StringReader;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;
import toolbox.util.io.RegexFilterReader;

/** 
 * Unit test for RegexFilterReader
 */
public class RegexFilterReaderTest extends TestCase
{
    /** Logger **/
    private static final Category logger_ = 
	    Category.getInstance(RegexFilterReaderTest.class);
        
    /**
     * Constructor for RegexFilterReaderTest.
     * 
     * @param arg0
     */
    public RegexFilterReaderTest(String arg0)
    {
        super(arg0);
    }

    /**
	 * Entrypoint
	 *
	 * @param  args  Arguments
	 */
    public static void main(String[] args)
    {
        BasicConfigurator.configure();
        TestRunner.run(RegexFilterReaderTest.class);
    }
    
    /**
     * Tests readLine() for a simple test case
     */
    public void testRegexReader() throws Exception
    {
        String data = "one\ntwo\nthree\nfour\n";
        
        StringReader sr = new StringReader(data);
        RegexFilterReader rr = new RegexFilterReader(sr, "three", true);
        
        String result;
        
        while( (result = rr.readLine()) != null)
        {
            System.out.println("result=" + result);            
        }
    }
}
