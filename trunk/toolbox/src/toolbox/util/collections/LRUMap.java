package toolbox.util.collections;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 *                iterating over <tt>entrySet()</tt>.  
 *       <li>calling <tt>containsValue()</tt> and there is a match
 *   </ol>
 * </ol>
 * 
 * @todo   Use an ObjectPool for LRUKey
 * @author Steven Lee
 */
public class LRUMap implements Map, Serializable
{
    /**
     * No time limit
     */
    public static final int NO_TIME_LIMIT = 0;

    protected static final int DEFAULT_MAX_SIZE = 1000;

    private Map map_;
    private List keys_;

    private int maxSize_;
    private long timeLimit_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Constructs an LRUMap with a size of 1000 and no time limit.
     */
    public LRUMap()
    {
        this(DEFAULT_MAX_SIZE);
    }

    /**
     * Constructs an LRUMap with a size of <tt>maxSize</tt> with no time limit.
     * 
     * @param  maxSize  Max size
     */
    public LRUMap(int maxSize)
    {
        this(maxSize, NO_TIME_LIMIT);
    }

    /**
     * Constructs an LRUMap with a size of <tt>maxSize</tt> with a time limit of
     * <tt>timeLimit</tt> in milliseconds.
     * 
     * @param  maxSize      Max size
     * @param  timeLimit    Time limit 
     */
    public LRUMap(int maxSize, long timeLimit)
    {
        this(maxSize, timeLimit, new HashMap());
    }

