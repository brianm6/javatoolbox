package toolbox.util.reflect;

import java.util.HashMap;
import java.util.Map;

/**
 * SmartClassManager
 */
public class SmartClassManager
{
    //--------------------------------------------------------------------------
    // Static
    //--------------------------------------------------------------------------
    
    private static SmartClassManager DefaultClassManager = 
        new SmartClassManager();

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private Map cache_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a new SmartClassManager object.
     */
    protected SmartClassManager()
    {
        this(new HashMap());
    }


    /**
     * Creates a new SmartClassManager object.
     * 
     * @param cache DOCUMENT ME!
     */
    public SmartClassManager(Map cache)
    {
        cache_ = cache;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     * 
     * @param javaClass DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    public SmartClass getClass(Class javaClass)
    {
        SmartClass siClass = (SmartClass) cache_.get(javaClass.getName());

        if (siClass == null)
        {
            //System.out.println( "SmartClassManager: " + javaClass.getName()
            // );

            cache_.put(
                javaClass.getName(), 
                siClass = new SmartClass(javaClass));

            siClass.constructClass();
        }

        return siClass;
    }


    /**
     * DOCUMENT ME!
     * 
     * @param name DOCUMENT ME!
     * @return DOCUMENT ME!
     * @throws ClassNotFoundException DOCUMENT ME!
     */
    public SmartClass getClass(String name) throws ClassNotFoundException
    {
        return getClass(Class.forName(name));
    }


    // HELPER METHODS

    /**
     * DOCUMENT ME!
     * 
     * @param javaClass DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    public static SmartClass forClass(Class javaClass)
    {
        return DefaultClassManager.getClass(javaClass);
    }


    /**
     * DOCUMENT ME!
     * 
     * @param name DOCUMENT ME!
     * @return DOCUMENT ME!
     * @throws ClassNotFoundException DOCUMENT ME!
     */
    public static SmartClass loadClass(String name)
        throws ClassNotFoundException
    {
        return DefaultClassManager.getClass(name);
    }
}