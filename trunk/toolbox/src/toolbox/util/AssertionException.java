package toolbox.util;

/**
 * Represents an assertion violation.
 *
 * @see Assert
 */
public class AssertionException extends RuntimeException
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Create a default instance.
     */
    public AssertionException()
    {
    }
    
    
    /**
     * Create an instance with a descriptive message.
     *
     * @param message A descriptive message string
     */
    public AssertionException(String message)
    {
        super(message);
    }
}