package toolbox.util.typecast;

import java.lang.ref.SoftReference;
import java.util.Map;

import toolbox.util.collections.IdentityWeakReference;
import toolbox.util.collections.ReferenceHashMap;

/**
 * An IdentityCache which uses a ReferenceMap and a IdentityWeakReference as the keys
 * to store objects.  Since the IdentityWeakReference key uses the object reference 
 * to implement hashCode() and equals(), all objects are stored based for
 * each instance.
 *  
 * The values stored within the ReferenceMap are SoftReferences, and 
 * allows the values to reference the key and still be garbage collected
 * as resources are demanded.
 *
 * @see WeakIdentity
 */
public class WeakIdentityCache implements IdentityCache
{

    private Map cache =
        new ReferenceHashMap(IdentityWeakReference.REFERENCE_FACTORY);

    public WeakIdentityCache()
    {
    }

    // IDENTITYCACHE 

    public Object get(Object identity)
    {
        SoftReference ref = (SoftReference) cache.get(identity);
        return ref == null ? null : ref.get();
    }

    public void put(Object identity, Object castTo)
    {
        cache.put(identity, new SoftReference(castTo));
    }

}