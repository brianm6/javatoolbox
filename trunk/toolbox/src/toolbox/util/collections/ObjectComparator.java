package toolbox.util.collections;

import java.util.Arrays;
import java.util.Comparator;

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
 *      new ObjectComparator.Property( 
 *          "lastName", false, String.CASE_INSENSITIVE_ORDER ),
 *      new ObjectComparator.Property( 
 *          "firstName", true, String.CASE_INSENSITIVE_ORDER ) );
 * 
 *   Collections.sort( people, oc );
 * </pre>
 * 
 * @see ObjectMap
 * @author Steven Lee
 */
public class ObjectComparator implements Comparator, java.io.Serializable
{

    private Property[] props_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * @param propertyName The property to compare or a period separated list 
     *                     of properties (e.g. address.line1 )
     */
    public ObjectComparator(String propertyName)
    {
        this(new Property(propertyName));
    }

    /**
     * @param  propertyName  Property name
     * @param  comparator    Comparator
     */
    public ObjectComparator(String propertyName, Comparator comparator)
    {
        this(new Property(propertyName, false, comparator));
    }

    /**
     * @param  propertyName  Property name
     * @param  reverseOrder  Reverses the order
     */
    public ObjectComparator(String propertyName, boolean reverseOrder)
    {
        this(new Property[] { new Property(propertyName, reverseOrder)});
    }

    /**
     * @param  property1  Property one
     * @param  property2  Property two
     */
    public ObjectComparator(String property1, String property2)
    {
        this(new Property[] 
            { new Property(property1), new Property(property2)});
    }

    /**
     * @param  property1  Property 1
     * @param  property2  Property 2
     * @param  property3  Property 3
     */
    public ObjectComparator(String property1,String property2, String property3)
    {
        this(new Property[] {
                new Property(property1),
                new Property(property2),
                new Property(property3)});
    }

    /**
     * @param  prop  Property
     */
    public ObjectComparator(Property prop)
    {
        this(new Property[] { prop });
    }

    /**
     * @param  prop1  Property 1
     * @param  prop2  Property 2
     */
    public ObjectComparator(Property prop1, Property prop2)
    {
        this(new Property[] { prop1, prop2 });
    }

    /**
     * @param  properties  Properties
     */
    public ObjectComparator(Property[] properties)
    {
        this.props_ = properties;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Compares two objects
     * 
     * @param  o1  First object
     * @param  o2  Second object
     * @return Zero if equals, -1 
     *         -1   if o1 lessthan o2 
     *          1   if o1 greaterthan o2
     */
    public int compare(Object o1, Object o2)
    {
        Object c1 = null, c2 = null;
        int rVal = 0;
        boolean reverseOrder = false;

        for (int i = 0; rVal == 0 && i < props_.length; i++)
        {
            c1 = getProperty(o1, props_[i].name_);
            c2 = getProperty(o2, props_[i].name_);

            // Atleast One of the values are null
            if (c1 == null || c2 == null)
            {
                rVal = (c2 == null ? 0 : 1) - (c1 == null ? 0 : 1);
                break;
            }
            else if (props_[i].comparator_ == null)
            {
                rVal = ((Comparable) c1).compareTo(c2);
            }
            else
            {
                rVal = props_[i].comparator_.compare(c1, c2);
            }

            reverseOrder = props_[i].reverseOrder_;
        }

        // If the Object compares identically and they are different
        // return the difference of System.identityHashCode
        if (rVal == 0 && !o1.equals(o2))
            rVal = System.identityHashCode(o1) - System.identityHashCode(o2);

        return reverseOrder ? -rVal : rVal;
    }


    /**
     * Test for equality
     * 
     * @param  obj  Object to test
     * @return True if equal, false otherwise
     */
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (obj == null || !obj.getClass().equals(this.getClass()))
            return false;

        return Arrays.equals(
            this.props_,
            ((ObjectComparator) obj).props_);
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
    public static class Property implements java.io.Serializable
    {
        private String name_;
        private boolean reverseOrder_;
        private Comparator comparator_;

        /**
         * @param  name  Property name
         */
        public Property(String name)
        {
            this(name, false);
        }

        /**
         * Constructor
         * 
         * @param  name         Property name
         * @param  reverseOrder Flip order
         */
        public Property(String name, boolean reverseOrder)
        {
            this(name, reverseOrder, null);
        }

        /**
         * Constructor
         * 
         * @param  name         Name
         * @param  comparator   Comparator
         */
        public Property(String name, Comparator comparator)
        {
            this(name, false, comparator);
        }

        /**
         * Constructor
         * 
         * @param  name             Property name
         * @param  reverseOrder     Flip order
         * @param  comparator       Comparator
         */
        public Property(String name, boolean reverseOrder, 
            Comparator comparator)
        {
            name_ = name;
            reverseOrder_ = reverseOrder;
            comparator_ = comparator;
        }

        /**
         * Equals
         * 
         * @param  obj  Object
         * @return TRue or false
         */
        public boolean equals(Object obj)
        {
            if (obj == this)
                return true;

            if (obj == null || !obj.getClass().equals(this.getClass()))
                return false;

            Property p = (Property) obj;

            return (name_ == p.name_ || 
                (name_ != null && name_.equals(p.name_))) && 
                (reverseOrder_ == p.reverseOrder_) && 
                (comparator_ == p.comparator_ || 
                (comparator_ != null && comparator_.equals(p.comparator_)));
        }
    }

}