package toolbox.util.db;

import toolbox.util.AbstractConstant;

/**
 * Capitalization modes for the SQLFormatter.
 * 
 * @see toolbox.util.db.SQLFormatter
 */
public class CapsMode extends AbstractConstant
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Uppercase mode.
     */
    public static final CapsMode UPPERCASE = new CapsMode("Upper case");
    
    /**
     * Lowercase mode.
     */
    public static final CapsMode LOWERCASE = new CapsMode("Lower case");
        
    /**
     * Preserve case mode.
     */
    public static final CapsMode PRESERVE = new CapsMode("Preserve case");
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Caps mode.
     */
    private String capsMode_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent construction.
     * 
     * @param capsMode Caps mode.
     */
    private CapsMode(String capsMode)
    {
        capsMode_ = capsMode;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Returns caps mode in string form.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return capsMode_;
    }
}