package toolbox.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.log4j.Logger;

/**
 * Utility class for thread related stuff
 */
public final class ThreadUtil
{
    /** Logger */
    protected static final Logger logger_ = 
        Logger.getLogger(ThreadUtil.class);

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
            
    /**
     * Prevent construction
     */ 
    private ThreadUtil()
    {
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
        
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
            // Ignore
        }
    }
    
    
    /**
     * Joins the current thread without try/catch business
     */
    public static void join()
    {
        join(Thread.currentThread(), 0);
    }


    /**
     * Joins the current thread without try/catch business
     * 
     * @param  millis  Max number of millis to wait
     */
    public static void join(long millis)
    {
        join(Thread.currentThread(), millis);
    }


    /**
     * Joins the given thread
     * 
     * @param  thread   Thread to join
     * @param  millis   Timeout
     */
    public static void join (Thread thread, long millis)
    {
        if (thread != null)
        {
            try
            {
                thread.join(millis);
            }
            catch(InterruptedException e)
            {
                // Ignore
            }
        }
    }


    /**
     * Runs an objects method in a separate thread 
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
     * Runs an objects method in a separate thread 
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
    public static class MethodRunner implements Runnable
    {
        /** 
         * Name of method to execute 
         */
        private String method_;
        
        /** 
         * Object to execute the method on 
         */
        private Object target_;
        
        /** 
         * Parameters to pass on the method invocation 
         */
        private Object[] params_;
    
    
        /**
         * Creates a MethodRunner
         * 
         * @param  newTarget   Target object of method invocation
         * @param  newMethod   Method name on target object
         * @param  newParams   List of parameters to the method
         */
        public MethodRunner(Object newTarget, String newMethod, 
            Object[] newParams)
        {
            target_ = newTarget;
            method_ = newMethod;                    
            params_ = newParams;
            
            if (params_ == null)
                params_ = new Object[0];
        }
        
        
        /**
         * Executes the method provided at time of construction
         */
        public void run()
        {
            try
            {   
                MethodUtils.invokeMethod(target_, method_, params_);
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
                   "target=" + target_.getClass().getName() + "\n" +
                   "method=" + method_ + "\n" +
                   "params=" + (params_ != null ? ArrayUtil.toString(params_) : 
                               null);
        }
    }
}
