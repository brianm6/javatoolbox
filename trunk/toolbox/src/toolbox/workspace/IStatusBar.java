package toolbox.workspace;

/**
 * Statusbar Interface
 */
public interface IStatusBar
{
    public static final int BUSY    = 1;
    public static final int ERROR   = 2;
    public static final int INFO    = 3;
    public static final int WARNING = 4;
    
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