package toolbox.jtail.filter;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 * RegexLineFilter applies a regular expression to a string of text. If there 
 * is a match, the text is returned, otherwise a null is returned.
 */
public class RegexLineFilter extends AbstractLineFilter
{
    /** Regular expression */
    private String strRegExp_;
    
    /** Flag to match case */
    private boolean matchCase_;
    
    /** Regular expression engine */
    private RE regExp_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Default constructor that matches everything.
     * 
     * @throws  RESyntaxException on an invalid regular expression
     */
    public RegexLineFilter() throws RESyntaxException
    {
        this(".*", false);
    }

    /**
     * Creates a RegexLineFilter with the given regular expression. The match
     * is case insensetive by default. 
     * 
     * @param   regularExpression   Regular expression to match against
     * @throws  RESyntaxException on an invalid regular expression
     */
    public RegexLineFilter(String regularExpression) throws RESyntaxException
    {
        this(regularExpression, false);
    }
        
    /**
     * Creates a RegexLineFilter with the given regular expression and case
     * matching flag.
     * 
     * @param   regularExpression     Regular expression to match
     * @param   matchCase             Set to true to observe case sensetivity
     * @throws  RESyntaxException if the regular expression is invalid
     */
    public RegexLineFilter(String regularExpression, boolean matchCase)
        throws RESyntaxException
    {
        setRegularExpression(regularExpression);
        setMatchCase(matchCase);
    }

    //--------------------------------------------------------------------------
    //  ILineFilter Interface
    //--------------------------------------------------------------------------
    
    /**
     * Filters a line based on a regular expression
     * 
     * @param   line  Line to match
     * @return  Line if it matched the regular expression, null otherwise
     */
    public String filter(String line)
    {
        if (!isEnabled())
            return line;
            
        if (line == null)
            return line;
            
        return regExp_.match(line) ? line : null;
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the regular expression
     * 
     * @param   regExp  Regular expression
     * @throws  RESyntaxException on invalid regular expression
     */
    public void setRegularExpression(String regExp) throws RESyntaxException
    {
        strRegExp_ = regExp;
        regExp_ = new RE(strRegExp_);
    }
    
    /**
     * Sets case matching
     * 
     * @param  matchCase  Flag to match case
     */
    public void setMatchCase(boolean matchCase) 
    {
        matchCase_ = matchCase;
        
        if (!matchCase_)
            regExp_.setMatchFlags(RE.MATCH_CASEINDEPENDENT);
        else
            regExp_.setMatchFlags(RE.MATCH_NORMAL);
    }
}