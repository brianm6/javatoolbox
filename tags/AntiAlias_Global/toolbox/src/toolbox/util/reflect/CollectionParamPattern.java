package toolbox.util.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Vector;

/**
 * CollectionParamPattern
 */
public class CollectionParamPattern extends ParamPattern
{
    private Method convertMethod_;

    // CONSTRUCTORS

    /**
     * Creates a new CollectionParamPattern object.
     */
    public CollectionParamPattern()
    {
    }

    /**
     * Creates a new CollectionParamPattern object.
     * 
     * @param paramType DOCUMENT ME!
     */
    public CollectionParamPattern(Class paramType)
    {
        super(paramType);
        initializeConvertMethod();
    }

    // PARAMPATTERN METHODS

    /**
     * DOCUMENT ME!
     * 
     * @param aClass DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    protected boolean isApplicable(Class aClass)
    {
        return aClass.isArray() || Vector.class.isAssignableFrom(aClass);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aClass DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    protected int getFactor(Class aClass)
    {
        return MATCH_CONVERT;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param object DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    protected Object advancedConvert(Object object)
    {
        if (object instanceof Vector || object.getClass().isArray())
            return invoke(object);
        else
            return super.advancedConvert(object);
    }

    // SUPPORT METHODS

    /**
     * DOCUMENT ME!
     * 
     * @param object DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    protected Object invoke(Object object)
    {
        try
        {
            return convertMethod_.invoke(
                null, new Object[] { getParamType(), object });
        }
        catch (Exception ex)
        {
            throw new ClassCastException(ex.getMessage());
        }
    }

    /**
     * DOCUMENT ME!
     */
    protected void initializeConvertMethod()
    {
        try
        {
            convertMethod_ = getParamType().isArray() ? 
                getClass().getMethod("toArray", 
                    new Class[] { Class.class, Vector.class }) : 
                getClass().getMethod("toVector", 
                    new Class[] { Class.class, Object.class });
        }
        catch (Exception ex)
        {

            // should never happen
        }
    }

    // CONVERSION METHODS

    /**
     * DOCUMENT ME!
     * 
     * @param paramType DOCUMENT ME!
     * @param v DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    public static Object toArray(Class paramType, Vector v)
    {
        int size = v.size();
        Object array = Array.newInstance(paramType.getComponentType(), size);
        Enumeration enum = v.elements();

        for (int i = 0; i < size; i++)
            Array.set(array, i, enum.nextElement());

        return array;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param paramType DOCUMENT ME!
     * @param array DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    public static Vector toVector(Class paramType, Object array)
    {
        int length = Array.getLength(array);
        Vector vector = new Vector(length);

        for (int i = 0; i < length; i++)
            vector.addElement(Array.get(array, i));

        return vector;
    }

    // STATIC INITIALIZER
    static 
    {
        ParamPattern.register(new CollectionParamPattern());
    }
}