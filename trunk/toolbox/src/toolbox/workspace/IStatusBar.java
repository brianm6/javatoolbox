package toolbox.util.ui.plugin;

/**
 * Statusbar Interface
 */
public interface IStatusBar
{
    /**
     * Sets the status text
     * 
     * @param  status  Status text
     */
    public void setStatus(String status);
    
    /**
     * Retrieves the status text
     * 
     * @return  Status text
     */
    public String getStatus();
}