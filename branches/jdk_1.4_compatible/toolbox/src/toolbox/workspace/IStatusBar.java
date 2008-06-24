package toolbox.workspace;

/**
 * An IStatusbar is capable of displaying an arbitrary sized status message 
 * along with a visual cue to indicate the nature of the message. 
 */
public interface IStatusBar
{
    /**
     * Sets the text of the status bar.
     * 
     * @param status Status text.
     */
    void setStatus(String status);
    
    
    /**
     * Sets the busy state indicator on the status bar.
     * 
     * @param busy True to indicate busy, false otherwise.
     */
    void setBusy(boolean busy);

    
    /**
     * Sets the text of the status bar and indicates that it is an error
     * message.
     * 
     * @param status Error message.
     */
    void setError(String status);
    
    
    /**
     * Sets the text of the status bar and indicates that it is an informational
     * message.
     * 
     * @param status Informational message.
     */
    void setInfo(String status);
    
    
    /**
     * Sets the text of the status bar and indicates that it is a warning.
     * 
     * @param status Warning message.
     */
    void setWarning(String status);
    
    
    /**
     * Retrieves the text of the status message.
     * 
     * @return String
     */
    String getStatus();
}