package toolbox.util.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

/**
 * Popup menu listener for RMB
 */ 
public class JPopupListener extends MouseAdapter
{
    /**
     * Popup menu to add a listener to
     */
    private JPopupMenu popupMenu_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JPopupListener
     * 
     * @param  popupMenu  Menu to add this listener to
     */    
    public JPopupListener(JPopupMenu popupMenu)
    {
        popupMenu_ = popupMenu;
    }
    
    //--------------------------------------------------------------------------
    //  Overrides java.awt.event.MouseAdapter
    //--------------------------------------------------------------------------
    
    /**
     * @param e Mouse event
     */
    public void mousePressed(MouseEvent e)
    {
        maybeShowPopup(e);
    }

    /**
     * @param e Mouse event
     */
    public void mouseReleased(MouseEvent e)
    {
        maybeShowPopup(e);
    }

    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------
    
    /**
     * Determines if the popupmenu should be made visible
     * 
     * @param  e  Mouse event
     */
    private void maybeShowPopup(MouseEvent e)
    {
        if (e.isPopupTrigger())
            popupMenu_.show(e.getComponent(), e.getX(), e.getY());
    }
}