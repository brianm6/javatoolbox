package toolbox.util.collections;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;

import toolbox.util.StringUtil;

/**
 * Converts an Object into a Map.  The keys are the properties 
 * (get/set method names) and the values are the result of calling
 * the corresponding get method.
 * 
 * <pre>
 * Example:
 *   Person person = new Person();
 *   ...
 *   Map map = new ObjectMap( person );
 * 
 *   // Prints person.getFirstName()
 *   System.out.println( map.get( "firstName" ) );
 * 
 *   // Calls person.setFirstName( "Steven" );
 *   map.put( "firstName", "Steven" );
 * 
 *   // Prints person.getAddress().getStreet1()
 *   System.out.println( map.get( "address.street1" ) );
 * </pre>
 * 
 * @author Steven Lee
 */
public class ObjectMap
    extends AbstractMap
    implements java.io.Serializable, Cloneable
{
    private static Map CACHED_PDS =
        Collections.synchronizedMap(new WeakHashMap(100));

    private static final String[] TRUE_VALUES =
        { "on", "true", "activate", "yes", "y", "t", "1" };

    private static final Set NUMBER_CLASSES = new HashSet();

    static {
        NUMBER_CLASSES.add(Byte.class);
        NUMBER_CLASSES.add(Byte.TYPE);
        NUMBER_CLASSES.add(Short.class);
        NUMBER_CLASSES.add(Short.TYPE);
        NUMBER_CLASSES.add(Integer.class);
        NUMBER_CLASSES.add(Integer.TYPE);
        NUMBER_CLASSES.add(Float.class);
        NUMBER_CLASSES.add(Float.TYPE);
        NUMBER_CLASSES.add(Long.class);
        NUMBER_CLASSES.add(Long.TYPE);
        NUMBER_CLASSES.add(Double.class);
        NUMBER_CLASSES.add(Double.TYPE);
    }

    protected Object bean;
    protected transient WeakReference wBean;
    protected transient ObjectEntrySet entrySet;
    protected boolean silent;

    // CONSTRUCTORS

    /**
     * Constructs a Map which wraps a <tt>bean</tt>.  If an exception
     * is thrown by the bean, then a MapInvocationException can
     * be thrown by most of the <tt>Map</tt> methods.  The <tt>bean</tt>
     * is held onto by a strong reference.
     * 
     * @param bean the object to convert to a <tt>Map</tt>.
     */
    public ObjectMap(Object bean) throws IntrospectionException
    {
        this(bean, false);
    }

    /**
     * Constructs a Map which wraps a <tt>bean</tt>.  If an exception
     * is thrown by the bean, then a MapInvocationException can
     * be thrown by most of the <tt>Map</tt> methods when silent is false.  
     * The <tt>bean</tt> is held onto by a strong reference.
     * 
     * @param bean the object to convert to a <tt>Map</tt>.
     * @param silent true - exceptions thrown by bean are ignored<br>
     *               false - MapInvocationTargetException will be thrown
     *                       if the bean throws an exception
     */
    public ObjectMap(Object bean, boolean silent) throws IntrospectionException
    {
        this(bean, silent, true);
    }

    /**
     * Constructs a Map which wraps a <tt>bean</tt>.  If an exception
     * is thrown by the bean, then a MapInvocationException can
     * be thrown by most of the <tt>Map</tt> methods when silent is false.  
     * The <tt>bean</tt> is held onto by a strong reference if
     * <tt>useWeakRef</tt> is false.
     * 
     * @param bean the object to convert to a <tt>Map</tt>.
     * @param silent true - exceptions thrown by bean are ignored<br>
     *               false - MapInvocationTargetException will be thrown
     *                       if the bean throws an exception
     * @param useWeakRef true - holds onto the bean using a WeakReference
     * 					 false - holds onto the bean using a strong reference
     */
    public ObjectMap(Object bean, boolean silent, boolean useWeakRef)
        throws IntrospectionException
    {
        if (useWeakRef)
            this.wBean = new WeakReference(bean);
        else
            this.bean = bean;

        this.silent = silent;
        this.entrySet = new ObjectEntrySet();
    }

    public Object getBean()
    {
        return isBeanReferenceWeak() ? wBean.get() : bean;
    }

    // MAP METHODS 

    public boolean containsKey(Object key) throws MapInvocationTargetException
    {
        if (key != null)
        {
            String str = (String) key.toString();
            int index = str.indexOf('.');
            if (index > 0)
            {
                String thisKey = str.substring(0, index);
                if (super.containsKey(thisKey))
                {
                    Object value = super.get(thisKey);
                    if (value != null)
                    {
                        try
                        {
                            return asMap(value).containsKey(
                                str.substring(index + 1));
                        }
                        catch (MapInvocationTargetException e)
                        {
                            if (isSilent())
                                return false;
                            throw e;
                        }
                    }
                }
                return false;
            }
        }

        return super.containsKey(key);
    }

    public Object get(Object key) throws MapInvocationTargetException
    {
        if (key != null)
        {
            String str = (String) key.toString();
            int index = str.indexOf('.');

            if (index > 0)
            {
                Object rVal = super.get(str.substring(0, index));
                try
                {
                    return rVal == null
                        ? null
                        : asMap(rVal).get(str.substring(index + 1));
                }
                catch (MapInvocationTargetException e)
                {
                    if (isSilent())
                        return null;
                    throw e;
                }
            }
        }

        return super.get(key);
    }

    /**
     * @throws UnsupportedOperationException if a set method is not found
     * @throws MapInvocationTargetException if an exception is thrown while calling the method
     * @throws IllegalArgumentException if the property is not found
     */
    public Object put(Object key, Object value)
        throws
            UnsupportedOperationException,
            IllegalArgumentException,
            MapInvocationTargetException
    {
        String str = (String) key.toString();
        int index = str.lastIndexOf('.');

        if (index > 0)
        {
            try
            {
                Object obj = get(str.substring(0, index));
                return asMap(obj).put(str.substring(index + 1), value);
            }
            catch (MapInvocationTargetException e)
            {
                if (isSilent())
                    return null;
                throw e;
            }
        }
        else
        {
            for (Iterator i = entrySet.iterator(); i.hasNext();)
            {
                Map.Entry entry = (Map.Entry) i.next();

                if (entry.getKey().equals(key))
                    return entry.setValue(value);
            }

            if (isSilent())
                return null;

            throw new IllegalArgumentException(
                "Property '" + key + "' not found");
        }
    }

    public Set entrySet()
    {
        return entrySet;
    }

    // STANDARD METHODS

    public String toString()
    {
        int max = size() - 1;
        StringBuffer buf = new StringBuffer();


        int maxLen = 0;
        
        // Find max length of the keys so values can be lined up
        for(Iterator i = entrySet().iterator(); i.hasNext(); )
        {
            Entry e = (Entry) i.next();
            
            int currentLen = e.getKey().toString().length();
            if (currentLen > maxLen)
                maxLen = currentLen;
        }       

        Iterator i = entrySet().iterator();
        
        for (int j = 0; j <= max; j++)
        {
            Entry e = (Entry) (i.next());
            try
            {
                buf.append(StringUtil.left(e.getKey().toString(), maxLen) + 
                    ": " + e.getValue());
                    
                if (j < max)
                    buf.append("\n");
            }
            catch (UnsupportedOperationException ignore)
            {
                // Get Method Not Present
            }
        }
        
        return buf.toString();
    }

    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new IllegalStateException(e.getMessage());
        }
    }

    // SERIALIZABLE METHODS

    /**
     * After Serialization the ObjectMap will not use WeakReferences
     * if previously used.
     */
    private void writeObject(ObjectOutputStream s) throws IOException
    {
        WeakReference wBeanSave = wBean;
        boolean useWeak = (wBeanSave != null);

        // Setup ObjectMap to not use WeakReferences
        if (useWeak)
        {
            this.wBean = null;
            this.bean = wBeanSave.get();
        }

        s.defaultWriteObject();

        // Reset Object Back
        if (useWeak)
        {
            this.wBean = wBeanSave;
            this.bean = null;
        }
    }

    /**
     * Reads in the ObjectMap
     */
    private void readObject(ObjectInputStream s)
        throws IOException, ClassNotFoundException
    {
        s.defaultReadObject();

        // transient fields
        try
        {
            this.entrySet = new ObjectEntrySet();
        }
        catch (IntrospectionException e)
        {
            throw new ClassNotFoundException(e.getMessage(), e);
        }
    }

    // HELPER METHODS

    protected boolean isSilent()
    {
        return silent;
    }

    protected boolean isBeanReferenceWeak()
    {
        return wBean != null;
    }

    protected Map asMap(Object object)
    {
        return AsMap.of(object);
    }

    //////////////////////////////////////////////////////////
    // OBJECTENTRYSET

    class ObjectEntrySet extends AbstractSet
    {
        protected PropertyDescriptor[] pds;
        protected MapEntry[] mapEntries;

        public ObjectEntrySet() throws IntrospectionException
        {
            this.pds = getPropertyDescriptors(getBean().getClass());
            this.mapEntries = new MapEntry[pds.length];

            for (int i = 0; i < pds.length; i++)
                this.mapEntries[i] = new MapEntry(i);
        }

        public int size()
        {
            return pds.length;
        }

        public Iterator iterator()
        {
            return new EntryIterator();
        }

        class EntryIterator implements Iterator
        {
            int i = 0;

            public boolean hasNext()
            {
                return i < pds.length;
            }

            public Object next()
            {
                return mapEntries[i++];
            }

            public void remove()
            {
                throw new UnsupportedOperationException();
            }

        }

        class MapEntry implements java.util.Map.Entry
        {
            int index;

            public MapEntry(int index)
            {
                this.index = index;
            }

            // MAPENTRY METHODS

            public Object getKey()
            {
                return pds[index].getName();
            }

            /**
             * @throws MapInvocationTargetException if an exception is thrown while calling the method
             */
            public Object getValue() throws MapInvocationTargetException
            {
                try
                {
                    Method getMethod = pds[index].getReadMethod();
                    if (getMethod == null)
                        throw new UnsupportedOperationException(
                            "get method for '"
                                + pds[index].getName()
                                + "' not found");

                    return getMethod.invoke(getBean(), null);
                }
                catch (RuntimeException e)
                {
                    throw (RuntimeException) e;
                }
                catch (InvocationTargetException e)
                {
                    throw new MapInvocationTargetException(
                        e.getTargetException(),
                        "Error while getting property: "
                            + pds[index].getName());
                }
                catch (Exception e)
                {
                    throw new MapInvocationTargetException(
                        e,
                        "Error while getting property: "
                            + pds[index].getName());
                }
            }

            public int hashCode()
            {
                return getKey().hashCode();
            }

            /**
             * @throws UnsupportedOperationException if a set method is not found
             * @throws MapInvocationTargetException if an exception is thrown while calling the method
             */
            public Object setValue(Object value)
                throws MapInvocationTargetException, UnsupportedOperationException
            {
                Method setMethod = pds[index].getWriteMethod();

                if (setMethod == null)
                    throw new UnsupportedOperationException(
                        "set method for '"
                            + pds[index].getName()
                            + "' not found");

                try
                {
                    Class parmType = setMethod.getParameterTypes()[0];

                    if (value == null)
                    {
                        value = convertString(null, parmType);
                    }
                    else if (!parmType.isAssignableFrom(value.getClass()))
                    {
                        if (value instanceof Number
                            && isNumberConvertable(parmType))
                            value = convertNumber((Number) value, parmType);
                        else
                            value = convertString(value.toString(), parmType);
                    }

                    setMethod.invoke(getBean(), new Object[] { value });
                }
                catch (RuntimeException e)
                {
                    throw (RuntimeException) e;
                }
                catch (InvocationTargetException e)
                {
                    throw new MapInvocationTargetException(
                        e.getTargetException(),
                        "Error while setting property: "
                            + pds[index].getName());
                }
                catch (Exception e)
                {
                    throw new MapInvocationTargetException(
                        e,
                        "Error while setting property: "
                            + pds[index].getName());
                }

                return null;
            }
        }

    }

    // HELPER STATIC MEHTODS

    protected static boolean isNumberConvertable(Class t)
    {
        return NUMBER_CLASSES.contains(t);
    }

    public static Number convertNumber(Number n, Class t)
    {
        if (t.equals(Byte.class) || t.equals(Byte.TYPE))
        {
            return new Byte(n.byteValue());
        }
        else if (t.equals(Short.class) || t.equals(Short.TYPE))
        {
            return new Short(n.shortValue());
        }
        else if (t.equals(Integer.class) || t.equals(Integer.TYPE))
        {
            return new Integer(n.intValue());
        }
        else if (t.equals(Float.class) || t.equals(Float.TYPE))
        {
            return new Float(n.floatValue());
        }
        else if (t.equals(Long.class) || t.equals(Long.TYPE))
        {
            return new Long(n.longValue());
        }
        else if (t.equals(Double.class) || t.equals(Double.TYPE))
        {
            return new Double(n.doubleValue());
        }

        return n;
    }

    public static Object convertString(String s, Class t)
    {
        if (s == null)
        {
            if (t.equals(Boolean.class) || t.equals(Boolean.TYPE))
                s = "false";
            else
                return null;
        }

        if (t.equals(Boolean.class) || t.equals(Boolean.TYPE))
        {
            for (int i = 0; i < TRUE_VALUES.length; i++)
            {
                if (TRUE_VALUES[i].equalsIgnoreCase(s))
                    return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
        else if (t.equals(Byte.class) || t.equals(Byte.TYPE))
        {
            return new Byte(s);
        }
        else if (t.equals(Character.class) || t.equals(Character.TYPE))
        {
            return s.length() > 0 ? new Character(s.charAt(0)) : null;
        }
        else if (t.equals(Short.class) || t.equals(Short.TYPE))
        {
            return new Short(s);
        }
        else if (t.equals(Integer.class) || t.equals(Integer.TYPE))
        {
            return new Integer(s);
        }
        else if (t.equals(Float.class) || t.equals(Float.TYPE))
        {
            return new Float(s);
        }
        else if (t.equals(Long.class) || t.equals(Long.TYPE))
        {
            return new Long(s);
        }
        else if (t.equals(Double.class) || t.equals(Double.TYPE))
        {
            return new Double(s);
        }
        else if (t.equals(String.class))
        {
            return s;
        }
        else if (t.equals(java.io.File.class))
        {
            return new java.io.File(s);
        }

        return s;
    }

    /**
     * Uses a cache of <tt>WeakReference</tt>s to hold onto a 
     * <tt>Class</tt> and <tt>PropertyDescriptor</tt>(s).
     * 
     * <tt>WeakReference</tt>s are used so that the garbage collector can
     * collect a Class and/or it's descriptors when they are no longer used.
     */
    protected static PropertyDescriptor[] getPropertyDescriptors(Class aClass)
        throws IntrospectionException
    {
        WeakReference ref = (WeakReference) CACHED_PDS.get(aClass);
        PropertyDescriptor[] pds;

        if (ref == null || (pds = (PropertyDescriptor[]) ref.get()) == null)
        {
            pds = Introspector.getBeanInfo(aClass).getPropertyDescriptors();
            CACHED_PDS.put(aClass, new WeakReference(pds));
        }

        return pds;
    }

}