package toolbox.util.typecast;

/**
 * Gives a standard interface for caching objects based on the
 * identity an object.
 *
 * The identity of an object is can be determined by using
 * <code>System.getIdentityHashcode(...)</code>, but can also have
 * other implementation.
 *
 * @see WeakIdentityCache
 */
public interface IdentityCache
{

    /**
     * Gets the value based on <tt>key</tt>'s object reference
     */
    public Object get(Object key);

    /**
     * Stores <tt>value</tt> and associates it with the <tt>key</tt>'s
     * object reference
     */
    public void put(Object key, Object value);
}