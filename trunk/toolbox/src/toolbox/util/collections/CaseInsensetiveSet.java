package toolbox.util.collections;

import java.util.Set;

import org.apache.commons.collections.set.AbstractSetDecorator;

/**
 * A set that enforces String uniqueness regardless of case. This is ideal for 
 * a set of Strings but can also be applied to arbitrary objects. In this case, 
 * the result of <code>object.toString()<code> is used.  
 * <p>
 * Example:
 * <pre>
 *
 * Set s = new CaseInsensetiveSet(new HashSet());
 * s.add("abc");
 *
 * if (s.contains("abc"))
 *    System.out.println("Found abc");
 *
 * if (s.contains("ABC"))
 *    System.out.println("Found ABC");
 *
 * if (s.remove("aBc"))
 *    System.out.println("Removed aBc");
 * 
 * if (s.isEmpty())
 *    System.out.println("Set is empty"); 
 *
 * </pre>
 */
public class CaseInsensetiveSet extends AbstractSetDecorator
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a CaseInsensetiveSet.
     * 
     * @param set Set to decorate.
     */
    public CaseInsensetiveSet(Set set)
    {
        super(set);
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.util.Set
    //--------------------------------------------------------------------------
    
    /**
     * Adds the objects string representation to the set only if a
     * case-insensetive form does not already exist in the set.
     * 
     * @see java.util.Set#add(java.lang.Object)
     */
    public boolean add(Object o)
    {
        return super.add(o.toString().toLowerCase());
    }


    /**
     * Determines if the objects string representation exists in the set in
     * case-insensetive form. 
     * 
     * @see java.util.Set#contains(java.lang.Object)
     */
    public boolean contains(Object o)
    {
        return super.contains(o.toString().toLowerCase());
    }
    
    
    /**
     * Removes the objects string representation from the set only if it exists 
     * in the set in case-insensetive form. 
 
     * @see java.util.Set#remove(java.lang.Object)
     */
    public boolean remove(Object o)
    {
        return super.remove(o.toString().toLowerCase());
    }
}