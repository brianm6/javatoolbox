package toolbox.util.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * SmartMethod.
 */
public class SmartMethod
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    protected Member method_;
    protected ParamPattern[] patterns_;
    private Symbol selector_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a new SmartMethod object.
     * 
     * @param member DOCUMENT ME!
     */
    protected SmartMethod(Member member)
    {
        method_ = member;
        selector_ = new Symbol(method_.getName());
        cachePatterns();
    }


    /**
     * Creates a new SmartMethod object.
     * 
     * @param method DOCUMENT ME!
     */
    public SmartMethod(Method method)
    {
        this((Member) method);
    }


    /**
     * Creates a new SmartMethod object.
     * 
     * @param method DOCUMENT ME!
     * @param patterns DOCUMENT ME!
     */
    public SmartMethod(Method method, ParamPattern[] patterns)
    {
        method_ = method;
        selector_ = new Symbol(method.getName());
        patterns_ = patterns;
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return method_.hashCode();
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object another)
    {
        if (another == null || another.getClass() != getClass())
            return false;

        return method_ == ((SmartMethod) another).method_;
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return method_.toString();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Class[] getExceptionTypes()
    {
        return getMethod().getExceptionTypes();
    }


    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Class getDeclaringClass()
    {
        return method_.getDeclaringClass();
    }


    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public final Method getMethod()
    {
        return (Method) method_;
    }


    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public String getName()
    {
        return method_.getName();
    }


    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Symbol getSelector()
    {
        return selector_;
    }


    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Class[] getParameterTypes()
    {
        return getMethod().getParameterTypes();
    }


    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public ParamPattern[] getParameterPatterns()
    {
        return patterns_;
    }


    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Class getReturnType()
    {
        return getMethod().getReturnType();
    }


    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public int getModifiers()
    {
        return method_.getModifiers();
    }


    // INVOKATION METHODS

    /**
     * DOCUMENT ME!
     * 
     * @param object DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     * @return DOCUMENT ME!
     * @throws IllegalAccessException DOCUMENT ME!
     * @throws IllegalArgumentException DOCUMENT ME!
     * @throws InvocationTargetException DOCUMENT ME!
     */
    public Object invoke(Object object, Object[] parameters) throws 
        IllegalAccessException, 
        IllegalArgumentException,
        InvocationTargetException
    {
        try
        {
            if (parameters != null)
                for (int i = 0; i < parameters.length; i++)
                    parameters[i] = patterns_[i] == null ? parameters[i]
                        : patterns_[i].convert(parameters[i]);
        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException(ex.toString());
        }

        return getMethod().invoke(object, parameters);
    }


    /**
     * DOCUMENT ME!
     * 
     * @param object DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    public Object invokeSilent(Object object, Object[] parameters)
    {
        try
        {
            return invoke(object, parameters);
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    protected void cachePatterns()
    {
        Class[] types = getParameterTypes();
        patterns_ = new ParamPattern[types.length];

        for (int i = 0; i < types.length; i++)
            patterns_[i] = ParamPattern.createPattern(types[i]);
    }
}