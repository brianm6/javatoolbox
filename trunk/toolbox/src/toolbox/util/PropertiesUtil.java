package toolbox.util;

import java.util.Properties;

/**
 * Convenience methods dealing with properties
 */
public final class PropertiesUtil
{
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Prevent construction
     */
    private PropertiesUtil()
    {
    }
    
    //--------------------------------------------------------------------------
    //  Static Methods
    //--------------------------------------------------------------------------
    
    /**
     * Gets a boolean value from a properties object. The case of the string
     * is irrelevant.
     * 
     * @param  props         Properties to retrieve boolean from
     * @param  name          Name of the property
     * @param  defaultValue  Default value if property is not present or invalid
     * @return True          if property represents the string "true". 
     *          False         if the  property represents the string "false". 
     *          Default value if the property is non-existant or an error is
     *                        encountered.
     */
    public static boolean getBoolean(Properties props, String name, 
        boolean defaultValue)
    {
        boolean b = defaultValue;
        
        String value = props.getProperty(name);
        
        if (value != null)
        {
            value = value.trim().toLowerCase();
            
            if (value.equals("true"))
                b = true;
            else if (value.equals("false"))
                b = false;
        }
        
        return b;
    }   
    
    /**
     * Sets a boolean property in a properties object
     * 
     * @param  props  Properties to set property in
     * @param  name   Name of the property
     * @param  value  Value to set
     */
    public static void setBoolean(Properties props, String name, 
        boolean value)
    {
        props.setProperty(name, value + "");   
    }
    
    /**
     * Gets an signed integer value from a properties object.
     * 
     * @param  props         Properties to retrieve integer from
     * @param  name          Name of the property
     * @param  defaultValue  Default value if property is not present 
     *                        or invalid
     * @return Integer if property exists and is a valid integer, default
     *          value otherwise. 
     */
    public static int getInteger(
        Properties props, String name, int defaultValue)
    {
        int i = defaultValue;
        String value = props.getProperty(name);
        
        if (value != null)
        {
            try
            {
                i = Integer.parseInt(value);
            }
            catch (NumberFormatException nfe)
            {
                // return default value
            }
        }
        
        return i;
    }   
    
    /**
     * Sets an integer property in a properties object
     * 
     * @param  props  Properties to set property in
     * @param  name   Name of the property
     * @param  value  Value to set
     */
    public static void setInteger(Properties props, String name, int value)
    {
        props.setProperty(name, value + "");   
    }
}
