package toolbox.util.io.filter;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 * RegexFilter is a file filter that accepts based on the filename matching
 * a regular expression.
 */
public class RegexFilter implements FilenameFilter
{
    /** 
     * Regular expression as a string 
     */
    private String strRegExp_;
    
    /** 
     * Flag to match case 
     */
    private boolean matchCase_;
    
    /** 
     * Regular expression 
     */
    private RE regExp_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a file filter that applies a regular expression to the name of
     * a file
     * 
     * @param  regExp     Regular expression to match
     * @param  matchCase  Set to true to observe case sensetivity
     * @throws RESyntaxException if the regular expression is invalid
     */
    public RegexFilter(String regExp, boolean matchCase) 
        throws RESyntaxException
    {
        strRegExp_ = regExp;
        matchCase_ = matchCase;
        
        regExp_ = new RE(strRegExp_);
        
        if (!matchCase_)
            regExp_.setMatchFlags(RE.MATCH_CASEINDEPENDENT);        
    }

    //--------------------------------------------------------------------------
    //  FilenameFilter Interface
    //--------------------------------------------------------------------------
    
    /**
     * Tests if a specified file should be included in a file list.
     *
     * @param   dir    Directory in which the file was found.
     * @param   name   Name of the file.
     * @return  True if and only if the name should be included in the 
     *          file list; false otherwise.
     */
    public boolean accept(File dir, String name)
    {
        return (regExp_.match(name));
    }
}