package toolbox.util.collections;

import java.util.*;

/**
 * A Least Recently Used (LRU) Map which can be constrained by time
 * and/or size.  The LRUMap is purged of "old" data everytime it is 
 * changed.  A key value pair's timestamp is considered "new" when:
 * 
 * <ol>
 *   <li>the key value pair is added (e.g. put() )
 *   <li>the key is found (e.g. containsKey(), get() )
 *   <li>the value is specifically accessed by
 *   <ol>
 *       <li><tt>Entry.getValue()</tt> when
 *       		 iterating over <tt>entrySet()</tt>.  
 *       <li>calling <tt>containsValue()</tt> and there is a match
 *   </ol>
 * </ol>
 * 
 * @todo use an ObjectPool for LRUKey
 * @author Steven Lee
 */
public class LRUMap implements java.util.Map, java.io.Serializable
{
    public static final int NO_TIME_LIMIT = 0;

    protected static final int DEFAULT_MAX_SIZE = 1000;

    protected Map map;
    protected List keys;

    protected int maxSize;
    protected long timeLimit;

    // CONSTRUCTORS

    /**
     * Constructs an LRUMap with a size of 1000
     * and no time limit.
     */
    public LRUMap()
    {
        this(DEFAULT_MAX_SIZE);
    }

    /**
     * Constructs an LRUMap with a size of <tt>maxSize</tt>
     * with no time limit.
     */
    public LRUMap(int maxSize)
    {
        this(maxSize, NO_TIME_LIMIT);
    }

    /**
     * Constructs an LRUMap with a size of <tt>maxSize</tt>
     * with a time limit of <tt>timeLimit</tt> in milliseconds.
     */
    public LRUMap(int maxSize, long timeLimit)
    {
        this(maxSize, timeLimit, new HashMap());
    }

    /**
     * Constructs an LRUMap with a size of <tt>maxSize</tt>
     * with a time limit of <tt>timeLimit</tt> in milliseconds
     * and using <tt>backingMap</tt> to store the data.
     * 
     * @param maxSize the maximum size of this map
     * @param timeLimit the time limit in milliseconds that a key
     *                  should exist
     * @param backingMap the Map used to store the key and values
     *                   for the LRUMap
     */
    public LRUMap(int maxSize, long timeLimit, Map backingMap)
    {
        this.maxSize = maxSize;
        this.timeLimit = timeLimit;
        this.map = backingMap;
        this.keys = new LinkedList();
    }

    // API

    public int getMaxSize()
    {
        return maxSize;
    }

    public void setMaxSize(int maxSize)
    {
        this.maxSize = maxSize;
        update();
    }

    public long getTimeLimit()
    {
        return timeLimit;
    }

    public void setTimeLimit(long timeLimit)
    {
        this.timeLimit = timeLimit;
        update();
    }

    /**
     * Checks for if the LRUMap is above the max size or
     * any of the elements have exceeded the maximum
     * time limit.
     */
    public void update()
    {
        // Reset Size
        while (keys.size() > getMaxSize())
        {
            map.remove(keys.remove(0));
        }

        // Check Time
        if (timeLimit > 0)
        {
            long time = System.currentTimeMillis();
            for (Iterator i = keys.iterator(); i.hasNext();)
            {
                LRUKey key = (LRUKey) i.next();
                if (time - key.getTime() > timeLimit)
                {
                    i.remove();
                    map.remove(key);
                }
                else
                {
                    break;
                }
            }
        }
    }

    // MAP METHODS

