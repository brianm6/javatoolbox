package toolbox.workspace;

/**
 * Statusbar Interface
 */
public interface IStatusBar
{
    int BUSY    = 1;
    int ERROR   = 2;
    int INFO    = 3;
    int WARNING = 4;
    
    /**
     * Sets the status text
     * 
     * @param  status  Status text
     */
    void setStatus(String status);
    
    void setBusy(boolean busy);
    void setError(String status);
    void setInfo(String status);
    void setWarning(String status);
    
    /**
     * Retrieves the status text
     * 
     * @return  Status text
     */
    String getStatus();
}