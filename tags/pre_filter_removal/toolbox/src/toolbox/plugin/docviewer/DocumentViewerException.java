package toolbox.plugin.docviewer;

/**
 * Exception thrown by classes implementing the {@link DocumentViewer} 
 * interface.
 */
public class DocumentViewerException extends RuntimeException
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
     * @param message Error message.
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
     * @param message Error message.
     * @param cause Originating exception.
     */
    public DocumentViewerException(String message, Throwable cause)
    {
        super(message, cause);
    }
}