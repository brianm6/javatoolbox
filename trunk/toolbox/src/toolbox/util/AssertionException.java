package toolbox.util;

/**
 * Represents an assertion violation
 *
 * @see Assert
 */
public class AssertionException extends RuntimeException
{
    /**
     * Create a default instance
     */
    public AssertionException()
    {
    }
    
    /**
     * Create an instance with a descriptive message
     *
     * @param aString  A descriptive message string
     */
    public AssertionException(String aString)
    {
        super(aString);
    }
}
