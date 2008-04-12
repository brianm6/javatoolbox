package toolbox.util.io.filter;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 * RegexFilter is a file filter that accepts based on the filename matching
 * a regular expression.
 */
public class RegexFileFilter extends AbstractFileFilter {
    
    // TODO: Replace with file filter from commons if exists
    
    public static final int APPLY_TO_BOTH = 0;
    public static final int APPLY_TO_FILENAME = 1;
    public static final int APPLY_TO_PATH = 2;
    
    public static final int DEFAULT_APPLY = APPLY_TO_BOTH;
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Regular expression matcher. 
     */
    private RE regExp_;
    
    /**
     * Which part of the absolute path and filename should the regular 
     * expression be applied to? See APPLY_* constants. Default scope is
     * the entire file and path.
     */
    private int scope_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    public RegexFileFilter(String regExp, boolean matchCase)
        throws RESyntaxException {
        this(regExp, matchCase, DEFAULT_APPLY);
    }

    /**
     * Creates a file filter that applies a regular expression to the entire
     * path + name of a file's absolute location.
     * 
     * @param regExp Regular expression to match.
     * @param matchCase Set to true to observe case sensetivity.
     * @throws RESyntaxException if the regular expression is invalid.
     */
    public RegexFileFilter(String regExp, boolean matchCase, int scope)
        throws RESyntaxException {
        scope_ = scope;
        regExp_ = new RE(regExp, 
            matchCase ? RE.MATCH_NORMAL: RE.MATCH_CASEINDEPENDENT);        
    }

    //--------------------------------------------------------------------------
    // Overrides AbstractFileFilter
    //--------------------------------------------------------------------------
    
    /*
     * @see org.apache.commons.io.filefilter.AbstractFileFilter#accept(java.io.File)
     */
    public boolean accept(File file) {
        try {
            String fileStr = file.getCanonicalPath();
            
            switch (scope_) {
                case APPLY_TO_BOTH    : return regExp_.match(fileStr);
                case APPLY_TO_FILENAME: return regExp_.match(FilenameUtils.getName(fileStr));
                case APPLY_TO_PATH    : return regExp_.match(FilenameUtils.getFullPathNoEndSeparator(fileStr));
                default: throw new IllegalArgumentException("Invalid scope of " + scope_);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}