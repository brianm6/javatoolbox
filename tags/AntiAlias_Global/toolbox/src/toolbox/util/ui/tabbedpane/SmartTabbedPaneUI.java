package toolbox.util.ui.tabbedpane;

import java.awt.FontMetrics;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import org.apache.log4j.Logger;

/**
 * Specialization of the BasicTabbedPaneIU that allows for setting of the
 * horizontal text position.
 */
public class SmartTabbedPaneUI extends BasicTabbedPaneUI
{
    private static final Logger logger_ =
        Logger.getLogger(SmartTabbedPaneUI.class);
        
    /**
     * Bounds of the selected icon
     */
    private Rectangle selectedIconRect_;
    
    /**
     * Text position which defaults to LEFT (not the Icon position)
     */
    private int horizontalTextPosition_ = SwingUtilities.LEFT;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SmartTabbedPaneUI.
     */
    public SmartTabbedPaneUI()
    {
    }

    /**
     * Creates a SmartTabbedPaneUI
     * 
     * @param horTextPosition Horizontal text position. 
     *        Use SwingConstants.[LEFT|RIGHT]
     */
    public SmartTabbedPaneUI(int horTextPosition)
    {
        horizontalTextPosition_ = horTextPosition;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the bounds of the selection icon
     * 
     * @return Rectangle
     */
    public Rectangle getSelectedIconRect()
    {
        return selectedIconRect_;
    }

    //--------------------------------------------------------------------------
    // Overrides BasicTabbedPaneUI
    //--------------------------------------------------------------------------

    /**
     * @see javax.swing.plaf.basic.BasicTabbedPaneUI#layoutLabel(
     *          int, java.awt.FontMetrics, int, java.lang.String, 
     *          javax.swing.Icon, java.awt.Rectangle, java.awt.Rectangle, 
     *          java.awt.Rectangle, boolean)
     */
    protected void layoutLabel(
        int tabPlacement,
        FontMetrics metrics,
        int tabIndex,
        String title,
        Icon icon,
        Rectangle tabRect,
        Rectangle iconRect,
        Rectangle textRect,
        boolean isSelected)
    {

        //logger_.debug("Layout out label in " + getClass().getName());
        
        textRect.x = 0;
        textRect.y = 0;
        iconRect.x = 0;
        iconRect.y = 0;
        
        SwingUtilities.layoutCompoundLabel(
            (JComponent) tabPane,
            metrics,
            title,
            icon,
            SwingUtilities.CENTER,
            SwingUtilities.CENTER,
            SwingUtilities.CENTER,
            horizontalTextPosition_,
            tabRect,
            iconRect,
            textRect,
            textIconGap + 2);

        selectedIconRect_ = iconRect;
    }
}