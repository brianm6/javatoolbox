package toolbox.util.io;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

import toolbox.util.StringUtil;

/**
 * A {@link java.io.Reader} that reads in lines of token delimited text. One 
 * line is read per readTokens() invocation.
 * <p>
 * Example:
 * <pre>
 * String s = "one,two,three\nfour,five,six";
 * TokenReader reader = new TokenReader(new StringReader(s));
 * 
 * System.out.println(line1 = reader.readTokens());  // reads in one two three
 * System.out.println(line2 = reader.readTokens());  // reads in four fix six
 * 
 * reader.close();
 * </pre>
 */
public class TokenReader extends LineNumberReader
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Token delimiter.
     */
    private String delimiter_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Create a TokenReader with the given delimiter.
     * 
     * @param in Reader to read tokens from.
     * @param delimiter Delimiter used to separate tokens.
     */
    public TokenReader(Reader in, String delimiter)
    {
        super(in);
        delimiter_ = delimiter;
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the next batch of tokens parsed from a single line.
     *
     * @return Array of tokens, or null or end of reader reached.
     * @throws IOException on I/O error.
     */
    public String[] readTokens() throws IOException
    {
        String line = readLine();
        
        if (line == null)
            return null;
        else            
            return StringUtil.tokenize(line, delimiter_);   
    }
}