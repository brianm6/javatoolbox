package toolbox.plugin.pdf;

/**
 * Exception throws by methods defined in the DocumentViewer interface.
 */
public class DocumentViewerException extends Exception
{
    /**
     * Creates a DocumentViewerException. 
     */
    public DocumentViewerException()
    {
    }


    /**
     * Creates a DocumentViewerException.
     * 
     * @param message Error message
     */
    public DocumentViewerException(String message)
    {
        super(message);
    }


    /**
     * Creates a DocumentViewerException.
     * 
     * @param cause Originating exception.
     */
    public DocumentViewerException(Throwable cause)
    {
        super(cause);
    }


    /**
     * Creates a DocumentViewerException.
     * 
     * @param message Error message
     * @param cause Originating exception
     */
    public DocumentViewerException(String message, Throwable cause)
    {
        super(message, cause);
    }
}