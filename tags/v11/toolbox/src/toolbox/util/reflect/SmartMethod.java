package toolbox.util.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * SmartMethod
 */
public class SmartMethod
{
    protected Member method;
    protected ParamPattern[] patterns;
    protected Symbol selector;

    // CONSTRUCTORS

    /**
     * Creates a new SmartMethod object.
     * 
     * @param member DOCUMENT ME!
     */
    protected SmartMethod(Member member)
    {
        this.method = member;
        selector = new Symbol(method.getName());
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
        this.method = method;
        this.selector = new Symbol(method.getName());
        this.patterns = patterns;
    }

    // STANDARD METHODS

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME! 
     */
    public int hashCode()
    {
        return method.hashCode();
    }

    /**
     * DOCUMENT ME!
     * 
     * @param another DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    public boolean equals(Object another)
    {
        if (another == null || another.getClass() != getClass())
            return false;

        return method == ((SmartMethod) another).method;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME! 
     */
    public String toString()
    {
        return method.toString();
    }

    // ACCESSING

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
        return method.getDeclaringClass();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME! 
     */
    public final Method getMethod()
    {
        return (Method) method;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME! 
     */
    public String getName()
    {
        return method.getName();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME! 
     */
    public Symbol getSelector()
    {
        return selector;
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
        return patterns;
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
        return method.getModifiers();
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
    public Object invoke(Object object, Object[] parameters)
        throws IllegalAccessException, IllegalArgumentException, 
            InvocationTargetException
    {
        try
        {
            if (parameters != null)
                for (int i = 0; i < parameters.length; i++)
                    parameters[i] = patterns[i] == null 
                        ? parameters[i] 
                        : patterns[i].convert(parameters[i]);
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

    // SUPPORT METHODS

    /**
     * DOCUMENT ME!
     */
    protected void cachePatterns()
    {
        Class[] types = getParameterTypes();
        patterns = new ParamPattern[types.length];

        for (int i = 0; i < types.length; i++)
            patterns[i] = ParamPattern.createPattern(types[i]);
    }
}