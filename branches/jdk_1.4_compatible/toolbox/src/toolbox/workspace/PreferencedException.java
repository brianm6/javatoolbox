package toolbox.workspace;

/**
 * PreferencedException is thrown for for errors encountered during the
 * application or saving of preferences.
 */
public class PreferencedException extends RuntimeException
{
    /**
     * Creates a PreferencedException.
     */
    public PreferencedException()
    {
    }

    
    /**
     * Creates a PreferencedException.
     * 
     * @param message Error message.
     */
    public PreferencedException(String message)
    {
        super(message);
    }

    
    /**
     * Creates a PreferencedException.
     * 
     * @param cause Originating exception.
     */
    public PreferencedException(Throwable cause)
    {
        super(cause);
    }

    
    /**
     * Creates a PreferencedException.
     * 
     * @param message Error message.
     * @param cause Originating exception.
     */
    public PreferencedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}