package toolbox.util.collections;

import java.util.Map;

import toolbox.util.typecast.Typecast;

/**
 * Used to convert an object into a Map
 * <pre>
 * Example:
 *      Map map = AsMap.of( someObject );
 * </pre>
 */
public class AsMap
{
    public static final Typecast TYPECAST = new Typecast(Map.class);

    static 
    {
        TYPECAST.addSearchPackage(AsMap.class);
    }

    public static Map of(Object object)
    {
        return of(object, null);
    }

    public static Map of(Object object, String policy)
    {
        return (Map) TYPECAST.coerce(object, policy);
    }
}
