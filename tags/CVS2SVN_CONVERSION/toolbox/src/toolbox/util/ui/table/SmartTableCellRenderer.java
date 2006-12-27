package toolbox.util.ui.table;

import java.awt.Graphics;

import javax.swing.table.DefaultTableCellRenderer;

import toolbox.util.SwingUtil;
import toolbox.util.ui.AntiAliased;

/**
 * SmartTableCellRender adds the following behavior.
 * <p>
 * <ul>
 * <li>Antialiased text
 * </ul>
 * <p>
 * The antialiasing cannot be achieved via a decorator since the paintComponent
 * method of the delegate would have to be overridden (there is no other way
 * to get to the Graphics context that is passed in as an argument).
 */
public class SmartTableCellRenderer extends DefaultTableCellRenderer implements
    AntiAliased {

    // --------------------------------------------------------------------------
    // Fields
    // --------------------------------------------------------------------------

    /**
     * Antialiased flag.
     */
    private boolean antiAliased_ = SwingUtil.getDefaultAntiAlias();

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    /**
     * Creates a SmartTableCellRenderer.
     */
    public SmartTableCellRenderer() {
    }

    // --------------------------------------------------------------------------
    // AntiAliased Interface
    // --------------------------------------------------------------------------

    /*
     * @see toolbox.util.ui.AntiAliased#isAntiAliased()
     */
    public boolean isAntiAliased() {
        return antiAliased_;
    }


    /*
     * @see toolbox.util.ui.AntiAliased#setAntiAliased(boolean)
     */
    public void setAntiAliased(boolean b) {
        antiAliased_ = b;
    }

    // --------------------------------------------------------------------------
    // Overrides JComponent
    // --------------------------------------------------------------------------

    /*
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics gc) {
        SwingUtil.makeAntiAliased(gc, isAntiAliased());
        super.paintComponent(gc);
    }
}