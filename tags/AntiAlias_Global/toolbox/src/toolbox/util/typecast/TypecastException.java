package toolbox.util.typecast;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * TypeCase exception
 */
public class TypecastException extends RuntimeException
{
    private Throwable rootCause_;

    /**
     * Constructor
     * 
     * @param  message  Exception message
     */ 
    public TypecastException(String message)
    {
        super(message);
    }

    /**
     * Constructor
     * 
     * @param  message      Exception message
     * @param  rootCause    Exception at root cause
     */ 
    public TypecastException(String message, Throwable rootCause)
    {
        super(message);
        rootCause_ = rootCause;
    }

    /**
     * Prints stack trace to system.out
     */
    public void printStackTrace()
    {
        printStackTrace(System.out);
    }

    /**
     * Prints stack trace to a print stream
     * 
     * @param  s  PrintStream
     */
    public void printStackTrace(PrintStream s)
    {
        if (rootCause_ != null)
            rootCause_.printStackTrace(s);
            
        rootCause_.printStackTrace(s);
    }

    /**
     * Prints stack trace to a Writer
     * 
     * @param  s  PrintWriter
     */
    public void printStackTrace(PrintWriter s)
    {
        if (rootCause_ != null)
            rootCause_.printStackTrace(s);
            
        rootCause_.printStackTrace(s);
    }

    /**
     * @return  String representation
     */
    public String toString()
    {
        return rootCause_ == null ? super.toString() : rootCause_.toString();
    }
}
