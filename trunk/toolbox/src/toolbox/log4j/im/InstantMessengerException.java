package toolbox.log4j.im;

/**
 * Exception class for InstantMessenger related failures
 */
public class InstantMessengerException extends Exception
{
    public InstantMessengerException()
    {
    }

    public InstantMessengerException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InstantMessengerException(Throwable cause)
    {
        super(cause);
    }

    public InstantMessengerException(String message)
    {
        super(message);
    }
}