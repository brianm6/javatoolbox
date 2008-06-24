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
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /** 
     * Not match 
     */
    public static final int MATCH_NOT = Integer.MIN_VALUE;

    /** 
     * Convert match 
     */
    public static final int MATCH_CONVERT = 50;

    /** 
     * Primitive match 
     */
    public static final int MATCH_PRIMITIVE = 70;

    /** 
     * Superclass match 
     */
    public static final int MATCH_SUPERCLASS = 80;

    /** 
     * Exact match 
     */
    public static final int MATCH_EXACT = 100;

    //--------------------------------------------------------------------------
    // Static Fields
    //--------------------------------------------------------------------------
    
    private static Hashtable CachedPatterns = new Hashtable(50);
    private static Vector RegisteredPatterns = new Vector(10);
    private static Hashtable Wrappers = new Hashtable(15);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private Class paramType_;
    private Constructor patternConstructor_;
    
    //--------------------------------------------------------------------------
    // Static Initializer
    //--------------------------------------------------------------------------
    
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

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

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
        paramType_ = paramType;
    }


    //--------------------------------------------------------------------------
    // Public Static
    //--------------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     * 
     * @param clazz DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    public static ParamPattern createPattern(Class clazz)
    {
        ParamPattern pattern = (ParamPattern) CachedPatterns.get(clazz);

        if (pattern == null)
        {
            pattern = new ParamPattern(clazz);

            for (Enumeration e = RegisteredPatterns.elements(); 
                 e.hasMoreElements();)

                pattern = ((ParamPattern) 
                    e.nextElement()).applyTo(pattern, clazz);

            if (pattern != null)
                CachedPatterns.put(clazz, pattern);
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

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * DOCUMENT ME!
     * 
     * @param current DOCUMENT ME!
     * @param clazz DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    protected ParamPattern applyTo(ParamPattern current, Class clazz)
    {
        ParamPattern pattern;

        if (isApplicable(clazz))
            pattern = newPattern(clazz);
        else
            return current;

        if (current == null)
            return pattern;
        else if (current.getFactor(clazz) >= pattern.getFactor(clazz))
            return new CompoundPattern(current, pattern);
        else
            return new CompoundPattern(pattern, current);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     * 
     * @param clazz DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    public int getMatchingFactor(Class clazz)
    {
        if (clazz == paramType_
            || (clazz.isPrimitive() && isWrapperFor(paramType_, clazz))
            || (paramType_.isPrimitive() && isWrapperFor(clazz, paramType_)))
            return MATCH_EXACT;
        else if (isApplicable(clazz))
            return getFactor(clazz);
        else
            return MATCH_NOT;
    }


    /**
     * DOCUMENT ME!
     * 
     * @param clazz DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    public ParamPattern getConverter(Class clazz)
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
     * @param clazz DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    protected boolean isApplicable(Class clazz)
    {
        return paramType_.isAssignableFrom(clazz);
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
     * @param clazz DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    protected int getFactor(Class clazz)
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

        throw new ClassCastException("Cannot convert " + object.getClass()
            + " to " + paramType_);
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
            Class clazz = (Class) Wrappers.get(object.getClass());
            return clazz.isAssignableFrom(paramType_);
        }

        return false;
    }


    /**
     * DOCUMENT ME!
     * 
     * @param clazz DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    protected ParamPattern newPattern(Class clazz)
    {
        try
        {
            if (patternConstructor_ == null)
                patternConstructor_ = getClass().getConstructor(
                    new Class[]{Class.class});

            return (ParamPattern) patternConstructor_
                .newInstance(new Object[]{clazz});
        }
        catch (Exception ex)
        {
            // should never happen
            return null;
        }
    }


    /**
     * Returns the paramType.
     * 
     * @return Class
     */
    public Class getParamType()
    {
        return paramType_;
    }
}