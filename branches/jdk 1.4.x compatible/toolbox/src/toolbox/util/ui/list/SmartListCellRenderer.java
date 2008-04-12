package toolbox.util.ui.list;

import java.awt.Graphics;

import javax.swing.DefaultListCellRenderer;

import toolbox.util.SwingUtil;
import toolbox.util.ui.AntiAliased;

/**
 * SmartListCellRenderer extends DefaultListCellRenderer by adding the
 * following behavior.
 * <p>
 * <ul>
 *   <li>Antialised text
 * </ul>
 */
public class SmartListCellRenderer extends DefaultListCellRenderer
    implements AntiAliased
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Antialiased flag.
     */
    private boolean antiAliased_ = SwingUtil.getDefaultAntiAlias();

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a SmartListCellRenderer.
     */
    public SmartListCellRenderer()
    {
    }

    //--------------------------------------------------------------------------
    // AntiAliased Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.ui.AntiAliased#isAntiAliased()
     */
    public boolean isAntiAliased()
    {
        return antiAliased_;
    }


    /**
     * @see toolbox.util.ui.AntiAliased#setAntiAliased(boolean)
     */
    public void setAntiAliased(boolean b)
    {
        antiAliased_ = b;
    }

    //--------------------------------------------------------------------------
    // Overrides JComponent
    //--------------------------------------------------------------------------

    /**
     * Activates antialiasing if enabled.
     *
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics gc)
    {
        SwingUtil.makeAntiAliased(gc, isAntiAliased());
        super.paintComponent(gc);
    }
}