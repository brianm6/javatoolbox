package toolbox.util.random;

/**
 * SequenceEndedException is thrown when a non-repeating sequence no longer has
 * any more unique values to offer.
 * 
 * @see toolbox.util.random.RandomSequence 
 */
public class SequenceEndedException extends RuntimeException
{
    /**
     * Creates a SequenceEndedException.
     * 
     * @param message Error message.
     */
    public SequenceEndedException(String message)
    {
        super(message);
    }
}