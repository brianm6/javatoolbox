package toolbox.util.db;

import toolbox.util.AbstractConstant;

/**
 * CapsMode.
 */
public class CapsMode extends AbstractConstant
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Uppercase mode.
     */
    public static final CapsMode UPPERCASE = new CapsMode("upper");
    
    /**
     * Lowercase mode.
     */
    public static final CapsMode LOWERCASE = new CapsMode("lower");
        
    /**
     * Preserve case mode.
     */
    public static final CapsMode PRESERVE = new CapsMode("preserve");
    
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