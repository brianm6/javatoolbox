package toolbox.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import toolbox.util.StringUtil;

/**
 * PrintableOutputStream is a stream that recognizes only letters of the
 * alpha, digits, and symbols. All binary characters are filtered out.
 */
public class PrintableOutputStream extends FilterOutputStream
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Replacement string for characters that are discarded.
     */
    private String replacement_;
    
    /**
     * Flag for enabling/disabling the filter criteria.
     */
    private boolean enabled_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a PrintableOutputStream.
     * 
     * @param out OutputStream to filter.
     */
    public PrintableOutputStream(OutputStream out)
    {
        this(out, true, "");
    }
    

    /**
     * Creates a PrintableOutputStream.
     * 
     * @param out OutputStream to filter.
     * @param enabled Enables the filter.
     * @param replacement Replacement character. Null to discard the filtered
     *        characters.
     */
    public PrintableOutputStream(
        OutputStream out, 
        boolean enabled, 
        String replacement)
    {
        super(out);
        setEnabled(enabled);
        setReplacement(replacement);
    }
    
    //--------------------------------------------------------------------------
    // Overrides FilterOutputStream
    //--------------------------------------------------------------------------
    
    /**
     * @see java.io.FilterOutputStream#write(int)
     */
    public void write(int b) throws IOException
    {
        char c = (char) b;
        
        if (!enabled_)
            super.write(b);
        else if ((b >=  32 && b <= 126) || c == '\n' || c == '\t') 
            super.write(b);
        else if (!StringUtil.isNullOrEmpty(replacement_))
            out.write(replacement_.getBytes());  // Is this a NO NO?
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the enabled.
     * 
     * @return boolean
     */
    public boolean isEnabled()
    {
        return enabled_;
    }
    
    
    /**
     * Sets the enabled.
     * 
     * @param enabled The enabled to set.
     */
    public void setEnabled(boolean enabled)
    {
        enabled_ = enabled;
    }
    
    
    /**
     * Returns the replacement.
     * 
     * @return String
     */
    public String getReplacement()
    {
        return replacement_;
    }
    
    
    /**
     * Sets the replacement.
     * 
     * @param String The replacement to set.
     */
    public void setReplacement(String replacement)
    {
        replacement_ = replacement;
    }
}