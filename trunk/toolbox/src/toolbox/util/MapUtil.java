package toolbox.util;

import java.util.Iterator;
import java.util.Map;

import toolbox.util.collections.AsMap;

/**
 * Utility functionality for Maps.
 */
public final class MapUtil
{
    // Clover private constructor workaround
    static { new MapUtil(); }

    /**
     * Private constructor.
     */
    private MapUtil()
    {
    }
    
    
    /**
     * Alternative toString() implementation that displays map entries, one
     * per line.
     * 
     * @param map Map to stringify
     * @return String representing map's contents
     */
    public static String toString(Map map)
    {
        StringBuffer sb = new StringBuffer();
        
        for (Iterator i = map.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();
            sb.append(entry.getKey() + " : " + entry.getValue() + "\n");
        }
        
        return sb.toString();
    }
    
    
    /**
     * Alternative toString() implementation that displays map entries, one
     * per line.
     * 
     * @param map Map to stringify
     * @return String representing map's contents
     */
    public static String toStringVerbose(Map map)
    {
        StringBuffer sb = new StringBuffer();
        
        for (Iterator i = map.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();
            sb.append("Key= " + entry.getKey() + " \nValue= " + AsMap.of(entry.getValue()) + "\n");
        }
        
        return sb.toString();
    }
}
