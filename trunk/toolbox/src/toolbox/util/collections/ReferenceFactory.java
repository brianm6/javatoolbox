package toolbox.util.collections;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

/**
 * A factory for creating a key used within
 * a ReferenceHashMap.  Returning a WeakReference
 * would give the same functionality as a WeakHashMap.
 * 
 * @see ReferenceHashMap
 * @see com.swa.turbo.util.WeakIdentity
 */
public interface ReferenceFactory
{

    /**
     * Creates a Reference key for searching in 
     * a ReferenceHashMap.  This key is not stored
     * within the ReferenceHashMap.
     * 
     */
    public Reference create(Object key);

    /**
     * Create a Reference key which is stored in a
     * ReferenceHashMap.  This returned valued is stored
     * within the ReferenceHashMap.
     */
    public Reference create(Object key, ReferenceQueue q);

}
