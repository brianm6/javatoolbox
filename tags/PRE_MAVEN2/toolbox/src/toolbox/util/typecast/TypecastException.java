package toolbox.util.typecast;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Typecast exception.
 */
public class TypecastException extends RuntimeException
{
    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    /**
     * Root cause of the error.
     */
    private Throwable rootCause_;

    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Creates a TypecastException.
     * 
     * @param message Exception message.
     */
    public TypecastException(String message)
    {
        super(message);
    }

    
    /**
     * Creates a TypecastException.
     * 
     * @param message Exception message.
     * @param rootCause Exception at root cause.
     */
    public TypecastException(String message, Throwable rootCause)
    {
        super(message);
        rootCause_ = rootCause;
    }

    //--------------------------------------------------------------------------
    // Public 
    //--------------------------------------------------------------------------
    
    /**
     * Prints stack trace to system.out.
     */
    public void printStackTrace()
    {
        printStackTrace(System.out);
    }

    
    /**
     * Prints stack trace to a print stream.
     * 
     * @param s PrintStream.
     */
    public void printStackTrace(PrintStream s)
    {
        if (rootCause_ != null)
            rootCause_.printStackTrace(s);

        rootCause_.printStackTrace(s);
    }

    
    /**
     * Prints stack trace to a Writer.
     * 
     * @param s PrintWriter.
     */
    public void printStackTrace(PrintWriter s)
    {
        if (rootCause_ != null)
            rootCause_.printStackTrace(s);

        rootCause_.printStackTrace(s);
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object 
    //--------------------------------------------------------------------------
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return rootCause_ == null ? super.toString() : rootCause_.toString();
    }
}