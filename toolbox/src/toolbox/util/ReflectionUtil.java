package toolbox.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.MethodUtils;

/**
 * ReflectionUtil is a utility class for java reflection.
 */
public class ReflectionUtil
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent construction of this static singleton.
     */
    private ReflectionUtil()
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Invokes a static method on a class and returns the object returned by
     * the method invocation.
     * 
     * @param clazz Class containing static method to invoke.
     * @param method Name of method to invoke.
     * @return Object
     * @throws InvocationTargetException on illegal invocation.
     * @throws IllegalAccessException on access error.
     */
    public static Object invokeStatic(Class clazz, String method)
        throws InvocationTargetException, IllegalAccessException
    {
        return invokeStatic(clazz, method, new Object[0]);
    }


    /**
     * Invokes a static method on a class and returns the object returned by
     * the method invocation.
     * 
     * @param clazz Class containing static method to invoke.
     * @param method Name of method to invoke.
     * @param arg Single argument to the method.
     * @return Object
     * @throws InvocationTargetException on illegal invocation.
     * @throws IllegalAccessException on access error.
     */
    public static Object invokeStatic(Class clazz, String method, Object arg)
        throws InvocationTargetException, IllegalAccessException
    {
        return invokeStatic(clazz, method, new Object[] {arg});
    }
    
    
    /**
     * Invokes a static method on a class and returns the object returned by
     * the method invocation.
     * 
     * @param clazz Class containing static method to invoke.
     * @param method Name of method to invoke.
     * @param args Array of arguments to the method.
     * @return Object
     * @throws InvocationTargetException on illegal invocation.
     * @throws IllegalAccessException on access error.
     */
    public static Object invokeStatic(Class clazz, String method, Object[] args)
        throws InvocationTargetException, IllegalAccessException
    {
        Method m = 
            MethodUtils.getAccessibleMethod(
                clazz, method, ClassUtil.toClass(args));
        
        if (m == null)
            throw new IllegalArgumentException("Method not found");
        
        return m.invoke(clazz, args);
    }
}