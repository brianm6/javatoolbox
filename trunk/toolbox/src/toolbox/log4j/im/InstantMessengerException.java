package toolbox.log4j.im;

/**
 * Exception class for InstantMessenger related failures.
 */
public class InstantMessengerException extends Exception
{
	/**
	 * Creates an InstantMessengerException.
	 */
    public InstantMessengerException()
    {
    }


	/**
	 * Creates an InstantMessengerException.
	 * 
	 * @param message Error message
	 * @param cause Originating exception
	 */
    public InstantMessengerException(String message, Throwable cause)
    {
        super(message, cause);
    }


	/**
	 * Creates an InstantMessengerException.
	 * 
	 * @param cause Originating exception
	 */
    public InstantMessengerException(Throwable cause)
    {
        super(cause);
    }


	/**
	 * Creates an InstantMessengerException.
	 * 
	 * @param message Error message
	 */
    public InstantMessengerException(String message)
    {
        super(message);
    }
}