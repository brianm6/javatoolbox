package toolbox.util;

/**
 * AssertionException represents an assertion violation.
 *
 * @see Assert
 */
public class AssertionException extends RuntimeException
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an AssertionException.
     */
    public AssertionException()
    {
    }
    
    
    /**
     * Creates an AssertionException with a descriptive message.
     *
     * @param message A descriptive message string.
     */
    public AssertionException(String message)
    {
        super(message);
    }
}