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
     * @param  millis  Milliseconds for the current thread to sleep
     */
    public static void sleep(int millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch(Exception e)
        {
            ;// Ignore
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
     * @param  thread  Thread to join
     */
    public static void join (Thread thread)
    {
        if (thread != null)
        {
            try
            {
                thread.join();
            }
            catch(InterruptedException e)
            {
                ;// Ignore
            }
        }
    }

    /**
     * Joins the given thread
     * 
     * @param  thread  Thread to join
     * @param  millis  Timeout
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
                ;// Ignore
            }
        }
    }

    /**
     * Stops a thread as gracefully as possible with a default max wait of 
     * 1 second
     * 
     * @param  t  Thread to stop
     */
    public static void stop(Thread t)
    {
        stop(t, 1000);
    }

    /**
     * Stops a thread as gracefully as possible within a given amount of time.
     * If the thread is stopped successfully, the thread is set equal to null
     * otherwise a warning is logged and the thread is not left alone.
     * 
     * @param  t       Thread to stop
     * @param  millis  Max number of millis to wait for thread to die
     */
    public static void stop(Thread t, long millis)
    {
        // Kill nicely and wait for death
        ThreadUtil.join(t, millis);
        
        // Nuke as last resort if not dead
        if (t.isAlive())
            t.interrupt();

        if (t.isAlive())
            logger_.warn("Could not kill thread " + t);
        else    
            t = null;                    
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

    /**
     * Runs an object's method in a separate thread 
     * 
     * @param   target   Object to invoke method upon
     * @param   method   Name of the method to execute
     * @param   params   Methods arguments
     * @param   clazzes  Parameter class types
     * @return  Started thread of execution 
     */
    public static Thread run(Object target, String method, Object[] params, 
        Class[] clazzes)
    {
        // create thread with a MethodRunner and start
        Runnable runnable = 
            new ThreadUtil.MethodRunner(target, method, params, clazzes);
            
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
         * Parameter types
         */
        private Class[] clazzes_;
            
        /**
         * Creates a MethodRunner
         * 
         * @param  newTarget  Target object of method invocation
         * @param  newMethod  Method name on target object
         * @param  newParams  List of parameters to the method
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
         * Creates a MethodRunner
         * 
         * @param  newTarget   Target object of method invocation
         * @param  newMethod   Method name on target object
         * @param  newParams   List of parameters to the method
         * @param  newClazzes  List of classes for parameter types
         */
        public MethodRunner(Object newTarget, String newMethod, 
            Object[] newParams, Class[] newClazzes)
        {
            target_  = newTarget;
            method_  = newMethod;                    
            params_  = newParams;
            clazzes_ = newClazzes;
        }        
        
        
        /**
         * Executes the method provided at time of construction
         */
        public void run()
        {
            try
            {  
                if (clazzes_ == null)
                    MethodUtils.invokeMethod(target_, method_, params_);    
                else
                    MethodUtils.invokeMethod(target_, method_, params_, 
                        clazzes_);    
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