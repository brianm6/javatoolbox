package toolbox.util.collections;

import java.util.*;

/**
 * A Comparator which compares a properties of objects.
 *
 * The property returned from each bean must implement Comparable
 * or have a custom Comparator for that object.<p>
 * 
 * Example:
 * <pre>
 *   Comparator oc;
 *   List people = ...;
 * 
 *   // Sort people with lastName and then firstName
 *   oc = new ObjectComparator( "lastName", "firstName" );
 *   Collections.sort( people, oc );
 * 
 *   // Do a case insentive sort on lastName, firstName
 *   oc = new ObjectComparator( "lastName", String.CASE_INSENSITIVE_ORDER );
 *   Collections.sort( people, oc );
 * 
 *   // Do a case insentive sort on lastName ascending, firstName descending
 *   oc = new ObjectComparator(
 *             new ObjectComparator.Property( "lastName", false, String.CASE_INSENSITIVE_ORDER ),
 *             new ObjectComparator.Property( "firstName", true, String.CASE_INSENSITIVE_ORDER ) );
 *   Collections.sort( people, oc );
 * </pre>
 * 
 * @see ObjectMap
 * @author Steven Lee
 */
public class ObjectComparator implements Comparator, java.io.Serializable
{

    protected Property[] properties;

    /**
     * @param propName the property to compare or a period separated list of properties
     *                 (e.g. address.line1 )
     */
    public ObjectComparator(String propertyName)
    {
        this(new Property(propertyName));
    }

    public ObjectComparator(String propertyName, Comparator comparator)
    {
        this(new Property(propertyName, false, comparator));
    }

    public ObjectComparator(String propertyName, boolean reverseOrder)
    {
        this(new Property[] { new Property(propertyName, reverseOrder)});
    }

    public ObjectComparator(String property1, String property2)
    {
        this(
            new Property[] { new Property(property1), new Property(property2)});
    }

    public ObjectComparator(
        String property1,
        String property2,
        String property3)
    {
        this(
            new Property[] {
                new Property(property1),
                new Property(property2),
                new Property(property3)});
    }

    public ObjectComparator(Property prop)
    {
        this(new Property[] { prop });
    }

    public ObjectComparator(Property prop1, Property prop2)
    {
        this(new Property[] { prop1, prop2 });
    }

    public ObjectComparator(Property[] properties)
    {
        this.properties = properties;
    }

    public int compare(Object o1, Object o2)
    {
        Object c1 = null, c2 = null;
        int rVal = 0;
        boolean reverseOrder = false;

        for (int i = 0; rVal == 0 && i < properties.length; i++)
        {
            c1 = getProperty(o1, properties[i].name);
            c2 = getProperty(o2, properties[i].name);

            // Atleast One of the values are null
            if (c1 == null || c2 == null)
            {
                rVal = (c2 == null ? 0 : 1) - (c1 == null ? 0 : 1);
                break;
            }
            else if (properties[i].comparator == null)
            {
                rVal = ((Comparable) c1).compareTo(c2);
            }
            else
            {
                rVal = properties[i].comparator.compare(c1, c2);
            }

            reverseOrder = properties[i].reverseOrder;
        }

        // If the Object compares identically and they are different
        // return the difference of System.identityHashCode
        if (rVal == 0 && !o1.equals(o2))
            rVal = System.identityHashCode(o1) - System.identityHashCode(o2);

        return reverseOrder ? -rVal : rVal;
    }

    // STANDARD METHODS

    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (obj == null || !obj.getClass().equals(this.getClass()))
            return false;

        return Arrays.equals(
            this.properties,
            ((ObjectComparator) obj).properties);
    }

    // HELPER METHODS

    protected Object getProperty(Object obj, Object property)
    {
        return AsMap.of(obj).get(property);
    }

    // INNER CLASSES 	

    /**
     * A description of the property to compare.  If a Comparator
     * is not provided, the property should implement Comparable.
     */
    static public class Property implements java.io.Serializable
    {
        protected String name;
        protected boolean reverseOrder;
        protected Comparator comparator;

        public Property(String name)
        {
            this(name, false);
        }

        public Property(String name, boolean reverseOrder)
        {
            this(name, reverseOrder, null);
        }

        public Property(String name, Comparator comparator)
        {
            this(name, false, comparator);
        }

        public Property(
            String name,
            boolean reverseOrder,
            Comparator comparator)
        {
            this.name = name;
            this.reverseOrder = reverseOrder;
            this.comparator = comparator;
        }

        public boolean equals(Object obj)
        {
            if (obj == this)
                return true;

            if (obj == null || !obj.getClass().equals(this.getClass()))
                return false;

            Property p = (Property) obj;

            return (name == p.name || (name != null && name.equals(p.name)))
                && (reverseOrder == p.reverseOrder)
                && (comparator == p.comparator
                    || (comparator != null && comparator.equals(p.comparator)));
        }
    }

}