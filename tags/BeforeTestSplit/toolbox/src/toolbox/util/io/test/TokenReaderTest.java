package toolbox.util.io.test;

import java.io.StringReader;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.io.TokenReader;

/**
 * Unit test for TokenReader.
 */
public class TokenReaderTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(TokenReaderTest.class);

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
        TestRunner.run(TokenReaderTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
        
    /**
     * Tests an empty reader.
     * 
     * @throws Exception on error.
     */    
    public void testEmptyReader() throws Exception
    {
        logger_.info("Running testEmptyReader...");
        
        TokenReader tr = new TokenReader(new StringReader(""), ",");
        String[] tokens = tr.readTokens();
        assertNull("tokens should be null", tokens);
    }
    
    
    /**
     * Tests a reader with a single line of data.
     * 
     * @throws Exception on error.
     */
    public void testSingleLineReader() throws Exception
    {
        logger_.info("Running testSingleLineReader...");
        
        TokenReader tr = new TokenReader(new StringReader("a,b,c,d,e,f"), ",");
        String[] tokens = tr.readTokens();
        assertNotNull("first line should not be null", tokens);
        assertEquals("number of tokens incorrect", 6, tokens.length);
        tokens = tr.readTokens();
        assertNull("next line should be null", tokens);
    }
    
    
    /**
     * Tests a reader with multiple lines of multiple tokens.
     * 
     * @throws Exception on error.
     */
    public void testMultiLineReader() throws Exception
    {
        logger_.info("Running testMultiLineReader...");
        
        int numLines = 3;
        String lines = "a,b,c\nd,e,f\ng,h,i";
        TokenReader tr = new TokenReader(new StringReader(lines), ",");
        
        for (int i = 0; i < numLines; i++)
        {
            String[] tokens = tr.readTokens();
            assertNotNull("line should not be null", tokens);
            assertEquals("number of tokens incorrect", 3, tokens.length);
            logger_.info("Tokens: " + ArrayUtil.toString(tokens));            
        }
        
        assertNull("next line should be null", tr.readTokens());
    }
}