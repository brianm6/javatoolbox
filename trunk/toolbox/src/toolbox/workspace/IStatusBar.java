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
    public void setStatus(String status);
    
    public void setBusy(boolean busy);
    public void setError(String status);
    public void setInfo(String status);
    public void setWarning(String status);
    
    /**
     * Retrieves the status text
     * 
     * @return  Status text
     */
    public String getStatus();
}