package toolbox.jtail.config;

import java.awt.Dimension;
import java.awt.Point;

/**
 * Interface used for the persistence of the JTail application's preferences.
 */
public interface IJTailConfig
{
    /**
     * Returns the default tail pane configuration
     * 
     * @return  Default configuration
     */
    public ITailPaneConfig getDefaultConfig();

    /**
     * Returns the location of the window
     * 
     * @return  Location of the window
     */
    public Point getLocation();

    /**
     * Returns the size of the window
     * 
     * @return  Size of the window
     */
    public Dimension getSize();

    /**
     * Returns the last directory selecting in the file explorer pane
     *
     * @return  Last selected directory
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
     * @param config  Default tail pane configuration
     */
    public void setDefaultConfig(ITailPaneConfig config);

    /**
     * Sets the location of the window
     * 
     * @param  location  Location of the window
     */
    public void setLocation(Point location);

    /**
     * Sets the size of the window
     * 
     * @param  size  Size of the window
     */
    public void setSize(Dimension size);
    
    /**
     * Returns the tailPaneConfigs.
     * 
     * @return  Array of tail pain configurations
     */
    public ITailPaneConfig[] getTailConfigs();

    /**
     * Sets the list of tail pane configurations
     * 
     * @param tailPaneConfigs  Tail pane configurations
     */
    public void setTailConfigs(ITailPaneConfig[] tailPaneConfigs);
}
