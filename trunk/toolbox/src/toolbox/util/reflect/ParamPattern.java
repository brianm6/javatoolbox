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
    /** Not match */
    public static final int MATCH_NOT = Integer.MIN_VALUE;
    
    /** Convert match */
    public static final int MATCH_CONVERT = 50;
    
    /** Primitive match */
    public static final int MATCH_PRIMITIVE = 70;
    
    /** Superclass match */
    public static final int MATCH_SUPERCLASS = 80;
    
    /** Exact match */
    public static final int MATCH_EXACT = 100;
    
    private static Hashtable CachedPatterns = new Hashtable(50);
    private static Vector RegisteredPatterns = new Vector(10);
    private static Hashtable Wrappers = new Hashtable(15);
    private Class paramType_;
    private Constructor patternConstructor_;

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
        this.paramType_ = paramType;
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
        if (aClass == paramType_ || (aClass.isPrimitive() && 
            isWrapperFor(paramType_, aClass)) || 
            (paramType_.isPrimitive() && isWrapperFor(aClass, paramType_)))
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
        if (object == null || paramType_.isAssignableFrom(object.getClass()))
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
        return pattern.paramType_ == paramType_;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aClass DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    protected boolean isApplicable(Class aClass)
    {
        return paramType_.isAssignableFrom(aClass);
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
            "Cannot convert " + object.getClass() + " to " + paramType_);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param object DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    protected boolean validPrimitiveTypes(Object object)
    {
        if (paramType_.isPrimitive())
        {
            Class aClass = (Class) Wrappers.get(object.getClass());

            return aClass.isAssignableFrom(paramType_);
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
            if (patternConstructor_ == null)
                patternConstructor_ = 
                    getClass().getConstructor(new Class[] {Class.class});

            return (ParamPattern) 
                patternConstructor_.newInstance(new Object[] {aClass});
        }
        catch (Exception ex)
        {

            // should never happen
            return null;
        }
    }
    
    /**
     * Returns the paramType.
     * @return Class
     */
    public Class getParamType()
    {
        return paramType_;
    }

}