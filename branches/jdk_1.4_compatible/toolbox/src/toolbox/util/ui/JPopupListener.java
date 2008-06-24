package toolbox.util.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

/**
 * Popup menu listener activated by a right-mouse-button click event.
 */
public class JPopupListener extends MouseAdapter
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Popup menu to add a listener to.
     */
    private JPopupMenu popupMenu_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a JPopupListener.
     *
     * @param popupMenu Menu to add this listener to.
     */
    public JPopupListener(JPopupMenu popupMenu)
    {
        popupMenu_ = popupMenu;
    }

    //--------------------------------------------------------------------------
    // Overrides java.awt.event.MouseAdapter
    //--------------------------------------------------------------------------

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e)
    {
        maybeShowPopup(e);
    }


    /**
     * @see java.awt.event.MouseListener#mouseReleased(
     *      java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e)
    {
        maybeShowPopup(e);
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------

    /**
     * Determines if the popupmenu should be made visible.
     *
     * @param e Mouse event.
     */
    private void maybeShowPopup(MouseEvent e)
    {
        if (e.isPopupTrigger())
            popupMenu_.show(e.getComponent(), e.getX(), e.getY());
    }
}