package toolbox.util.typecast;

import java.lang.reflect.*;
import java.util.*;

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
 * @see toolbox.util.collection.AsMap
 * @see ClassFinder
 * 
 * @todo improve cache to have several alternatives to a WeakIdentityCache
 *       (e.g. SoftReference, LRUTable, TimeDelayed, or a CombinedCache which would combine them all )
 */
public class Typecast
{

    // INSTANCE VARIABLES

    private Class toClass;
    private ClassFinder finder;
    private Class identityCacheClass;
    /** key=policy, value=IdentityCache */
    private Map cache = Collections.synchronizedMap(new HashMap(10));

    public Typecast(Class toClass)
    {
        this(toClass, new ClassFinder());
    }

    public Typecast(Class toClass, ClassFinder finder)
    {
        this(toClass, finder, WeakIdentityCache.class);
    }

    public Typecast(
        Class toClass,
        ClassFinder finder,
        Class identityCacheClass)
    {
        this.toClass = toClass;
        this.finder = finder;
        this.identityCacheClass = identityCacheClass;
    }

    // API

    public ClassFinder getClassFinder()
    {
        return finder;
    }

    public void addSearchPackage(String pckgName)
    {
        getClassFinder().addSearchPackage(pckgName);
    }

    public void addSearchPackage(Class classInPckg)
    {
        getClassFinder().addSearchPackage(classInPckg);
    }

    public Object coerce(Object identity, String policy)
    {
        return coerce(identity, policy, null);
    }

    /**
     * Tries to convert the given <code>identity</code> to <code>toClass</code>.
     * The cachedCasts are first checked, and then a newInstance is created if
     * needed.
     */
    public Object coerce(Object identity, String policy, ClassLoader loader)
    {
        if (toClass.isInstance(identity))
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

    // HELPER METHODS

    private IdentityCache getCache(String policy)
    {
        IdentityCache rVal = (IdentityCache) cache.get(policy);
        if (rVal == null)
        {
            try
            {
                rVal = (IdentityCache) identityCacheClass.newInstance();
            }
            catch (Exception e)
            {
                throw new IllegalArgumentException(
                    "Cannot instantiate " + identityCacheClass.getName());
            }
            cache.put(policy, rVal);
        }
        return rVal;
    }

    private Object newInstance(
        Object identity,
        String policy,
        ClassLoader loader)
    {
        try
        {
            Class fromClass = identity.getClass();
            Class fClass = finder.findClass(fromClass, toClass, policy, loader);
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

            throw new TypecastException(
                "Unable to coerce identity '"
                    + identity
                    + "' to a "
                    + toClass.getClass().getName(),
                e);
        }

        return null;
    }
}