package toolbox.util.random;

/**
 * SequenceEndedException is responsible for _____.
 */
public class SequenceEndedException extends Exception
{

    /**
     * Creates a SequenceEndedException.
     */
    public SequenceEndedException()
    {
        super();
    }


    /**
     * Creates a SequenceEndedException.
     * 
     * @param message
     */
    public SequenceEndedException(String message)
    {
        super(message);
    }


    /**
     * Creates a SequenceEndedException.
     * 
     * @param cause
     */
    public SequenceEndedException(Throwable cause)
    {
        super(cause);
    }


    /**
     * Creates a SequenceEndedException.
     * 
     * @param message
     * @param cause
     */
    public SequenceEndedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
