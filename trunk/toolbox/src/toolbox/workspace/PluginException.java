package toolbox.workspace;

/**
 * PluginException
 */
public class PluginException extends Exception
{

    /**
     * 
     */
    public PluginException()
    {
        super();
    }

    /**
     * @param message
     */
    public PluginException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public PluginException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public PluginException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
