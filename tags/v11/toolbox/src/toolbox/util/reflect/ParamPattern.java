package toolbox.util.reflect;

import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * ParamPattern
 */
public class ParamPattern
{
    
    
    public static final int MATCH_NOT = Integer.MIN_VALUE;
    public static final int MATCH_CONVERT = 50;
    public static final int MATCH_PRIMITIVE = 70;
    public static final int MATCH_SUPERCLASS = 80;
    public static final int MATCH_EXACT = 100;
    protected static Hashtable CachedPatterns = new Hashtable(50);
    protected static Vector RegisteredPatterns = new Vector(10);
    protected static Hashtable Wrappers = new Hashtable(15);
    protected Class paramType;
    protected Constructor patternConstructor;

    // STATIC INITIALIZER
    static 
    {
        Wrappers.put(Integer.class, Integer.TYPE);
        Wrappers.put(Short.class, Short.TYPE);
        Wrappers.put(Long.class, Long.TYPE);
        Wrappers.put(Byte.class, Byte.TYPE);
        Wrappers.put(Float.class, Float.TYPE);
        Wrappers.put(Double.class, Double.TYPE);
        Wrappers.put(Character.class, Character.TYPE);
        Wrappers.put(Boolean.class, Boolean.TYPE);
        Wrappers.put(Void.class, Void.TYPE);
    }

    static 
    {
        // FORCE THE FOLLOWING PATTERNS TO REGISTER THEMSELVES
        Class c = NumberParamPattern.class;
        c = CollectionParamPattern.class;
    }


    // CONSTRUCTORS

    /**
     * Creates a new ParamPattern object.
     */
    public ParamPattern()
    {
    }

    /**
     * Creates a new ParamPattern object.
     * 
     * @param paramType DOCUMENT ME!
     */
    protected ParamPattern(Class paramType)
    {
        this.paramType = paramType;
    }

    // FACTORY METHODS

    /**
     * DOCUMENT ME!
     * 
     * @param aClass DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    public static ParamPattern createPattern(Class aClass)
    {
        ParamPattern pattern = (ParamPattern) CachedPatterns.get(aClass);

        if (pattern == null)
        {
            pattern = new ParamPattern(aClass);

            for (Enumeration enum = RegisteredPatterns.elements(); 
                enum.hasMoreElements();)
                
                pattern = ((ParamPattern) 
                    enum.nextElement()).applyTo(pattern, aClass);

            if (pattern != null)
                CachedPatterns.put(aClass, pattern);
        }

        return pattern;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param factoryPattern DOCUMENT ME!
     */
    public static void register(ParamPattern factoryPattern)
    {
        RegisteredPatterns.addElement(factoryPattern);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param current DOCUMENT ME!
     * @param aClass DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    protected ParamPattern applyTo(ParamPattern current, Class aClass)
    {
        ParamPattern pattern;

        if (isApplicable(aClass))
            pattern = newPattern(aClass);
        else
            return current;

        if (current == null)
            return pattern;
        else if (current.getFactor(aClass) >= pattern.getFactor(aClass))
            return new CompoundPattern(current, pattern);
        else
            return new CompoundPattern(pattern, current);
    }

    // MATCHING METHODS

    /**
     * DOCUMENT ME!
     * 
     * @param aClass DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    public int getMatchingFactor(Class aClass)
    {
        if (aClass == paramType
            || (aClass.isPrimitive() && isWrapperFor(paramType, aClass))
            || (paramType.isPrimitive() && isWrapperFor(aClass, paramType)))
            return MATCH_EXACT;
        else if (isApplicable(aClass))
            return getFactor(aClass);
        else
            return MATCH_NOT;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aClass DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    public ParamPattern getConverter(Class aClass)
    {
        return this;
    }

    // CONVERTING METHODS

    /**
     * DOCUMENT ME!
     * 
     * @param object DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    public Object convert(Object object)
    {
        if (object == null || paramType.isAssignableFrom(object.getClass()))
            return object;
        else
            return advancedConvert(object);
    }

    // TESTING

    /**
     * DOCUMENT ME!
     * 
     * @param pattern DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    public boolean isLike(ParamPattern pattern)
    {
        return pattern.paramType == paramType;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aClass DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    protected boolean isApplicable(Class aClass)
    {
        return paramType.isAssignableFrom(aClass);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param wrapper DOCUMENT ME!
     * @param primitive DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    protected boolean isWrapperFor(Class wrapper, Class primitive)
    {
        return Wrappers.get(wrapper) == primitive;
    }

    // SUPPORT METHODS

    /**
     * DOCUMENT ME!
     * 
     * @param aClass DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    protected int getFactor(Class aClass)
    {
        return MATCH_SUPERCLASS;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param object DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    protected Object advancedConvert(Object object)
    {
        if (validPrimitiveTypes(object))
            return object;

        throw new ClassCastException(
            "Cannot convert " + object.getClass() + " to " + paramType);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param object DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    protected boolean validPrimitiveTypes(Object object)
    {
        if (paramType.isPrimitive())
        {
            Class aClass = (Class) Wrappers.get(object.getClass());

            return aClass.isAssignableFrom(paramType);
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aClass DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    protected ParamPattern newPattern(Class aClass)
    {
        try
        {
            if (patternConstructor == null)
                patternConstructor = 
                    getClass().getConstructor(new Class[] { Class.class });

            return (ParamPattern) 
                patternConstructor.newInstance(new Object[] { aClass });
        }
        catch (Exception ex)
        {

            // should never happen
            return null;
        }
    }
}