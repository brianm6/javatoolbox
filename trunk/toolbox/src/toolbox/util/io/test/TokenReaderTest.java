package toolbox.util.io.test;

import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.log4j.Category;
import toolbox.util.ArrayUtil;
import toolbox.util.io.TokenReader;

/**
 * Unit test for TokenReader
 */
public class TokenReaderTest extends TestCase
{
    /** Logger **/
    public static final Category logger_ =
        Category.getInstance(TokenReaderTest.class);
        
    /**
     * Entrypoint
     */
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(TokenReaderTest.class);
    }

    /**
     * Constructor for TokenReaderTest.
     * 
     * @param arg0
     */
    public TokenReaderTest(String arg0)
    {
        super(arg0);
    }
    
    /**
     * Tests an empty reader
     */    
    public void testEmptyReader() throws Exception
    {
        TokenReader tr = new TokenReader( new StringReader(""), ",");
        String[] tokens = tr.readTokens();
        assertNull("tokens should be null", tokens);
    }
    
    /**
     * Tests a reader with a single line of data
     */
    public void testSingleLineReader() throws Exception
    {
        TokenReader tr = new TokenReader( new StringReader("a,b,c,d,e,f"), ",");
        String[] tokens = tr.readTokens();
        assertNotNull("first line should not be null", tokens);
        assertEquals("number of tokens incorrect", 6, tokens.length);
        tokens = tr.readTokens();
        assertNull("next line should be null", tokens);
    }
    
    /**
     * Tests a reader with multiple lines of multiple tokens
     */
    public void testMultiLineReader() throws Exception
    {
        int numLines = 3;
        String lines = "a,b,c\nd,e,f\ng,h,i";
        TokenReader tr = new TokenReader( new StringReader(lines), ",");
        
        for (int i=0; i<numLines; i++)
        {
            String[] tokens = tr.readTokens();
            assertNotNull("line should not be null", tokens);
            assertEquals("number of tokens incorrect", 3, tokens.length);
            logger_.info("Tokens: " + ArrayUtil.toString(tokens));            
        }
        
        assertNull("next line should be null", tr.readTokens());
    }
}