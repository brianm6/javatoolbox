package toolbox.util.io.filter;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 * RegexFilter is a file filter that accepts based on the filename matching
 * a regular expression.
 */
public class RegexFileFilter extends AbstractFileFilter
{
    // TODO: Replace with file filter from commons if exists
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Regular expression matcher. 
     */
    private RE regExp_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a file filter that applies a regular expression to the entire
     * path + name of a file's absolute location.
     * 
     * @param regExp Regular expression to match.
     * @param matchCase Set to true to observe case sensetivity.
     * @throws RESyntaxException if the regular expression is invalid.
     */
    public RegexFileFilter(String regExp, boolean matchCase)
        throws RESyntaxException
    {
        regExp_ = new RE(regExp, 
            matchCase ? RE.MATCH_NORMAL: RE.MATCH_CASEINDEPENDENT);        
    }

    //--------------------------------------------------------------------------
    // Overrides AbstractFileFilter
    //--------------------------------------------------------------------------
    
    /**
     * @see org.apache.commons.io.filefilter.AbstractFileFilter#accept(
     *      java.io.File)
     */
    public boolean accept(File file)
    {
        try
        {
            return regExp_.match(file.getCanonicalPath());
        }
        catch (IOException e)  
        {
            throw new RuntimeException(e);
        }
    }
}