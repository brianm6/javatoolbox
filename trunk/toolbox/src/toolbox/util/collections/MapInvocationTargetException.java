package toolbox.util.collections;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * A RuntimeException which can be thrown by ObjectMap
 * when the underlying call to get/set methods throw
 * an exception.  It is a RuntimeException so that
 * the Map interface will not be violoated by throwing
 * a caught exception.
 * 
 * @author Steven Lee
 * 
 * @see ObjectMap
 */
public class MapInvocationTargetException extends RuntimeException
{
    private Throwable target;

    protected MapInvocationTargetException()
    {
        super();
    }

    public MapInvocationTargetException(Throwable target)
    {
        super();
        this.target = target;
    }

    public MapInvocationTargetException(Throwable target, String s)
    {
        super(s);
        this.target = target;
    }

    public Throwable getTargetException()
    {
        return target;
    }

    public void printStackTrace()
    {
        printStackTrace(System.err);
    }

    public void printStackTrace(PrintStream ps)
    {
        synchronized (ps)
        {
            if (target != null)
            {
                ps.print("java.lang.reflect.MapInvocationTargetException: ");
                target.printStackTrace(ps);
            }
            else
            {
                super.printStackTrace(ps);
            }
        }
    }

    public void printStackTrace(PrintWriter pw)
    {
        synchronized (pw)
        {
            if (target != null)
            {
                pw.print(
                    "com.ip.util.collection.MapInvocationTargetException: ");
                target.printStackTrace(pw);
            }
            else
            {
                super.printStackTrace(pw);
            }
        }
    }

}
