package toolbox.jtail.config;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;


/**
 * enclosing_type
 */
public interface IJTailConfig
{
    /**
     * Returns the defaultFont.
     * @return Font
     */
    public Font getDefaultFont();

    /**
     * Returns the location.
     * @return Point
     */
    public Point getLocation();

    /**
     * Returns the size.
     * @return Dimension
     */
    public Dimension getSize();

    /**
     * Sets the defaultFont.
     * @param defaultFont The defaultFont to set
     */
    public void setDefaultFont(Font defaultFont);

    /**
     * Sets the location.
     * @param location The location to set
     */
    public void setLocation(Point location);

    /**
     * Sets the size.
     * @param size The size to set
     */
    public void setSize(Dimension size);
    
    /**
     * Returns the tailPaneConfigs.
     * @return ITailPaneConfig[]
     */
    public ITailPaneConfig[] getTailPaneConfigs();

    /**
     * Sets the tailPaneConfigs.
     * @param tailPaneConfigs The tailPaneConfigs to set
     */
    public void setTailPaneConfigs(ITailPaneConfig[] tailPaneConfigs);
}
