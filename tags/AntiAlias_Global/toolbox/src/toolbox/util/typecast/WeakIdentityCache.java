package toolbox.util.typecast;

import java.lang.ref.SoftReference;
import java.util.Map;

import toolbox.util.collections.IdentityWeakReference;
import toolbox.util.collections.ReferenceHashMap;

/**
 * An IdentityCache which uses a ReferenceMap and a IdentityWeakReference as 
 * the keys to store objects.  Since the IdentityWeakReference key uses the 
 * object reference to implement hashCode() and equals(), all objects are 
 * stored based for each instance.
 * <p>  
 * The values stored within the ReferenceMap are SoftReferences, and 
 * allows the values to reference the key and still be garbage collected
 * as resources are demanded.
 */
public class WeakIdentityCache implements IdentityCache
{

    private Map cache_ =
        new ReferenceHashMap(IdentityWeakReference.REFERENCE_FACTORY);

    /**
     * Default constructor
     */
    public WeakIdentityCache()
    {
    }

    // IDENTITYCACHE 

    /**
     * Gets from cache
     * 
     * @param  identity  Identity
     * @return Value
     */
    public Object get(Object identity)
    {
        SoftReference ref = (SoftReference) cache_.get(identity);
        return ref == null ? null : ref.get();
    }

    /**
     * Puts into the cache
     * 
     * @param  identity  Identity
     * @param  castTo    Object to castTo
     */
    public void put(Object identity, Object castTo)
    {
        cache_.put(identity, new SoftReference(castTo));
    }

}