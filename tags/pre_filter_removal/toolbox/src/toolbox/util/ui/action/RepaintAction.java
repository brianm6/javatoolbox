package toolbox.util.ui.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * Action that repaints a Component.
 */
public class RepaintAction extends AbstractAction
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Component to repaint.
     */
    private Component comp_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a RepaintAction.
     *
     * @param comp Component to repaint.
     */
    public RepaintAction(Component comp)
    {
        comp_ = comp;
    }

    //--------------------------------------------------------------------------
    // Overrides ActionListener
    //--------------------------------------------------------------------------

    /**
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        comp_.repaint();
    }
}