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
    private Class javaClass_;
    private Hashtable methods_;
    private IMethodHolder constructors_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a new SmartClass object.
     * 
     * @param aClass 
     */
    protected SmartClass(Class aClass)
    {
        javaClass_ = aClass;
    }

    //--------------------------------------------------------------------------
    // Get Methods
    //--------------------------------------------------------------------------

    /**
     * Gets method
     * 
     * @param   selector         Selector
     * @param   parameterTypes   Param types
     * @return  Smart method
     * @throws  NoSuchMethodException on no method
     */
    public SmartMethod getMethod(Symbol selector, Class[] parameterTypes)
        throws NoSuchMethodException
    {
        IMethodHolder method = (IMethodHolder) methods_.get(selector);

        if (method == null)
            throw new NoSuchMethodException();

        return method.getMethod(parameterTypes == null ? 
            new Class[] { } : parameterTypes);
    }

    /**
     * Gets method
     * 
     * @param   selector    Selector 
     * @param   parameters  Params 
     * @return  Smart method
     * @throws  NoSuchMethodException on no method
     */
    public SmartMethod getMethod(Symbol selector, Object[] parameters)
        throws NoSuchMethodException
    {
        if (parameters == null)
            return getMethod(selector, null);

        Class[] types = new Class[parameters.length];

        for (int i = 0; i < types.length; i++)
            types[i] = parameters[i] == null ? null : parameters[i].getClass();

        return getMethod(selector, types);
    }

    /**
     * Gets method
     * 
     * @param   name            Name
     * @param   parameterTypes  Param types
     * @return  Smart method
     * @throws  NoSuchMethodException on no method
     */
    public SmartMethod getMethod(String name, Class[] parameterTypes)
        throws NoSuchMethodException
    {
        return getMethod(new Symbol(name), parameterTypes);
    }

    /**
     * Gets method
     * 
     * @param   name            Name
     * @param   parameters      Parameters
     * @return  Smart method
     * @throws  NoSuchMethodException on no method
     */
    public SmartMethod getMethod(String name, Object[] parameters) 
        throws NoSuchMethodException
    {
        return getMethod(new Symbol(name), parameters);
    }

    /**
     * Gets constructor
     * 
     * @param   parameterTypes  Param types
     * @return  Smart constructor
     * @throws  NoSuchMethodException on no method
     */
    public SmartConstructor getConstructor(Class[] parameterTypes) 
        throws NoSuchMethodException
    {
        return (SmartConstructor) 
            constructors_.getMethod(parameterTypes == null ? 
                new Class[] {} : parameterTypes);
    }

    /**
     * Gets constructor
     * 
     * @param   parameters  Parameters
     * @return  SmartConstructor
     * @throws  NoSuchMethodException on method not found
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

    //--------------------------------------------------------------------------
    // Invocation Methods
    //--------------------------------------------------------------------------
    
    /**
     * Invokes method on an object
     * 
     * @param   obj         Object to invoke method on 
     * @param   selector    Selector for method
     * @param   parameters  Params to method
     * @return  Method return value
     * @throws  NoSuchMethodException on method not found
     * @throws  IllegalAccessException  on not public enough to access
     * @throws  IllegalArgumentException on illegal argument
     * @throws  InvocationTargetException on invalid invocation on target
     * @throws  Exception on error
     */
    public Object invoke(Object obj, Symbol selector, Object[] parameters)
        throws NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, Exception
    {
        SmartMethod method = getMethod(selector, parameters);

        return method.invoke(obj, parameters);
    }

    /**
     * Invokes method on an object
     * 
     * @param   obj            Object to invoke method on 
     * @param   methodName     Name of method to invoke
     * @param   parameters     Params to method
     * @return  Method return value
     * @throws  IllegalAccessException on not public enough to access
     * @throws  IllegalArgumentException on illegal argument
     * @throws  InvocationTargetException on invalid invocation on target
     * @throws  Exception on error
     */
    public Object invoke(Object obj, String methodName, Object[] parameters)
        throws IllegalAccessException, IllegalArgumentException, 
            InvocationTargetException, Exception
    {
        return invoke(obj, new Symbol(methodName), parameters);
    }

    /**
     * @param  obj          Object
     * @param  selector     Selector 
     * @param  parameters   Params
     * @return Return value 
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
     * @param  obj          Object
     * @param  methodName   Method name
     * @param  parameters   Params
     * @return Return value
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
     * @return New object 
     * @throws NoSuchMethodException on error
     * @throws InstantiationException on error
     * @throws IllegalAccessException on error
     * @throws IllegalArgumentException on error
     * @throws InvocationTargetException on error
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
     * Creates a new instance
     * 
     * @param  parameters   Params 
     * @return New instance 
     * @throws NoSuchMethodException on error
     * @throws InstantiationException on error
     * @throws IllegalAccessException on error
     * @throws IllegalArgumentException on error
     * @throws InvocationTargetException on error
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
     * Constructs class 
     */
    public void constructClass()
    {
        cacheMethods();
        cacheConstructors();
    }

    /**
     * Caches methods
     */
    protected void cacheMethods()
    {
        Method[] javaMethods = javaClass_.getMethods();
        methods_ = new Hashtable(javaMethods.length * 2);

        for (int i = 0; i < javaMethods.length; i++)
        {
            SmartMethod method = new SmartMethod(javaMethods[i]);
            Object selector = method.getSelector();
            IMethodHolder holder = (IMethodHolder) methods_.get(selector);
            
            holder = holder == null ? new MethodHolder(method)  : 
                holder.addMethod(method);
                
            methods_.put(selector, holder);
        }
    }

    /**
     * Caches constructors
     */
    protected void cacheConstructors()
    {
        Constructor[] cs = javaClass_.getConstructors();

        if (cs.length > 0)
            constructors_ = new MethodHolder(new SmartConstructor(cs[0]));

        for (int i = 1; i < cs.length; i++)
            constructors_ = 
                constructors_.addMethod(new SmartConstructor(cs[i]));
    }
}