package toolbox.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;

/**
 * A {@link java.io.OutputStream} that filters out non-printable characters
 * and replaces them with a printable one. The tab and newline characters are
 * exempt from filtering.
 * <p>
 * Example:
 * <pre>
 * 
 * StringOutputStream sos = new StringOutputStream();
 * PrintableOutputStream pos = new PrintableOutputStream(sos, true, ".");
 * 
 * // Assume we've got some binary data.
 * pos.write(binaryByteArray);  
 * pos.close();
 * 
 * // Prints out all ASCII chars with binary chars replaced with a period. 
 * System.out.println(sos.toString()); 
 * </pre>
 * 
 * @see java.io.OutputStream
 */
public class PrintableOutputStream extends FilterOutputStream
{
    // TODO: Allow exempt characters to be specified at runtime.
    // TODO: Allow customizable replacement characters with a map.
    
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
     * Filters out non-printable characters.
     * 
     * @see java.io.FilterOutputStream#write(int)
     */
    public void write(int b) throws IOException
    {
        char c = (char) b;
        
        if (!enabled_)
            super.write(b);
        else if ((b >=  32 && b <= 126) || c == '\n' || c == '\t') 
            super.write(b);
        else if (!StringUtils.isEmpty(replacement_))
            out.write(replacement_.getBytes());  // Is this a NO NO?
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns true if filtering is enabled, false otherwise.
     * 
     * @return boolean
     */
    public boolean isEnabled()
    {
        return enabled_;
    }
    
    
    /**
     * Sets the enabled state of the filter.
     * 
     * @param enabled The enabled to set.
     */
    public void setEnabled(boolean enabled)
    {
        enabled_ = enabled;
    }
    
    
    /**
     * Returns the replacement string for non-printable characters. 
     * 
     * @return String
     */
    public String getReplacement()
    {
        return replacement_;
    }
    
    
    /**
     * Sets the replacement string. Set to the empty string to discard 
     * completely.
     *  
     * @param replacement The non-printable character replacement string.
     */
    public void setReplacement(String replacement)
    {
        replacement_ = replacement;
    }
}