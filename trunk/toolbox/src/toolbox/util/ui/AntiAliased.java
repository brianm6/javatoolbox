package toolbox.util.ui;

/**
 * Antialiased is implements by components that support antialiasing of
 * text or graphics.
 */
public interface AntiAliased
{
    /**
     * Returns true if antialiasing is turned on, false otherwise
     * 
     * @return boolean
     */
    public boolean isAntiAliased();
    
    /**
     * Sets the components antialiasing flag
     * 
     * @param  b  Set to true to turn on antialiasing, false otherwise
     */
    public void setAntiAliased(boolean b);
}