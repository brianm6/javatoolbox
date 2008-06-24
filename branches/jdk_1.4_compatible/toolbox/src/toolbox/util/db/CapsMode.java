package toolbox.util.db;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang.Validate;

import toolbox.util.AbstractConstant;

/**
 * CapsMode represents one of three capitalization modes.
 * <ol>
 *  <li>Upper case
 *  <li>Lower case
 *  <li>Preserve case
 * </ol>
 * This class is used by SQLFormatter to specify capitalization preferences for
 * various sql keywords and names. A converter for commons-beanutils,
 * CapsModeConverter, is also included to aid in passivation and hydration of
 * this object to and from XML.
 * 
 * @see toolbox.util.db.SQLFormatter
 * @see toolbox.util.db.CapsModePropertyEditor
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
    
    /**
     * Maps a string its corresponding CapsMode. Used for hydration from XML.
     */
    public static final Map map_;
    
    //--------------------------------------------------------------------------
    // Static Initializer
    //--------------------------------------------------------------------------
    
    static
    {
        // Initialize the map
        map_ = new HashMap(3);
        map_.put(UPPERCASE.toString(), UPPERCASE);
        map_.put(LOWERCASE.toString(), LOWERCASE);
        map_.put(PRESERVE.toString(), PRESERVE);
        
        // Register converter for BeanUtils
        ConvertUtils.register(new CapsModeConverter(), CapsMode.class);
    }
    
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
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the CapsMode that maps to the given string.
     * 
     * @param s String value as obtained from toString()
     * @return CapsMode
     * @throws IllegalArgumentException if invalid string.
     */
    public static CapsMode toCapsMode(String s) 
    {
       CapsMode capsMode = (CapsMode) map_.get(s);
       
       Validate.isTrue(
           capsMode != null, 
           "Invalid capitalization mode '" + s + "'");

       return capsMode;
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
    
    //--------------------------------------------------------------------------
    // Converter Interface
    //--------------------------------------------------------------------------
 
    static class CapsModeConverter implements Converter
    {
        /**
         * @see org.apache.commons.beanutils.Converter#convert(
         *      java.lang.Class, java.lang.Object)
         */
        public Object convert(Class type, Object value)
        {
            Validate.isTrue(type == CapsMode.class, "Cannot convert " + type);
            return toCapsMode(value.toString());
        }
    }
}