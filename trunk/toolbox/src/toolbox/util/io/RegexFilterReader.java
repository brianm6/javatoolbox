package toolbox.util.io;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

import org.apache.log4j.Category;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 * RegexFilterReader applies a regular expression to each line
 * read from a Reader. If the line matches the regular
 * expression, it is included and returned to the caller, 
 * otherwise the line is omitted (/dev/null).
 */
public class RegexFilterReader extends LineNumberReader
{
    /** Logger **/
    private static final Category logger_ = 
        Category.getInstance(RegexFilterReader.class);
    
    /** Default regular expression matches all if one is not specified **/    
    private static final String DEFAULT_MATCH = "*";

    /** Regular expression as a string **/
    private String strRegExp_;
    
    /** Flag to match case **/
    private boolean matchCase_;
    
    /** Regular expression */
    private RE regExp_;

    
    /**
     * Creates a RegexFilterReader
     * 
     * @param  in  Reader to wrap
     */
    public RegexFilterReader(Reader in)
    {
        this(in, DEFAULT_MATCH, false);
    }


    /**
     * Creates a RegexFilterReader
     * 
     * @param  in         Reader to wrap
     * @param  regExp     Regular expression to match
     * @param  matchCase  Set to true to observe case sensetivity
     */
    public RegexFilterReader(Reader in, String regExp, boolean matchCase)
    {
        super(in);
        strRegExp_ = regExp;
        matchCase_ = matchCase;

        try
        {
            regExp_ = new RE(strRegExp_);
            
            if (!matchCase_)
                regExp_.setMatchFlags(RE.MATCH_CASEINDEPENDENT);        
        }
        catch (RESyntaxException e)
        {
            logger_.error("constructor", e);
        }
    }

    
    /**
     * Reads the next line that matches the regular expression
     * 
     * @return  String that matches regular expression or null if the 
     *          end of the stream has been reached
     * @throws  IOException on error
     */
    public String readLine() throws IOException
    {
        String  line = null;
        
        while(true)
        {
            line = super.readLine();
            
            if (line == null)
                return null;
            else if(regExp_.match(line))
                return line;
            else
                logger_.debug("failed match: " + line);
        }
    }
}