    /**
     * Removes all mappings from this map (optional operation).
     */
    public void clear()
    {
        map.clear();
        keys.clear();
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.
     * 
     * <b>NOTE:</b> If the key is found, then the timestamp for that entry
     * will be updated.
     *
     * @param key key whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map contains a mapping for the specified
     * key.
     * 
     * @throws ClassCastException if the key is of an inappropriate type for
     * 		  this map.
     * @throws NullPointerException if the key is <tt>null</tt> and this map
     *            does not not permit <tt>null</tt> keys.
     */
    public boolean containsKey(Object key)
    {
        update();
        return map.containsKey(new LRUKey(key));
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.  More formally, returns <tt>true</tt> if and only if
     * this map contains at least one mapping to a value <tt>v</tt> such that
     * <tt>(value==null ? v==null : value.equals(v))</tt>.  This operation
     * will probably require time linear in the map size for most
     * implementations of the <tt>Map</tt> interface.
     * <p>
     * <b>NOTE:</b> Alter the timestamp of all entries with <tt>value</tt>
     *
     * @param value value whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value.
     */
    public boolean containsValue(Object value)
    {
        try
        {
            boolean rVal = false;
            Iterator i = map.entrySet().iterator();

            if (value == null)
            {
                while (i.hasNext())
                {
                    Map.Entry e = (Map.Entry) i.next();
                    if (e.getValue() == null)
                    {
                        updateKey((LRUKey) e.getKey());
                        rVal = true;
                    }
                }
            }
            else
            {
                while (i.hasNext())
                {
                    Map.Entry e = (Map.Entry) i.next();
                    if (value.equals(e.getValue()))
                    {
                        updateKey((LRUKey) e.getKey());
                        rVal = true;
                    }
                }
            }

            return rVal;
        }
        finally
        {
            update();
        }
    }

    /**
     * Returns a set view of the mappings contained in this map.  Each element
     * in the returned set is a <tt>Map.Entry</tt>.  The set is backed by the
     * map, so changes to the map are reflected in the set, and vice-versa.
     * If the map is modified while an iteration over the set is in progress,
     * the results of the iteration are undefined.  The set supports element
     * removal, which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not support
     * the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * <b>NOTE:</b> If the value of an entry is accessed, then the timestamp 
     * for that entry will be updated.
     * 
     * @return a set view of the mappings contained in this map.
     */
    public java.util.Set entrySet()
    {
        update();
        return new EntrySet();
    }

    /**
     * Returns the value to which this map maps the specified key.  Returns
     * <tt>null</tt> if the map contains no mapping for this key.  A return
     * value of <tt>null</tt> does not <i>necessarily</i> indicate that the
     * map contains no mapping for the key; it's also possible that the map
     * explicitly maps the key to <tt>null</tt>.  The <tt>containsKey</tt>
     * operation may be used to distinguish these two cases.
     *
     * <b>NOTE:</b> If the key is found, then the timestamp for that entry
     * will be updated.
     * 
     * @param key key whose associated value is to be returned.
     * @return the value to which this map maps the specified key, or
     *	       <tt>null</tt> if the map contains no mapping for this key.
     * 
     * @throws ClassCastException if the key is of an inappropriate type for
     * 		  this map.
     * @throws NullPointerException key is <tt>null</tt> and this map does not
     *		  not permit <tt>null</tt> keys.
     * 
     * @see #containsKey(Object)
     */
    public Object get(Object key)
    {
        try
        {
            LRUKey lruKey =
                (key instanceof LRUKey) ? (LRUKey) key : new LRUKey(key);
            updateKey(lruKey);

            return map.get(lruKey);
        }
        finally
        {
            update();
        }
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings.
     */
    public boolean isEmpty()
    {
        update();
        return map.isEmpty();
    }

    /**
     * Returns a set view of the keys contained in this map.  The set is
     * backed by the map, so changes to the map are reflected in the set, and
     * vice-versa.  If the map is modified while an iteration over the set is
     * in progress, the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding mapping from
     * the map, via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt> <tt>retainAll</tt>, and <tt>clear</tt> operations.
     * It does not support the add or <tt>addAll</tt> operations.
     *
     * @return a set view of the keys contained in this map.
     */
    public java.util.Set keySet()
    {
        update();
        return new KeySet();
    }

    /**
     * Associates the specified value with the specified key in this map
     * (optional operation).  If the map previously contained a mapping for
     * this key, the old value is replaced.
     *
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return previous value associated with specified key, or <tt>null</tt>
     *	       if there was no mapping for key.  A <tt>null</tt> return can
     *	       also indicate that the map previously associated <tt>null</tt>
     *	       with the specified key, if the implementation supports
     *	       <tt>null</tt> values.
     * 
     * @throws UnsupportedOperationException if the <tt>put</tt> operation is
     *	          not supported by this map.
     * @throws ClassCastException if the class of the specified key or value
     * 	          prevents it from being stored in this map.
     * @throws IllegalArgumentException if some aspect of this key or value
     *	          prevents it from being stored in this map.
     * @throws NullPointerException this map does not permit <tt>null</tt>
     *            keys or values, and the specified key or value is
     *            <tt>null</tt>.
     */
    public Object put(Object key, Object value)
    {
        try
        {
            LRUKey lruKey = new LRUKey(key);

            if (map.containsKey(lruKey))
            {
                updateKey(lruKey);
            }
            else
            {
                keys.add(lruKey);
            }

            return map.put(lruKey, value);
        }
        finally
        {
            update();
        }
    }

    /**
     * Copies all of the mappings from the specified map to this map
     * (optional operation).  These mappings will replace any mappings that
     * this map had for any of the keys currently in the specified map.
     *
     * @param t Mappings to be stored in this map.
     * 
     * @throws UnsupportedOperationException if the <tt>putAll</tt> method is
     * 		  not supported by this map.
     * 
     * @throws ClassCastException if the class of a key or value in the
     * 	          specified map prevents it from being stored in this map.
     * 
     * @throws IllegalArgumentException some aspect of a key or value in the
     *	          specified map prevents it from being stored in this map.
     * 
     * @throws NullPointerException this map does not permit <tt>null</tt>
     *            keys or values, and the specified key or value is
     *            <tt>null</tt>.
     */
    public void putAll(java.util.Map t)
    {
        for (Iterator i = t.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Removes the mapping for this key from this map if present (optional
     * operation).
     *
     * @param key key whose mapping is to be removed from the map.
     * @return previous value associated with specified key, or <tt>null</tt>
     *	       if there was no mapping for key.  A <tt>null</tt> return can
     *	       also indicate that the map previously associated <tt>null</tt>
     *	       with the specified key, if the implementation supports
     *	       <tt>null</tt> values.
     * @throws UnsupportedOperationException if the <tt>remove</tt> method is
     *         not supported by this map.
     */
    public Object remove(Object key)
    {
        try
        {
            LRUKey lruKey = new LRUKey(key);
            keys.remove(lruKey);
            return map.remove(lruKey);
        }
        finally
        {
            update();
        }
    }

    /**
     * Returns the number of key-value mappings in this map.  If the
     * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of key-value mappings in this map.
     */
    public int size()
    {
        update();
        return map.size();
    }

    /**
     * Returns a collection view of the values contained in this map.  The
     * collection is backed by the map, so changes to the map are reflected in
     * the collection, and vice-versa.  If the map is modified while an
     * iteration over the collection is in progress, the results of the
     * iteration are undefined.  The collection supports element removal,
     * which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt> and <tt>clear</tt> operations.
     * It does not support the add or <tt>addAll</tt> operations.
     * <p>
     * 
     * <b>NOTE</b>: Does not Update the timestamp for the entry
     * 
     * @return a collection view of the values contained in this map.
     */
    public java.util.Collection values()
    {
        update();
        return map.values();
    }

    // STANDARD METHODS

    /**
     * Returns a String that represents the value of this object.
     * @return a string representation of the receiver
     */
    public String toString()
    {
        update();
        return map.toString();
    }

    public boolean equals(Object o)
    {
        return map.equals(o);
    }

    /**
     * Returns the hash code value for this map.  The hash code of a map is
     * defined to be the sum of the hash codes of each entry in the map's
     * <tt>entrySet()</tt> view.  This ensures that <tt>t1.equals(t2)</tt>
     * implies that <tt>t1.hashCode()==t2.hashCode()</tt> for any two maps
     * <tt>t1</tt> and <tt>t2</tt>, as required by the general contract of
     * Object.hashCode.<p>
     *
     * This implementation iterates over <tt>entrySet()</tt>, calling
     * <tt>hashCode</tt> on each element (entry) in the Collection, and adding
     * up the results.
     *
     * @return the hash code value for this map.
     * @see Map.Entry#hashCode()
     * @see Object#hashCode()
     * @see Object#equals(Object)
     * @see Set#equals(Object)
     */
    public int hashCode()
    {
        return map.hashCode();
    }

    // HELPER METHODS

    protected void updateKey(LRUKey key)
    {
        if (map.containsKey(key))
        {
            // get reference to key and remove
            key = (LRUKey) keys.remove(keys.indexOf(key));
            key.update();
            keys.add(key);
        }
    }

    /////////////////////////////////////////////////////////////////
    // INNER CLASSES

    class LRUKey implements java.io.Serializable
    {
        Object key;
        long time;

        LRUKey(Object key)
        {
            this.key = key;
            update();
        }

        long getTime()
        {
            return time;
        }

        void update()
        {
            time = System.currentTimeMillis();
        }

        // STANDARD METHODS

        public int hashCode()
        {
            return key.hashCode();
        }

        public boolean equals(Object object)
        {
            if (object == null)
                return false;

            // OBJECT IS AN LRUKey
            if (object instanceof LRUKey)
            {
                LRUKey lruKey = (LRUKey) object;
                boolean match = this.equals(lruKey.key);
                // UPDATE KEY, SINCE IT IS BEING ACCESSED
                if (match)
                    lruKey.update();

                return match;
            }

            // OBJECT IS NOT AN LRUKey
            boolean match = this.key.equals(object);
            if (match)
                this.update();
            return match;
        }

        public String toString()
        {
            return key.toString();
        }

    }

    class Entry implements Map.Entry, java.io.Serializable
    {
        Map.Entry entry;

        Entry(Map.Entry entry)
        {
            this.entry = entry;
        }

        public Object setValue(Object value)
        {
            return entry.setValue(value);
        }

        public Object getValue()
        {
            // Move key to bottom of keys
            updateKey((LRUKey) entry.getKey());
            return entry.getValue();
        }

        public Object getKey()
        {
            return ((LRUKey) entry.getKey()).key;
        }
    }

    class EntrySet extends java.util.AbstractSet
    {
        Set entrySet;

        public EntrySet()
        {
            this.entrySet = map.entrySet();
        }

        public java.util.Iterator iterator()
        {
            return new Iterator()
            {
                Iterator iEntrySet = entrySet.iterator();

                public boolean hasNext()
                {
                    return iEntrySet.hasNext();
                }

                public Object next()
                {
                    return new Entry((Map.Entry) iEntrySet.next());
                }

                public void remove()
                {
                    iEntrySet.remove();
                }
            };
        }

        public int size()
        {
            return entrySet.size();
        }
    }

    class KeySet extends java.util.AbstractSet
    {
        Set keySet = map.keySet();

        public java.util.Iterator iterator()
        {
            return new Iterator()
            {
                Iterator iKeySet = keySet.iterator();

                public boolean hasNext()
                {
                    return iKeySet.hasNext();
                }

                public Object next()
                {
                    return ((LRUKey) iKeySet.next()).key;
                }

                public void remove()
                {
                    iKeySet.remove();
                }
            };
        }

        public int size()
        {
            return keySet.size();
        }
    }

}