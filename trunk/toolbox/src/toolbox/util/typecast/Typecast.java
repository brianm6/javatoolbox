package toolbox.util.typecast;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Can perform a typecast on any object so that it can aquire new behavior at
 * runtime without coupling it to the implementation.
 * Most of the work is done within <code>ClassFinder</code>.<p>
 * 
 * Example:
 * <pre>
 * Typecast typecast = new Typecast( java.util.Map.class );
 * Map map = typecast.coerce( new com.some.Object() );
 * </pre>
 *
 * @see toolbox.util.collections.AsMap
 * @see ClassFinder
 */
public class Typecast
{
    /*
     * TO DO:improve cache to have several alternatives to a WeakIdentityCache
     *       (e.g. SoftReference, LRUTable, TimeDelayed, or a CombinedCache 
     *       which would combine them all )
     */
    
    // INSTANCE VARIABLES

    private Class toClass_;
    private ClassFinder finder_;
    private Class identityCacheClass_;
    
    /** 
     * key=policy, 
     * value=IdentityCache 
     */
    private Map cache_ = Collections.synchronizedMap(new HashMap(10));

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor
     * 
     * @param  toClass  Typecast destination class
     */
    public Typecast(Class toClass)
    {
        this(toClass, new ClassFinder());
    }

    /**
     * Constructor
     * 
     * @param  toClass  Typecast destination class
     * @param  finder   ClassFinder to use
     */
    public Typecast(Class toClass, ClassFinder finder)
    {
        this(toClass, finder, WeakIdentityCache.class);
    }

    /**
     * Constructor
     * 
     * @param  toClass              Typecast destination class
     * @param  finder               ClassFinder to use
     * @param  identityCacheClass   Class to use for identity in cache
     */
    public Typecast(Class toClass, ClassFinder finder, Class identityCacheClass)
    {
        toClass_ = toClass;
        finder_ = finder;
        identityCacheClass_ = identityCacheClass;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * @return ClassFinder
     */    
    public ClassFinder getClassFinder()
    {
        return finder_;
    }

    /**
     * Adds package name to search
     * 
     * @param  pckgName  Package name
     */
    public void addSearchPackage(String pckgName)
    {
        getClassFinder().addSearchPackage(pckgName);
    }

    /**
     * Adds a classes package name to the search
     * 
     * @param  classInPckg  Class in package to search
     */
    public void addSearchPackage(Class classInPckg)
    {
        getClassFinder().addSearchPackage(classInPckg);
    }

    /**
     * Coerces an identity using the given policy
     * 
     * @param  identity  Identity
     * @param  policy    Policy
     * @return Coerced object
     */
    public Object coerce(Object identity, String policy)
    {
        return coerce(identity, policy, null);
    }

    /**
     * Tries to convert the given <code>identity</code> to <code>toClass</code>.
     * The cachedCasts are first checked, and then a newInstance is created if
     * needed.
     * 
     * @param  identity  Identity
     * @param  policy    Policy
     * @param  loader    Classloader to use
     * @return Coerced object
     */
    public Object coerce(Object identity, String policy, ClassLoader loader)
    {
        if (toClass_.isInstance(identity))
            return identity;

        if (loader == null)
            loader = Thread.currentThread().getContextClassLoader();

        IdentityCache cache = getCache(policy);
        Object object = null;
        
        synchronized (cache)
        {
            object = cache.get(identity);
            if (object == null)
                cache.put(
                    identity,
                    object = newInstance(identity, policy, loader));
        }
        
        return object;
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------

    /**
     * @param  policy  Policy
     * @return Cache
     */
    private IdentityCache getCache(String policy)
    {
        IdentityCache rVal = (IdentityCache) cache_.get(policy);
        
        if (rVal == null)
        {
            try
            {
                rVal = (IdentityCache) identityCacheClass_.newInstance();
            }
            catch (Exception e)
            {
                throw new IllegalArgumentException(
                    "Cannot instantiate " + identityCacheClass_.getName());
            }
            cache_.put(policy, rVal);
        }
        return rVal;
    }

    /**
     * Creates new instance
     * 
     * @param identity
     * @param policy
     * @param loader
     * @return Object
     */
    private Object newInstance(Object identity, String policy, 
        ClassLoader loader)
    {
        try
        {
            Class fromClass = identity.getClass();
            
            Class fClass = 
                finder_.findClass(fromClass, toClass_, policy, loader);
                
            Constructor[] constructors = fClass.getConstructors();

            for (int i = 0; i < constructors.length; i++)
            {
                Class[] parms = constructors[i].getParameterTypes();

                if (parms.length == 1 && parms[0].isAssignableFrom(fromClass))
                    return constructors[i].newInstance(
                        new Object[] { identity });
            }
        }
        catch (Exception e)
        {
            // Try using the Default Policy
            if (e instanceof ClassNotFoundException && policy != null)
                return coerce(identity, null, loader);

            throw new TypecastException("Unable to coerce identity '" + 
                identity + "' to a " + toClass_.getClass().getName(), e);
        }

        return null;
    }
}