package toolbox.util.io;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

import toolbox.util.StringUtil;

/**
 * Simple reader that reads string delimited tokens a line at a time
 */
public class TokenReader extends LineNumberReader
{
    /**
     * Token delimiter
     */
    private String delimiter_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Create a TokenReader with the given delimiter
     * 
     * @param   in          Reader to read tokens from
     * @param   delimiter   Delimiter used to separate tokens
     */
    public TokenReader(Reader in, String delimiter)
    {
        super(in);
        delimiter_ = delimiter;
    }
    
    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the next batch of tokens parsed from a single line
     *
     * @return  Array of tokens, or null or end of reader reached.
     * @throws  IOException on IO error
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