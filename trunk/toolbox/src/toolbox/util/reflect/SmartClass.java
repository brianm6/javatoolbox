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
     * @param aClass 
     */
    protected SmartClass(Class aClass)
    {
        javaClass = aClass;
    }

    // ACCESSING

    /**
     * 
     * 
     * @param selector 
     * @param parameterTypes 
     * @return  
     * @throws NoSuchMethodException 
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
     * 
     * 
     * @param selector 
     * @param parameters 
     * @return  
     * @throws NoSuchMethodException 
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
     * 
     * 
     * @param name 
     * @param parameterTypes 
     * @return  
     * @throws NoSuchMethodException 
     */
    public SmartMethod getMethod(String name, Class[] parameterTypes)
        throws NoSuchMethodException
    {
        return getMethod(new Symbol(name), parameterTypes);
    }

    /**
     * 
     * 
     * @param name 
     * @param parameters 
     * @return  
     * @throws NoSuchMethodException 
     */
    public SmartMethod getMethod(String name, Object[] parameters) 
        throws NoSuchMethodException
    {
        return getMethod(new Symbol(name), parameters);
    }

    /**
     * 
     * 
     * @param parameterTypes 
     * @return  
     * @throws NoSuchMethodException 
     */
    public SmartConstructor getConstructor(Class[] parameterTypes) 
        throws NoSuchMethodException
    {
        return (SmartConstructor) 
            constructors.getMethod(parameterTypes == null 
                ? new Class[] {}
                : parameterTypes);
    }

    /**
     * 
     * 
     * @param parameters 
     * @return  
     * @throws NoSuchMethodException 
     */
    public SmartConstructor getConstructor(Object[] parameters) 
        throws NoSuchMethodException
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
     * 
     * 
     * @param obj 
     * @param selector 
     * @param parameters 
     * @return  
     * @throws NoSuchMethodException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws InvocationTargetException 
     * @throws Exception 
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
     * 
     * 
     * @param obj 
     * @param methodName 
     * @param parameters 
     * @return  
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws InvocationTargetException 
     * @throws Exception 
     */
    public Object invoke(Object obj, String methodName, Object[] parameters)
        throws IllegalAccessException, IllegalArgumentException, 
            InvocationTargetException, Exception
    {
        return invoke(obj, new Symbol(methodName), parameters);
    }

    /**
     * 
     * 
     * @param obj 
     * @param selector 
     * @param parameters 
     * @return  
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
     * 
     * 
     * @param obj 
     * @param methodName 
     * @param parameters 
     * @return  
     */
    public Object invokeSilent(Object obj, String methodName, 
        Object[] parameters)
    {
        return invokeSilent(obj, new Symbol(methodName), parameters);
    }

    // INSTATIONATION METHODS

    /**
     * 
     * 
     * @return  
     * @throws NoSuchMethodException 
     * @throws InstantiationException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws InvocationTargetException 
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
     * 
     * 
     * @param parameters 
     * @return  
     * @throws NoSuchMethodException 
     * @throws InstantiationException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws InvocationTargetException 
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
     * 
     */
    public void constructClass()
    {
        cacheMethods();
        cacheConstructors();
    }

    /**
     * 
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
            
            holder = holder == null 
                ? new MethodHolder(method) 
                : holder.addMethod(method);
                
            methods.put(selector, holder);
        }
    }

    /**
     * 
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