package toolbox.util.collections;

import java.lang.ref.*;

/**
 * A WeakReference which can be used within a hash table
 * as the identity of an object (i.e. object reference).
 * 
 * Two IdentityWeakReference(s) are the same if there underlying object
 * have the same object reference (i.e. object1 == object2 )
 * 
 * @see #REFERENCE_FACTORY
 * @see ReferenceMap
 */
public class IdentityWeakReference extends WeakReference
{

    /**
     * Used within a ReferenceMap to create keys
     * of IdentityWeakReference(s).
     * <p>
     * Example:
     * <pre>
     *   // Create a map who's keys are based on IdentityWeakReference
     *   Map map = new ReferenceMap( IdentityWeakReference.REFERENCE_FACTORY );
     * </pre>
     */
    public static final ReferenceFactory REFERENCE_FACTORY =
        new ReferenceFactory()
    {

            // REFERENCEFACTORY METHODS
    public Reference create(Object k)
        {
            return k == null ? null : new IdentityWeakReference(k);
        }

        public Reference create(Object k, ReferenceQueue q)
        {
            return k == null ? null : new IdentityWeakReference(k, q);
        }
    };

    private int hash;

    /**
     * Hashcode of key, stored here since the key
     * may be tossed by the GC
     */
    public IdentityWeakReference(Object k)
    {
        super(k);
        hash = System.identityHashCode(k);
    }

    public IdentityWeakReference(Object k, ReferenceQueue q)
    {
        super(k, q);
        hash = System.identityHashCode(k);
    }

    // STANDARD METHODS

    /**
     * Determines equality by the object references
     * pointed to by each <tt>Reference</tt>.<p>
     * 
     * <pre>return this.get() == ((IdentityWeakReference) o2).get();</pre>
     */
    public boolean equals(Object o2)
    {
        if (this == o2)
            return true;

        if (o2 == null || !getClass().equals(o2.getClass()))
            return false;

        // Check Object Reference Only
        return this.get() == ((IdentityWeakReference) o2).get();
    }

    /**
     * The System.identityHashCode(..) of the object referenced
     */
    public int hashCode()
    {
        return hash;
    }

}