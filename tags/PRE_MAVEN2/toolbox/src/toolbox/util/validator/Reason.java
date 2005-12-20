package toolbox.util.validator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * A Reason is represents the text and/or stacktrace explaining a failure or a 
 * warning generated during the validation process.
 */
public final class Reason
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Explanation of the warning or failure.
     */
    private String message_;
    
    /**
     * Cause of this warning or failure.
     */
    private Throwable cause_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a Reason.
     * 
     * @param message Reason for failure.
     */
    public Reason(String message)
    {
        this(message, null);
    }


    /**
     * Creates a Reason.
     * 
     * @param cause Cause of failure.
     */
    public Reason(Throwable cause)
    {
        this("", cause);
    }


    /**
     * Creates a Reason.
     * 
     * @param message Reason for failure.
     * @param cause Cause of failure.
     */
    public Reason(String message, Throwable cause)
    {
        message_ = message;
        cause_ = cause;
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Returns reason as a formatted string containing the text of the reason
     * and the stacktrace of the cause if available.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        if (!StringUtils.isBlank(message_))
            sb.append(message_);

        if (cause_ != null)
        {
            if (sb.length() > 0)
                sb.append("\n");
            sb.append(cause_.getLocalizedMessage() + "\n");
            sb.append(ExceptionUtils.getFullStackTrace(cause_));
        }

        if (sb.length() == 0)
            sb.append(
                "The creator of this error message neglected to provide "
                + " any meaningful error message or information");

        return sb.toString();
    }
}