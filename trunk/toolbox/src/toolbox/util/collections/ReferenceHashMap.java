package toolbox.util.collections;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * This class is similar to the WeakHashMap, except that the
 * keys used are created by the ReferenceFactory.  A ReferenceFactory
 * that returned a WeakReference would be the equivalent of a
 * WeakHashMap.
 * 
 * @see ReferenceFactory
 */
public class ReferenceHashMap extends AbstractMap implements Map
{
    /** 
     * Hash table mapping WeakKeys to values 
     */
    private Map hash_;
    
    /**
     *  Reference queue for cleared WeakKeys 
     */
    private ReferenceQueue queue_ = new ReferenceQueue();

    /**
     * Refernce factory
     */
    private ReferenceFactory factory_ = null;
    
    /**
     * Entry set
     */
    private Set entrySet_ = null;


    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor
     * 
     * @param  factory          Factory
     * @param  initialCapacity  Initial capacity
     * @param  loadFactor       Load factor
     */
    public ReferenceHashMap(ReferenceFactory factory, int initialCapacity,
        float loadFactor)
    {
        hash_ = new HashMap(initialCapacity, loadFactor);
        factory_ = factory;
    }

    /**
     * Constructor
     * 
     * @param  factory          Factory
     * @param  initialCapacity  Initial capacity
     */
    public ReferenceHashMap(ReferenceFactory factory, int initialCapacity)
    {
        hash_ = new HashMap(initialCapacity);
        factory_ = factory;
    }

    /**
     * Constructor
     * 
     * @param  factory          Factory
     */
    public ReferenceHashMap(ReferenceFactory factory)
    {
        hash_ = new HashMap();
        factory_ = factory;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * @return  Size of map
     */ 
    public int size()
    {
        return entrySet().size();
    }

    /**
     * @return  True if map is empty, false otherwise
     */
    public boolean isEmpty()
    {
        return entrySet().isEmpty();
    }

    /**
     * Checks if key is in the map
     * 
     * @param   key  Key to check for existence
     * @return  True if the map contains the key, false otherwise
     */
    public boolean containsKey(Object key)
    {
        return hash_.containsKey(factory_.create(key));
    }

    /**
     * Retrieves an object from the map
     * 
     * @param   key  Key of object to retrieve
     * @return  Object matching key, null if not found
     */
    public Object get(Object key)
    {
        return hash_.get(factory_.create(key));
    }

    /**
     * Puts an object in the map
     * 
     * @param  key    Key of object
     * @param  value  Value of object
     * @return Object
     */
    public Object put(Object key, Object value)
    {
        processQueue();
        return hash_.put(factory_.create(key, queue_), value);
    }

    /**
     * Removes an object from the map
     * 
     * @param  key  Key of object to remove
     * @return Removed object
     */
    public Object remove(Object key)
    {
        processQueue();
        return hash_.remove(factory_.create(key));
    }

    /**
     * Clears the map
     */
    public void clear()
    {
        processQueue();
        hash_.clear();
    }

    /**
     * @return Set view of the mappings in this map.
     */
    public Set entrySet()
    {
        if (entrySet_ == null)
            entrySet_ = new EntrySet();

        return entrySet_;
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Remove all invalidated entries from the map, that is, remove all entries
     * whose keys have been discarded.  This method should be invoked once by
     * each public mutator in this class.  We don't invoke this method in
     * public accessors because that can lead to surprising
     * ConcurrentModificationExceptions.
     */
    private void processQueue()
    {
        Object key;
        
        while ((key = queue_.poll()) != null)
            hash_.remove(key);
    }


    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------

    /**
     * Internal class for entries
     */
    private static class Entry implements Map.Entry
    {
        private Map.Entry ent_;
        private Object key_;

        /**
         * Strong reference to key, so that the GC
         * will leave it alone as long as this Entry
         * exists
         */
        Entry(Map.Entry ent, Object key)
        {
            ent_ = ent;
            key_ = key;
        }

        public Object getKey()
        {
            return key_;
        }

        public Object getValue()
        {
            return ent_.getValue();
        }

        public Object setValue(Object value)
        {
            return ent_.setValue(value);
        }

        private static boolean valEquals(Object o1, Object o2)
        {
            return (o1 == null) ? (o2 == null) : o1.equals(o2);
        }

        public boolean equals(Object o)
        {
            if (!(o instanceof Map.Entry))
                return false;

            Map.Entry e = (Map.Entry) o;
            return (valEquals(key_, e.getKey()) && 
                valEquals(getValue(), e.getValue()));
        }

        public int hashCode()
        {
            Object v;
            return (((key_ == null) ? 0 : key_.hashCode()) ^ 
                (((v = getValue()) == null) ? 0 : v.hashCode()));
        }
    }

    /**
     * Internal class for entry sets 
     */
    private class EntrySet extends AbstractSet
    {
        private Set hashEntrySet_ = hash_.entrySet();

        public Iterator iterator()
        {
            return new Iterator()
            {
                Iterator hashIterator = hashEntrySet_.iterator();
                Entry next = null;

                public boolean hasNext()
                {
                    while (hashIterator.hasNext())
                    {
                        Map.Entry ent = (Map.Entry) hashIterator.next();
                        Reference ref = (Reference) ent.getKey();
                        Object k = null;

                        if ((ref != null) && ((k = ref.get()) == null))
                        {
                            // Weak key has been cleared by GC
                            continue;
                        }

                        next = new Entry(ent, k);
                        return true;
                    }

                    return false;
                }

                public Object next()
                {
                    if ((next == null) && !hasNext())
                        throw new NoSuchElementException();

                    Entry e = next;
                    next = null;
                    return e;
                }

                public void remove()
                {
                    hashIterator.remove();
                }

            };
        }

        public boolean isEmpty()
        {
            return !(iterator().hasNext());
        }

        public int size()
        {
            int j = 0;

            for (Iterator i = iterator(); i.hasNext(); i.next())
                j++;

            return j;
        }

        public boolean remove(Object o)
        {
            processQueue();

            if (!(o instanceof Map.Entry))
                return false;

            Map.Entry e = (Map.Entry) o;
            Object ev = e.getValue();
            Reference key = factory_.create(e.getKey());
            Object hv = hash_.get(key);

            if ((hv == null) ? 
               ((ev == null) && hash_.containsKey(key)) : hv.equals(ev))
            {
                hash_.remove(key);
                return true;
            }

            return false;
        }

        public int hashCode()
        {
            int h = 0;

            for (Iterator i = hashEntrySet_.iterator(); i.hasNext();)
            {
                Map.Entry ent = (Map.Entry) i.next();
                Reference key = (Reference) ent.getKey();
                Object v;

                if (key == null)
                    continue;

                h += (key.hashCode() ^ 
                    (((v = ent.getValue()) == null) ? 0 : v.hashCode()));
            }

            return h;
        }

    }
}