package toolbox.util.ui.action;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * DisposeAction is responsible for disposing of a Window, Frame, or Dialog.
 */
public class DisposeAction extends AbstractAction
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Window to dispose of.
     */
    private Window window_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a DisposeAction.
     *
     * @param window Window to dispose of.
     */
    public DisposeAction(Window window)
    {
        this("", window);
    }


    /**
     * Creates a DisposeAction.
     *
     * @param text Text label.
     * @param window Window to dispose of.
     */
    public DisposeAction(String text, Window window)
    {
        super(text);
        window_ = window;
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
        window_.dispose();
    }
}