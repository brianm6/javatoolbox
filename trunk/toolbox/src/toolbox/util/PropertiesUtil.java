package toolbox.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Properties Utility Class
 */
public final class PropertiesUtil
{
    // Clover private constructor workaround
    static { new PropertiesUtil(); }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Prevent construction
     */
    private PropertiesUtil()
    {
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Gets a boolean value from a properties object. The case of the string
     * is irrelevant.
     * 
     * @param  props         Properties to retrieve boolean from
     * @param  name          Name of the property
     * @param  defaultValue  Default value if property is not present or invalid
     * @return True          if property represents the string "true". 
     *         False         if the  property represents the string "false". 
     *         Default value if the property is non-existant or an error is
     *                       encountered.
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
     *                       or invalid
     * @return Integer if property exists and is a valid integer, default
     *         value otherwise. 
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
                ; // return default value
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

    /**
     * Returns string of sorted properties for easy inspection
     * 
     * @param   props  Properties to output to a string
     * @return  List of sorted properties with their values, one per line
     */    
    public static String toString(Properties props)
    {
        List keyList = new ArrayList(props.keySet());
        Collections.sort(keyList);
        
        int max = 0;

        // Find the longest key for padding
        for (Iterator i = keyList.iterator(); i.hasNext(); )
        {
            String key = (String) i.next();
            if (key.length() > max)
                max = key.length();
        }

        StringBuffer sb = new StringBuffer();
        
                
        for (Iterator i = keyList.iterator(); i.hasNext(); )
        {
            String key = (String) i.next();
            String value = props.getProperty(key);
            sb.append(key + StringUtil.repeat(" ", max-key.length()) + 
                " = " + value + "\n");
        }
             
        return sb.toString();
    }
}