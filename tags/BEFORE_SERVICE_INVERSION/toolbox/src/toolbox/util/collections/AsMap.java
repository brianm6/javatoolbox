package toolbox.util.collections;

import java.util.Map;

import toolbox.util.typecast.Typecast;

/**
 * Used to convert an object into a Map.
 * <p>
 * Example:
 * <pre class="snippet">
 *  Map map = AsMap.of(someObject);
 * 
 *  // The toString() implementation dumps the objects attributes.
 *  // Very nice for debugging.
 *  System.out.println(map.toString());
 * 
 * </pre>
 */
public final class AsMap
{
    //--------------------------------------------------------------------------
    // Static
    //--------------------------------------------------------------------------

    /**
     * Instance.
     */
    public static final Typecast TYPECAST = new Typecast(Map.class);

    static
    {
        TYPECAST.addSearchPackage(AsMap.class);
    }


    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Maps an object.
     * 
     * @param object Object to map.
     * @return Map of object.
     */
    public static Map of(Object object)
    {
        return of(object, null);
    }


    /**
     * Maps an object.
     * 
     * @param object Object to map.
     * @param policy Mapping policy.
     * @return Map of object.
     */
    public static Map of(Object object, String policy)
    {
        return (Map) TYPECAST.coerce(object, policy);
    }
}