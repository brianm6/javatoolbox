package toolbox.util.collections;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * A RuntimeException which can be thrown by ObjectMap when the underlying call
 * to get/set methods throw an exception. It is a RuntimeException so that the
 * Map interface will not be violoated by throwing a caught exception.
 * 
 * @see ObjectMap
 */
public class MapInvocationTargetException extends RuntimeException
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Target exception.
     */
    private Throwable target_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Protected constructor.
     */    
    protected MapInvocationTargetException()
    {
    }

    
    /**
     * Creates MapInvocationTargetException.
     * 
     * @param target Target throwable.
     */
    public MapInvocationTargetException(Throwable target)
    {
        super();
        target_ = target;
    }

    
    /**
     * Creates MapInvocationTargetException.
     * 
     * @param target Target throwable.
     * @param s Reason.
     */
    public MapInvocationTargetException(Throwable target, String s)
    {
        super(s);
        target_ = target;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the target exception.
     * 
     * @return Throwable.
     */
    public Throwable getTargetException()
    {
        return target_;
    }

    
    /**
     * Prints the stack trace.
     */
    public void printStackTrace()
    {
        printStackTrace(System.err);
    }

    
    /**
     * Prints stack trace to a print stream.
     * 
     * @param ps Print stream.
     */
    public void printStackTrace(PrintStream ps)
    {
        synchronized (ps)
        {
            if (target_ != null)
            {
                ps.print("java.lang.reflect.MapInvocationTargetException: ");
                target_.printStackTrace(ps);
            }
            else
            {
                super.printStackTrace(ps);
            }
        }
    }

    
    /**
     * Prints stacktrace to print writer.
     * 
     * @param pw Print writer.
     */
    public void printStackTrace(PrintWriter pw)
    {
        synchronized (pw)
        {
            if (target_ != null)
            {
                pw.print(getClass().getName() + ": ");
                target_.printStackTrace(pw);
            }
            else
            {
                super.printStackTrace(pw);
            }
        }
    }
}