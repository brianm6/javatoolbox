package toolbox.workspace;

/**
 * PluginException
 */
public class PluginException extends Exception
{
    /**
     * Creates a PluginException
     */
    public PluginException()
    {
    }

    /**
     * Creates a PluginException
     * 
     * @param message Error message
     */
    public PluginException(String message)
    {
        super(message);
    }

    /**
     * Creates a PluginException
     * 
     * @param cause Originating exception
     */
    public PluginException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Creates a PluginException
     * 
     * @param message Error message
     * @param cause Originating exception
     */
    public PluginException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
