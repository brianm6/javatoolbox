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
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Match against the entire path of a file's location.
     * <br>
     * <pre>/tmp/some/dir/file.txt</pre> would match against the entire string.
     * 
     * @see #matchAgainst_
     */
    public static final int MATCH_ALL  = 0;
    
    /**
     * Match against only the path portion of a files location.
     * <br>
     * <pre>/tmp/some/dir/file.txt</pre> would match against 
     * <pre>/tmp/some/dir</pre>.
     * 
     * @see #matchAgainst_
     */
    public static final int MATCH_PATH = 1;
    
    /**
     * Match against only the file portion of a file's location.
     * <br>
     * <pre>/tmp/some/dir/file.txt</pre> would match against 
     * <pre>file.txt</pre>.
     * 
     * @see #matchAgainst_
     */
    public static final int MATCH_FILE = 2;
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Which part of the entire files location to match against? The entire 
     * thing string, path only, or file only.
     */
    private int matchAgainst_;
    
    /** 
     * Regular expression matcher. 
     */
    private RE regExp_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a file filter that applies a regular expression to the name of
     * a file.
     * 
     * @param regExp Regular expression to match.
     * @param matchAgainst Portion of the file's location to match against. 
     * @param matchCase Set to true to observe case sensetivity.
     * @throws RESyntaxException if the regular expression is invalid.
     */
    public RegexFileFilter(String regExp, int matchAgainst, boolean matchCase)
        throws RESyntaxException
    {
        regExp_ = new RE(regExp);
        matchAgainst_ = matchAgainst;
        
        regExp_.setMatchFlags(
            matchCase ? RE.MATCH_NORMAL: RE.MATCH_CASEINDEPENDENT);        
    }

    //--------------------------------------------------------------------------
    // Overrides AbstractFileFilter
    //--------------------------------------------------------------------------
    
    /**
     * Tests if a specified file should be included in a file list.
     *
     * @param dir Directory in which the file was found.
     * @param name Name of the file.
     * @return True if and only if the name should be included in the file list;
     *         false otherwise.
     */
//    public boolean accept(File dir, String name)
//    {
//        boolean result = false;
//        
//        switch (matchAgainst_)
//        {
//            case MATCH_ALL : 
//                result = accept (new File(dir, name));
//                break;
//            
//            case MATCH_PATH: 
//                try
//                {
//                    result = regExp_.match(dir.getCanonicalPath());
//                }
//                catch (IOException e)
//                {
//                    throw new RuntimeException(e);
//                }
//                break;
//                
//            case MATCH_FILE: 
//                result = regExp_.match(name);
//                break;
//                
//            default:
//                throw new IllegalArgumentException(
//                    "Invalid match flag: " + matchAgainst_);
//        }
//        
//        return result;
//    }
    
    
    /**
     * @see org.apache.commons.io.filefilter.AbstractFileFilter#accept(
     *      java.io.File)
     */
    public boolean accept(File file)
    {
        boolean result = false;
        
        try
        {
            switch (matchAgainst_)
            {
                case MATCH_ALL : 
                    result = regExp_.match(file.getCanonicalPath());
                    break;
                
                case MATCH_PATH:
                    if (file.isFile())
                        result = regExp_.match(file.getParent());
                    else
                        result = regExp_.match(file.getCanonicalPath());
                    break;
                    
                case MATCH_FILE:
                    if (file.isFile())
                        result = regExp_.match(file.getName());
                    else
                        result = false; // Can't match a file to a dir
                    break;
                    
                default:
                    throw new IllegalArgumentException(
                        "Invalid match flag: " + matchAgainst_);
            }
        }
        catch (IOException e)  
        {
            // Thrown by getCanonicalPath()
            throw new RuntimeException(e);
        }
        
        return result;
    }
}