    /**
     * Constructs an LRUMap with a size of <tt>maxSize</tt> with a time limit of
     * <tt>timeLimit</tt> in milliseconds and using <tt>backingMap</tt> to store
     * the data.
     * 
     * @param maxSize    Maximum size of this map
     * @param timeLimit  Time limit in milliseconds that a key should exist
     * @param backingMap Map used to store the key and values for the LRUMap
     */
    public LRUMap(int maxSize, long timeLimit, Map backingMap)
    {
        maxSize_   = maxSize;
        timeLimit_ = timeLimit;
        map_       = backingMap;
        keys_      = new LinkedList();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * @return  Max size
     */
    public int getMaxSize()
    {
        return maxSize_;
    }

    /**
     * Sets max size
     * 
     * @param  maxSize  Max size
     */
    public void setMaxSize(int maxSize)
    {
        maxSize_ = maxSize;
        update();
    }

    /**
     * @return Time limit
     */
    public long getTimeLimit()
    {
        return timeLimit_;
    }

    /**
     * Sets time limit
     * 
     * @param  timeLimit  Time limit
     */
    public void setTimeLimit(long timeLimit)
    {
        timeLimit_ = timeLimit;
        update();
    }

    /**
     * Checks for if the LRUMap is above the max size or any of the elements
     * have exceeded the maximum time limit.
     */
    public void update()
    {
        // Reset Size
        while (keys_.size() > getMaxSize())
        {
            map_.remove(keys_.remove(0));
        }

        // Check Time
        if (timeLimit_ > 0)
        {
            long time = System.currentTimeMillis();
            
            for (Iterator i = keys_.iterator(); i.hasNext();)
            {
                LRUKey key = (LRUKey) i.next();
                
                if (time - key.getTime() > timeLimit_)
                {
                    i.remove();
                    map_.remove(key);
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
        map_.clear();
        keys_.clear();
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.
     * 
     * <b>NOTE:</b> If the key is found, then the timestamp for that entry
     * will be updated.
     *
     * @param  key  Key whose presence in this map is to be tested.
     * @return True if this map contains a mapping for the specified key
     */
    public boolean containsKey(Object key)
    {
        update();
        return map_.containsKey(new LRUKey(key));
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
            Iterator i = map_.entrySet().iterator();

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
    public Set entrySet()
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
     *           <tt>null</tt> if the map contains no mapping for this key.
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

            return map_.get(lruKey);
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
        return map_.isEmpty();
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
    public Set keySet()
    {
        update();
        return new KeySet();
    }

    /**
     * Associates the specified value with the specified key in this map
     * (optional operation).  If the map previously contained a mapping for
     * this key, the old value is replaced.
     *
     * @param   key    Key with which the specified value is to be associated.
     * @param   value  Value to be associated with the specified key.
     * @return  Object
     * 
     * Previous value associated with specified key, or <tt>null</tt>
     * if there was no mapping for key.  A <tt>null</tt> return can also
     * indicate that the map previously associated <tt>null</tt> with the
     * specified key, if the implementation supports <tt>null</tt> values.
     * 
     * @throws UnsupportedOperationException if the <tt>put</tt> operation is
     *         not supported by this map.
     * @throws ClassCastException if the class of the specified key or value
     *         prevents it from being stored in this map.
     * @throws IllegalArgumentException if some aspect of this key or value
     *         prevents it from being stored in this map.
     * @throws NullPointerException this map does not permit <tt>null</tt>
     *         keys or values, and the specified key or value is
     *         <tt>null</tt>.
     */
    public Object put(Object key, Object value) 
        throws UnsupportedOperationException, ClassCastException,
               IllegalArgumentException, NullPointerException
    {
        try
        {
            LRUKey lruKey = new LRUKey(key);

            if (map_.containsKey(lruKey))
            {
                updateKey(lruKey);
            }
            else
            {
                keys_.add(lruKey);
            }

            return map_.put(lruKey, value);
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
     * @param  t   Mappings to be stored in this map.
     * @throws UnsupportedOperationException if the <tt>putAll</tt> method is
     *         not  supported by this map.
     * @throws ClassCastException if the class of a key or value in the
     *         specified  map prevents it from being stored in this map.
     * @throws IllegalArgumentException some aspect of a key or value in the
     *         specified  map prevents it from being stored in this map.
     * @throws NullPointerException this map does not permit <tt>null</tt>
     *         keys  or values, and the specified key or value is
     *         <tt>null</tt>.
     */
    public void putAll(Map t)
        throws UnsupportedOperationException, ClassCastException,
               IllegalArgumentException, NullPointerException
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
     *           if there was no mapping for key.  A <tt>null</tt> return can
     *           also indicate that the map previously associated <tt>null</tt>
     *           with the specified key, if the implementation supports
     *           <tt>null</tt> values.
     * @throws UnsupportedOperationException if the <tt>remove</tt> method is
     *         not supported by this map.
     */
    public Object remove(Object key) throws UnsupportedOperationException    
    {
        try
        {
            LRUKey lruKey = new LRUKey(key);
            keys_.remove(lruKey);
            return map_.remove(lruKey);
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
        return map_.size();
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
        return map_.values();
    }

    // STANDARD METHODS

    /**
     * Returns a String that represents the value of this object.
     * @return a string representation of the receiver
     */
    public String toString()
    {
        update();
        return map_.toString();
    }

    /**
     * @param   o  Object to test for equality
     * @return  True if equal, false otherwise
     */
    public boolean equals(Object o)
    {
        return map_.equals(o);
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
        return map_.hashCode();
    }

    // HELPER METHODS

    protected void updateKey(LRUKey key)
    {
        if (map_.containsKey(key))
        {
            // get reference to key and remove
            key = (LRUKey) keys_.remove(keys_.indexOf(key));
            key.update();
            keys_.add(key);
        }
    }

    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Least Recently Used Key
     */
    class LRUKey implements Serializable
    {
        private Object key_;
        private long time_;

        LRUKey(Object key)
        {
            key_ = key;
            update();
        }

        long getTime()
        {
            return time_;
        }

        void update()
        {
            time_ = System.currentTimeMillis();
        }

        // STANDARD METHODS

        public int hashCode()
        {
            return key_.hashCode();
        }

        public boolean equals(Object object)
        {
            if (object == null)
                return false;

            // OBJECT IS AN LRUKey
            if (object instanceof LRUKey)
            {
                LRUKey lruKey = (LRUKey) object;
                boolean match = this.equals(lruKey.key_);
                // UPDATE KEY, SINCE IT IS BEING ACCESSED
                if (match)
                    lruKey.update();

                return match;
            }

            // OBJECT IS NOT AN LRUKey
            boolean match = this.key_.equals(object);
            if (match)
                this.update();
            return match;
        }

        public String toString()
        {
            return key_.toString();
        }

    }

    /**
     * Map entry
     */
    class Entry implements Map.Entry, Serializable
    {
        private Map.Entry entry_;

        Entry(Map.Entry entry)
        {
            entry_ = entry;
        }

        public Object setValue(Object value)
        {
            return entry_.setValue(value);
        }

        public Object getValue()
        {
            // Move key to bottom of keys
            updateKey((LRUKey) entry_.getKey());
            return entry_.getValue();
        }

        public Object getKey()
        {
            return ((LRUKey) entry_.getKey()).key_;
        }
    }

    /**
     * Entry set
     */
    class EntrySet extends AbstractSet
    {
        private Set entrySet_;

        public EntrySet()
        {
            this.entrySet_ = map_.entrySet();
        }

        public Iterator iterator()
        {
            return new Iterator()
            {
                Iterator iEntrySet = entrySet_.iterator();

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
            return entrySet_.size();
        }
    }

    /**
     * Key set
     */
    class KeySet extends AbstractSet
    {
        private Set keySet_ = map_.keySet();

        public java.util.Iterator iterator()
        {
            return new Iterator()
            {
                Iterator iKeySet = keySet_.iterator();

                public boolean hasNext()
                {
                    return iKeySet.hasNext();
                }

                public Object next()
                {
                    return ((LRUKey) iKeySet.next()).key_;
                }

                public void remove()
                {
                    iKeySet.remove();
                }
            };
        }

        public int size()
        {
            return keySet_.size();
        }
    }
}
