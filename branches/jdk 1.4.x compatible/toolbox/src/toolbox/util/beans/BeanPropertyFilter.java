package toolbox.util.beans;

import java.util.Collection;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;

/**
 * Filters java beans using the value of a bean property as the selector. 
 * Filter criteria can be one of the following:
 * <ul>
 *   <li>Single object 
 *   <li>Collection of objects
 *   <li>Array of objects
 * </ul>
 * <p>
 * Usage:
 * <pre class="snippet">
 * // Plain old java bean with two properties, 'first' and 'last' 
 * class Name
 * {
 *     String first;
 *     String last;
 * 
 * 
 *     public String getFirst()
 *     {
 *         return first;
 *     };
 * 
 * 
 *     public void setFirst(Sting s)
 *     {
 *         first = s;
 *     };
 * 
 * 
 *     public String getLast()
 *     {
 *         return last;
 *     };
 * 
 * 
 *     public void setLast(Sting s)
 *     {
 *         last = s;
 *     };
 * }
 * 
 * // Lets filter out all the joe's 
 * Collection names = new ArrayList();
 * names.add(new Name(&quot;joe&quot;, &quot;schmo&quot;));
 * names.add(new Name(&quot;flo&quot;, &quot;jo&quot;));
 * 
 * Predicate joeFilter = new BeanPropertyFilter(&quot;first&quot;, &quot;joe&quot;);
 * Collection joes = CollectionUtils.select(names, joeFilter);
 * </pre>
 * 
 * @see org.apache.commons.collections.CollectionUtils
 */
public class BeanPropertyFilter implements Predicate
{
    private static final Logger logger_ = 
        Logger.getLogger(BeanPropertyFilter.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Name of the javabean property.
     */
    private String propertyName_;

    /**
     * Values to filter.
     */
    private Object[] filterValues_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a BeanPropertyFilter with the given javabean property name and
     * value.
     * 
     * @param name Name of the javabean property.
     * @param value Value to compare with the value of the javabean property.
     */
    public BeanPropertyFilter(String name, Object value)
    {
        this(name, new Object[] {value});
    }

    
    /**
     * Creates a BeanPropertyFilter.
     * 
     * @param name Name of the javabean property.
     * @param values Collection of values to match the value of the javabean
     *        property against.
     */
    public BeanPropertyFilter(String name, Collection values)
    {
        this(name, values.toArray());
    }

    
    /**
     * Creates a BeanPropertyFilter.
     * 
     * @param name Name of the javabean property.
     * @param values Array of values to match the value of the javabean property
     *        against.
     */
    public BeanPropertyFilter(String name, Object[] values)
    {
        propertyName_ = name;
        filterValues_ = values;
    }

    //--------------------------------------------------------------------------
    // Predicate Interface
    //--------------------------------------------------------------------------

    /**
     * @see org.apache.commons.collections.Predicate#evaluate( java.lang.Object)
     */
    public boolean evaluate(Object javabean)
    {
        // Extract the named javabean property value from the subject and
        // compare it to the value passed in via the constructor.

        Object propertyValue = null;

        try
        {
            propertyValue = PropertyUtils.getProperty(javabean, propertyName_);
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
        
        if (propertyValue == null)
            return false;
        else    
            return ArrayUtil.contains(filterValues_, propertyValue);
    }
}