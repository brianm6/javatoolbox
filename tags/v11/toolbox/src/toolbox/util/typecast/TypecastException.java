package toolbox.util.typecast;

/**
 * TypeCase exception
 */
public class TypecastException extends RuntimeException
{
    protected Throwable rootCause;
 
    public TypecastException(String message)
    {
        super(message);
    }

    public TypecastException(String message, Throwable rootCause)
    {
        super(message);
        this.rootCause = rootCause;
    }

    public void printStackTrace()
    {
        printStackTrace(System.out);
    }

    public void printStackTrace(java.io.PrintStream s)
    {
        if (rootCause != null)
            rootCause.printStackTrace(s);
        rootCause.printStackTrace(s);
    }

    public void printStackTrace(java.io.PrintWriter s)
    {
        if (rootCause != null)
            rootCause.printStackTrace(s);
        rootCause.printStackTrace(s);
    }

    public String toString()
    {
        return rootCause == null ? super.toString() : rootCause.toString();
    }
}
