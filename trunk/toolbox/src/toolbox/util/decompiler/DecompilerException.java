package toolbox.util.decompiler;

/**
 * Decompiler specific exceptions.
 */
public class DecompilerException extends Exception
{
    /**
     * Creates a DecompilerException.
     */
    public DecompilerException()
    {
    }

    
    /**
     * Creates a DecompilerException.
     * 
     * @param message Error message
     */
    public DecompilerException(String message)
    {
        super(message);
    }

    
    /**
     * Creates a DecompilerException.
     * 
     * @param cause Cause of the error
     */
    public DecompilerException(Throwable cause)
    {
        super(cause);
    }

    
    /**
     * Creates a DecompilerException.
     * 
     * @param message Error message.
     * @param cause Cause of the error.
     */
    public DecompilerException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
