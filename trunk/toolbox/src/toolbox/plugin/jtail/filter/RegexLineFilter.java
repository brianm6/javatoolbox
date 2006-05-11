package toolbox.plugin.jtail.filter;

import java.util.List;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 * RegexLineFilter applies a regular expression to a string of text. If there 
 * is a match, the text is returned, otherwise a null is returned.
 */
public class RegexLineFilter extends AbstractLineFilter
{
    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    /** 
     * Regular expression.
     */
    private String strRegExp_;
    
    /** 
     * Flag to match case.
     */
    private boolean matchCase_;
    
    /** 
     * Regular expression engine. 
     */
    private RE regExp_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a RegexLineFilter that matches any string.
     * 
     * @throws RESyntaxException on an invalid regular expression.
     */
    public RegexLineFilter() throws RESyntaxException
    {
        this(".*", false);
    }


    /**
     * Creates a RegexLineFilter with the given regular expression. The match
     * is case insensetive by default. 
     * 
     * @param regularExpression Regular expression to match against.
     * @throws RESyntaxException on an invalid regular expression.
     */
    public RegexLineFilter(String regularExpression) throws RESyntaxException
    {
        this(regularExpression, false);
    }

        
    /**
     * Creates a RegexLineFilter with the given regular expression and case
     * matching flag.
     * 
     * @param regularExpression Regular expression to match.
     * @param matchCase Set to true to observe case sensetivity.
     * @throws RESyntaxException if the regular expression is invalid.
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
     * Filters a line based on a regular expression.
     * 
     * @see toolbox.plugin.jtail.filter.ILineFilter#filter(java.lang.StringBuffer)
     */
    public boolean filter(StringBuffer line)
    {
        if (isEnabled())
            return regExp_.match(line.toString()) ? true : false;    
        else
            return true;
    }
    
    /*
     * @see toolbox.plugin.jtail.filter.ILineFilter#filter(java.lang.StringBuffer, java.util.List)
     */
    public boolean filter(StringBuffer line, List segments)
    {
        return filter(line);
    }
    
    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the regular expression.
     * 
     * @param regExp Regular expression.
     * @throws RESyntaxException on invalid regular expression.
     */
    public void setRegularExpression(String regExp) throws RESyntaxException
    {
        strRegExp_ = regExp;
        regExp_ = new RE(strRegExp_);
    }
   
   
    /**
     * Sets case matching.
     * 
     * @param matchCase Flag to match case.
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