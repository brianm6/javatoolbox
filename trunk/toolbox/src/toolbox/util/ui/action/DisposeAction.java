package toolbox.util.ui.action;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * DisposeAction is responsible for disposing of a Frame or a Dialog.
 */
public class DisposeAction extends AbstractAction
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Frame to dispose.
     */
    private Frame frame_;

    /**
     * Dialog to dispose.
     */
    private Dialog dialog_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a DisposeAction.
     *
     * @param dialog Dialog to dispose of.
     */
    public DisposeAction(Dialog dialog)
    {
        this("", dialog);
    }

    
    /**
     * Creates a DisposeAction.
     *
     * @param text Text label.
     * @param dialog Dialog to dispose of.
     */
    public DisposeAction(String text, Dialog dialog)
    {
        super(text);
        dialog_ = dialog;
    }

    
    /**
     * Creates a DisposeAction.
     *
     * @param frame Frame to dispose of.
     */
    public DisposeAction(Frame frame)
    {
        this("", frame);
    }

    
    /**
     * Creates a DisposeAction.
     *
     * @param text Text label.
     * @param frame Frame to dispose of.
     */
    public DisposeAction(String text, Frame frame)
    {
        super(text);
        frame_ = frame;
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
        if (dialog_ != null)
            dialog_.dispose();
        else if (frame_ != null)
            frame_.dispose();
    }
}
