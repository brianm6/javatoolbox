package toolbox.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;

import toolbox.util.service.Enableable;

/**
 * A {@link java.io.OutputStream} that filters out non-printable characters
 * and replaces them with a printable one. The tab and newline characters are
 * exempt from filtering. This implementation also has an on/off switch to
 * enable or disable the filtering at runtime via the setEnabled() method.
 * <p>
 * <b>Example:</b>
 * <pre class="snippet">
 * 
 * StringOutputStream sos = new StringOutputStream();
 * PrintableOutputStream pos = new PrintableOutputStream(sos, true, ".");
 * 
 * // Assume we've got some binary data.
 * pos.write(binaryByteArray);  
 * pos.close();
 * 
 * // Prints out all ASCII chars with nonprintable chars replaced with a period. 
 * System.out.println(sos.toString()); 
 * </pre>
 * 
 * @see java.io.OutputStream
 */
public class PrintableOutputStream extends FilterOutputStream 
    implements Enableable
{
    // TODO: Allow exempt characters to be specified at runtime.
    // TODO: Allow customizable replacement characters with a map.
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Replacement string for characters that are discarded.
     */
    private String substitute_;
    
    /**
     * Flag for enabling/disabling the printable character criteria. When
     * not enabled, this stream is effectively a transparent passthrough.
     */
    private boolean enabled_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a PrintableOutputStream that is enabled by default and discards
     * all non-printable characters.
     * 
     * @param os OutputStream to filter.
     */
    public PrintableOutputStream(OutputStream os)
    {
        this(os, true, "");
    }
    

    /**
     * Creates a PrintableOutputStream.
     * 
     * @param os OutputStream to filter.
     * @param enabled Enables the filter.
     * @param substitute Replacement character. Null to discard the filtered
     *        characters.
     */
    public PrintableOutputStream(
        OutputStream os, 
        boolean enabled, 
        String substitute)
    {
        super(os);
        setEnabled(enabled);
        setSubstitute(substitute);
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
        else if (!StringUtils.isEmpty(substitute_))
            out.write(substitute_.getBytes()); 
    }
    
    //--------------------------------------------------------------------------
    // Enabeable Interface
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
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the replacement string. Set to the empty string to discard 
     * completely.
     *  
     * @param substitute The non-printable character replacement string.
     */
    public void setSubstitute(String substitute)
    {
        substitute_ = substitute;
    }
    
    
    /**
     * Returns the substitute string for non-printable characters.
     * 
     * @return String
     */
    public String getSubstitute()
    {
        return substitute_;
    }
}