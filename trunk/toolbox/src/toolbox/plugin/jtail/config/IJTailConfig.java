package toolbox.jtail.config;

import java.awt.Dimension;
import java.awt.Point;

/**
 * Interface for JTail configuration preferences
 */
public interface IJTailConfig
{
    /**
     * Returns the default tail pane configuration
     * 
     * @return ITailPaneConfiguration
     */
    public ITailPaneConfig getDefaultConfig();

    /**
     * Returns the location.
     * 
     * @return Point
     */
    public Point getLocation();

    /**
     * Returns the size.
     * 
     * @return Dimension
     */
    public Dimension getSize();

    /**
     * Returns the last directory selecting in the file explorer pane
     *
     * @return  String
     */
    public String getDirectory();

    /**
     * Sets the last directory selected in the file explorer pane
     * 
     * @param directory  Directory selected
     */
    public void setDirectory(String directory);

    /**
     * Sets the default tail pane configuration
     * 
     * @param config  The default tail pane configuration
     */
    public void setDefaultConfig(ITailPaneConfig config);

    /**
     * Sets the location.
     * 
     * @param location The location to set
     */
    public void setLocation(Point location);

    /**
     * Sets the size.
     * 
     * @param size The size to set
     */
    public void setSize(Dimension size);
    
    /**
     * Returns the tailPaneConfigs.
     * 
     * @return ITailPaneConfig[]
     */
    public ITailPaneConfig[] getTailConfigs();

    /**
     * Sets the tailPaneConfigs.
     * 
     * @param tailPaneConfigs The tailPaneConfigs to set
     */
    public void setTailConfigs(ITailPaneConfig[] tailPaneConfigs);
    
}
