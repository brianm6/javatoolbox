package toolbox.util.collections;

import java.util.*;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

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
    /** Hash table mapping WeakKeys to values */
    private Map hash;
    /** Reference queue for cleared WeakKeys */
    private ReferenceQueue queue = new ReferenceQueue();

    private ReferenceFactory factory = null;
    private Set entrySet = null;

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
        while ((key = queue.poll()) != null)
            hash.remove(key);
    }

    /* -- Constructors -- */
    public ReferenceHashMap(
        ReferenceFactory factory,
        int initialCapacity,
        float loadFactor)
    {
        hash = new HashMap(initialCapacity, loadFactor);
        this.factory = factory;
    }

    public ReferenceHashMap(ReferenceFactory factory, int initialCapacity)
    {
        hash = new HashMap(initialCapacity);
        this.factory = factory;
    }

    public ReferenceHashMap(ReferenceFactory factory)
    {
        hash = new HashMap();
        this.factory = factory;
    }

    // Simple queries

    public int size()
    {
        return entrySet().size();
    }

    public boolean isEmpty()
    {
        return entrySet().isEmpty();
    }

    public boolean containsKey(Object key)
    {
        return hash.containsKey(factory.create(key));
    }

    // Lookup and modification operations

    public Object get(Object key)
    {
        return hash.get(factory.create(key));
    }

    public Object put(Object key, Object value)
    {
        processQueue();
        return hash.put(factory.create(key, queue), value);
    }

    public Object remove(Object key)
    {
        processQueue();
        return hash.remove(factory.create(key));
    }

    public void clear()
    {
        processQueue();
        hash.clear();
    }

    // INNER CLASSES

    /* Internal class for entries */

    static private class Entry implements Map.Entry
    {
        private Map.Entry ent;
        private Object key;

        /**
         * Strong reference to key, so that the GC
         * will leave it alone as long as this Entry
         * exists
         */
        Entry(Map.Entry ent, Object key)
        {
            this.ent = ent;
            this.key = key;
        }

        public Object getKey()
        {
            return key;
        }

        public Object getValue()
        {
            return ent.getValue();
        }

        public Object setValue(Object value)
        {
            return ent.setValue(value);
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
            return (
                valEquals(key, e.getKey())
                    && valEquals(getValue(), e.getValue()));
        }

        public int hashCode()
        {
            Object v;
            return (
                ((key == null) ? 0 : key.hashCode())
                    ^ (((v = getValue()) == null) ? 0 : v.hashCode()));
        }

    }

    /* Internal class for entry sets */
    private class EntrySet extends AbstractSet
    {
        Set hashEntrySet = hash.entrySet();

        public Iterator iterator()
        {
            return new Iterator()
            {
                Iterator hashIterator = hashEntrySet.iterator();
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
                            /* Weak key has been cleared by GC */
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
            Reference key = factory.create(e.getKey());
            Object hv = hash.get(key);

            if ((hv == null)
                ? ((ev == null) && hash.containsKey(key))
                : hv.equals(ev))
            {
                hash.remove(key);
                return true;
            }

            return false;
        }

        public int hashCode()
        {
            int h = 0;

            for (Iterator i = hashEntrySet.iterator(); i.hasNext();)
            {
                Map.Entry ent = (Map.Entry) i.next();
                Reference key = (Reference) ent.getKey();
                Object v;

                if (key == null)
                    continue;

                h
                    += (key.hashCode()
                        ^ (((v = ent.getValue()) == null) ? 0 : v.hashCode()));
            }

            return h;
        }

    }

    /**
     * Returns a <code>Set</code> view of the mappings in this map.
     */
    public Set entrySet()
    {
        if (entrySet == null)
            entrySet = new EntrySet();

        return entrySet;
    }

}
