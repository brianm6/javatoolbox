package toolbox.jtail.config;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;


/**
 * Interface for JTail configuration preferences
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
    
    /**
     * Returns the autoScroll.
     * @return boolean
     */
    public boolean getDefaultAutoScroll();

    /**
     * Returns the filter.
     * @return String
     */
    public String getDefaultFilter();

    /**
     * Returns the showLineNumbers.
     * @return boolean
     */
    public boolean getDefaultShowLineNumbers();

    /**
     * Sets the autoScroll.
     * @param autoScroll The autoScroll to set
     */
    public void setDefaultAutoScroll(boolean autoScroll);

    /**
     * Sets the filter.
     * @param filter The filter to set
     */
    public void setDefaultFilter(String filter);

    /**
     * Sets the showLineNumbers.
     * @param showLineNumbers The showLineNumbers to set
     */
    public void setDefaultShowLineNumbers(boolean showLineNumbers);
}
