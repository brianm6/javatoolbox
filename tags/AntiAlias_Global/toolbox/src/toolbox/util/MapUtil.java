package toolbox.util;

import java.util.Iterator;
import java.util.Map;

/**
 * Utility functionality for Maps
 */
public class MapUtil
{
    // Clover private constructor workaround
    static { new MapUtil(); }

    /**
     * Constructor for MapUtil.
     */
    private MapUtil()
    {
    }
    
    /**
     * Alternative toString() implementation that displays map entries, one
     * per line
     * 
     * @param   map  Map to stringify
     * @return  String representing map's contents
     */
    public static String toString(Map map)
    {
        StringBuffer sb = new StringBuffer();
        
        for (Iterator i = map.entrySet().iterator(); i.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) i.next();
            sb.append(entry.getKey() + " : " + entry.getValue() + "\n");
        }
        
        return sb.toString();
    }

}
