package toolbox.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.log4j.Category;

/**
 * Utility class for thread related stuff
 */
public final class ThreadUtil
{
    /** Logger **/
    protected static final Category logger_ = 
        Category.getInstance(ThreadUtil.class);
        
    /**
     * Prevent construction
     */ 
    private ThreadUtil()
    {
    }
    
    /**
     * Sleep without all the try/catch business
     * 
     * @param    millis    Milliseconds for the current thread to sleep
     */
    public static void sleep(int millis)
    {
        try
        {
            Thread.currentThread().sleep(millis);
        }
        catch(Exception e)
        {
        }
    }
    
    
    /**
     * Joins the current thread without try/catch business
     */
    public static void join()
    {
        try
        {
            Thread.currentThread().join();
        }
        catch(Exception e)
        {
        }
    }


    /**
     * Joins the current thread without try/catch business
     * 
     * @param  millis  Max number of millis to wait
     */
    public static void join(int millis)
    {
        try
        {
            Thread.currentThread().join(millis);
        }
        catch(Exception e)
        {
        }
    }


    /**
     * Runs an objects method in a thread 
     * 
     * @param   target      Object which contains method to run
     * @param   methodName  Name of the method to execute
     * @param   param       Method's single argument
     * @return  Thread that method is/was executed on
     */
    public static Thread run(Object target, String methodName, Object param)
    {
        return run(target, methodName, new Object[] {param});
    }

    
    /**
     * Runs an objects method in a thread 
     * 
     * @param   target      Object which contains method to run
     * @param   methodName  Name of the method to execute
     * @param   params      Method arguments
     * @return  Thread that method is/was executed on
     */
    public static Thread run(Object target, String methodName, Object[] params)
    {
        // Create thread with a MethodRunner and start
        Runnable runnable = new MethodRunner(target, methodName, params);
        Thread thread = new Thread(runnable);
        thread.start(); 
        return thread; 
    }

    //--------------------------------------------------------------------------
    //  Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Runs a method on a given object in a thread
     */        
    static class MethodRunner implements Runnable
    {
        /** 
         * Name of method to execute 
         */
        private String method;
        
        /** 
         * Object to execute the method on 
         */
        private Object target;
        
        /** 
         * Parameters to pass on the method invocation 
         */
        private Object[] params;
    
        /**
         * Creates a MethodRunner
         * 
         * @param  newTarget   Target object of method invocation
         * @param  newMethod   Method name on target object
         * @param  newParams   List of parameters to the method
         */
        public MethodRunner(Object newTarget, String newMethod, Object[] newParams)
        {
            target = newTarget;
            method = newMethod;                    
            params = newParams;
            
            if (params == null)
                params = new Object[0];
        }
        
        /**
         * Executes the method provided at time of construction
         */
        public void run()
        {
            try
            {   
                MethodUtils.invokeMethod(target, method, params);
            }
            catch (NoSuchMethodException nsme)
            {
                ThreadUtil.logger_.error(toString(), nsme);
            }        
            catch (IllegalAccessException iae)
            {
                ThreadUtil.logger_.error("run", iae);
            }
            catch (InvocationTargetException ite)
            {
                ThreadUtil.logger_.error("run", ite);
            }
        }
        
        /**
         * Dump to string
         * 
         * @return  Object state as a string
         */
        public String toString()
        {
            return "\n" +
                   "target=" + target.getClass().getName() + "\n" +
                   "method=" + method + "\n" +
                   "params=" + (params != null ? ArrayUtil.toString(params) : null);
        }
    }
}
