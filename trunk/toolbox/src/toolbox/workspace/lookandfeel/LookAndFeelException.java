package toolbox.workspace.lookandfeel;

/**
 * LookAndFeelException
 */
public class LookAndFeelException extends Exception
{
    /**
     * Creates a LookAndFeelException
     */
    public LookAndFeelException()
    {
    }

    /**
     * Creates a LookAndFeelException
     * 
     * @param message Error message
     */
    public LookAndFeelException(String message)
    {
        super(message);
    }

    /**
     * Creates a LookAndFeelException
     * 
     * @param cause Original cause
     */
    public LookAndFeelException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Creates a LookAndFeelException
     * 
     * @param message Error message
     * @param cause Original cause
     */
    public LookAndFeelException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
