package toolbox.util.collections;

import java.util.Map;

import toolbox.util.typecast.Typecast;

/**
 * Used to convert an object into a Map
 * <pre>
 * 
 * Example:
 *      Map map = AsMap.of(someObject);
 * 
 * </pre>
 */
public final class AsMap
{
    /** Instance */
    public static final Typecast TYPECAST = new Typecast(Map.class);

    static 
    {
        TYPECAST.addSearchPackage(AsMap.class);
    }

    /**
     * Maps an object
     * 
     * @param  object  Object to map 
     * @return Map of object
     */
    public static Map of(Object object)
    {
        return of(object, null);
    }

    /**
     * Maps an object
     * 
     * @param  object  Object to map
     * @param  policy  Mapping policy
     * @return Map of object
     */
    public static Map of(Object object, String policy)
    {
        return (Map) TYPECAST.coerce(object, policy);
    }
}
