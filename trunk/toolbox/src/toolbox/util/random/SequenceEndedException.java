package toolbox.util.random;

/**
 * SequenceEndedException is thrown when a non-repeating sequence no longer has
 * any more unique values to offer.
 * 
 * @see toolbox.util.random.RandomSequence 
 */
public class SequenceEndedException extends Exception
{
    /**
     * Creates a SequenceEndedException.
     */
    public SequenceEndedException()
    {
    }


    /**
     * Creates a SequenceEndedException.
     * 
     * @param message Error message.
     */
    public SequenceEndedException(String message)
    {
        super(message);
    }


    /**
     * Creates a SequenceEndedException.
     * 
     * @param cause Cause.
     */
    public SequenceEndedException(Throwable cause)
    {
        super(cause);
    }


    /**
     * Creates a SequenceEndedException.
     * 
     * @param message Error message.
     * @param cause Cause
     */
    public SequenceEndedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}