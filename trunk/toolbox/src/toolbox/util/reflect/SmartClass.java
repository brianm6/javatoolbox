package toolbox.util.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

/**
 * SmartClass
 */
public class SmartClass
{
    protected Class javaClass;
    protected Hashtable methods;
    protected IMethodHolder constructors;

    // CONSTRUCTORS

    /**
     * Creates a new SmartClass object.
     * 
     * @param aClass DOCUMENT ME!
     */
    protected SmartClass(Class aClass)
    {
        javaClass = aClass;
    }

    // ACCESSING

    /**
     * DOCUMENT ME!
     * 
     * @param selector DOCUMENT ME!
     * @param parameterTypes DOCUMENT ME!
     * @return DOCUMENT ME! 
     * @throws NoSuchMethodException DOCUMENT ME!
     */
    public SmartMethod getMethod(Symbol selector, Class[] parameterTypes)
        throws NoSuchMethodException
    {
        IMethodHolder method = (IMethodHolder) methods.get(selector);

        if (method == null)
            throw new NoSuchMethodException();

        return method.getMethod(parameterTypes == null ? new Class[] {
        }
        : parameterTypes);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param selector DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     * @return DOCUMENT ME! 
     * @throws NoSuchMethodException DOCUMENT ME!
     */
    public SmartMethod getMethod(Symbol selector, Object[] parameters)
        throws NoSuchMethodException
    {
        if (parameters == null)
            return getMethod(selector, (Class[]) null);

        Class[] types = new Class[parameters.length];

        for (int i = 0; i < types.length; i++)
            types[i] = parameters[i] == null ? null : parameters[i].getClass();

        return getMethod(selector, types);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param name DOCUMENT ME!
     * @param parameterTypes DOCUMENT ME!
     * @return DOCUMENT ME! 
     * @throws NoSuchMethodException DOCUMENT ME!
     */
    public SmartMethod getMethod(String name, Class[] parameterTypes)
        throws NoSuchMethodException
    {
        return getMethod(new Symbol(name), parameterTypes);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param name DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     * @return DOCUMENT ME! 
     * @throws NoSuchMethodException DOCUMENT ME!
     */
    public SmartMethod getMethod(String name, Object[] parameters) throws NoSuchMethodException
    {
        return getMethod(new Symbol(name), parameters);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param parameterTypes DOCUMENT ME!
     * @return DOCUMENT ME! 
     * @throws NoSuchMethodException DOCUMENT ME!
     */
    public SmartConstructor getConstructor(Class[] parameterTypes) throws NoSuchMethodException
    {
        return (SmartConstructor) constructors.getMethod(parameterTypes == null ? new Class[] {
        }
        : parameterTypes);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param parameters DOCUMENT ME!
     * @return DOCUMENT ME! 
     * @throws NoSuchMethodException DOCUMENT ME!
     */
    public SmartConstructor getConstructor(Object[] parameters) throws NoSuchMethodException
    {
        if (parameters == null)
            return getConstructor(null);

        Class[] types = new Class[parameters.length];

        for (int i = 0; i < types.length; i++)
            types[i] = parameters[i] == null ? null : parameters[i].getClass();

        return getConstructor(types);
    }

    // INVOKATION METHODS

    /**
     * DOCUMENT ME!
     * 
     * @param obj DOCUMENT ME!
     * @param selector DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     * @return DOCUMENT ME! 
     * @throws NoSuchMethodException DOCUMENT ME!
     * @throws IllegalAccessException DOCUMENT ME!
     * @throws IllegalArgumentException DOCUMENT ME!
     * @throws InvocationTargetException DOCUMENT ME!
     * @throws Exception DOCUMENT ME!
     */
    public Object invoke(Object obj, Symbol selector, Object[] parameters)
        throws
            NoSuchMethodException,
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException,
            Exception
    {
        SmartMethod method = getMethod(selector, parameters);

        return method.invoke(obj, parameters);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param obj DOCUMENT ME!
     * @param methodName DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     * @return DOCUMENT ME! 
     * @throws IllegalAccessException DOCUMENT ME!
     * @throws IllegalArgumentException DOCUMENT ME!
     * @throws InvocationTargetException DOCUMENT ME!
     * @throws Exception DOCUMENT ME!
     */
    public Object invoke(Object obj, String methodName, Object[] parameters)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, Exception
    {
        return invoke(obj, new Symbol(methodName), parameters);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param obj DOCUMENT ME!
     * @param selector DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     * @return DOCUMENT ME! 
     * @throws NoSuchMethodError DOCUMENT ME!
     */
    public Object invokeSilent(Object obj, Symbol selector, Object[] parameters)
    {
        try
        {
            SmartMethod method = getMethod(selector, parameters);

            return method.invokeSilent(obj, parameters);
        }
        catch (NoSuchMethodException ex)
        {
            throw new NoSuchMethodError(ex.getMessage());
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param obj DOCUMENT ME!
     * @param methodName DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     * @return DOCUMENT ME! 
     */
    public Object invokeSilent(Object obj, String methodName, Object[] parameters)
    {
        return invokeSilent(obj, new Symbol(methodName), parameters);
    }

    // INSTATIONATION METHODS

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME! 
     * @throws NoSuchMethodException DOCUMENT ME!
     * @throws InstantiationException DOCUMENT ME!
     * @throws IllegalAccessException DOCUMENT ME!
     * @throws IllegalArgumentException DOCUMENT ME!
     * @throws InvocationTargetException DOCUMENT ME!
     */
    public Object newInstance()
        throws
            NoSuchMethodException,
            InstantiationException,
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException
    {
        return newInstance(null);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param parameters DOCUMENT ME!
     * @return DOCUMENT ME! 
     * @throws NoSuchMethodException DOCUMENT ME!
     * @throws InstantiationException DOCUMENT ME!
     * @throws IllegalAccessException DOCUMENT ME!
     * @throws IllegalArgumentException DOCUMENT ME!
     * @throws InvocationTargetException DOCUMENT ME!
     */
    public Object newInstance(Object[] parameters)
        throws
            NoSuchMethodException,
            InstantiationException,
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException
    {
        SmartConstructor c = getConstructor(parameters);

        return c.newInstance(parameters);
    }

    // SUPPORT METHODS

    /**
     * DOCUMENT ME!
     */
    public void constructClass()
    {
        cacheMethods();
        cacheConstructors();
    }

    /**
     * DOCUMENT ME!
     */
    protected void cacheMethods()
    {
        Method[] javaMethods = javaClass.getMethods();
        methods = new Hashtable(javaMethods.length * 2);

        for (int i = 0; i < javaMethods.length; i++)
        {
            SmartMethod method = new SmartMethod(javaMethods[i]);
            Object selector = method.getSelector();
            IMethodHolder holder = (IMethodHolder) methods.get(selector);
            holder = holder == null ? new MethodHolder(method) : holder.addMethod(method);
            methods.put(selector, holder);
        }
    }

    /**
     * DOCUMENT ME!
     */
    protected void cacheConstructors()
    {
        Constructor[] cs = javaClass.getConstructors();

        if (cs.length > 0)
            constructors = new MethodHolder(new SmartConstructor(cs[0]));

        for (int i = 1; i < cs.length; i++)
            constructors = constructors.addMethod(new SmartConstructor(cs[i]));
    }
}