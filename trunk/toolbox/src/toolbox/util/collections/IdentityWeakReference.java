package toolbox.util.collections;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * A WeakReference which can be used within a hash table as the identity of an
 * object. (i.e. object reference)
 * <p>
 * Two IdentityWeakReference(s) are the same if there underlying object have
 * the same object reference. (ex: object1 == object2)
 * 
 * @see #REFERENCE_FACTORY
 * @see ReferenceHashMap
 */
public class IdentityWeakReference extends WeakReference
{
    private int hash_;

    /**
	 * Used within a ReferenceMap to create keys of IdentityWeakReference(s).
	 * <p>
	 * Example:
	 * <pre>
	 * // Create a map who's keys are based on IdentityWeakReference 
     * Map map = new ReferenceMap( IdentityWeakReference.REFERENCE_FACTORY );
	 * </pre>
	 */
    public static final ReferenceFactory REFERENCE_FACTORY =
        new ReferenceFactory()
    {
        public Reference create(Object k)
        {
            return k == null ? null : new IdentityWeakReference(k);
        }

        public Reference create(Object k, ReferenceQueue q)
        {
            return k == null ? null : new IdentityWeakReference(k, q);
        }
    };

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
	 * Hashcode of key, stored here since the key may be tossed by the GC
	 * 
	 * @param k Key
	 */
    public IdentityWeakReference(Object k)
    {
        super(k);
        hash_ = System.identityHashCode(k);
    }

    
    /**
	 * Creates a new IdentityWeakReference
	 * 
	 * @param k Key
	 * @param q Reference queue
	 */
    public IdentityWeakReference(Object k, ReferenceQueue q)
    {
        super(k, q);
        hash_ = System.identityHashCode(k);
    }

    //--------------------------------------------------------------------------
    //  Overrides java.lang.Object
    //--------------------------------------------------------------------------

    /**
	 * Determines equality by the object references pointed to by each 
     * <tt>Reference</tt>.<p>
	 * <pre>
	 *  return this.get() == ((IdentityWeakReference) o2).get();
	 * </pre>
	 * 
	 * @param o2 Object to compare
	 * @return True if equals, false otherwise
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
	 * The System.identityHashCode(..) of the object referenced.
	 * 
	 * @return hashCode
	 */
    public int hashCode()
    {
        return hash_;
    }
}
