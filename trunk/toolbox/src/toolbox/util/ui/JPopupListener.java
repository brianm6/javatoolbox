package toolbox.util.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

/**
 * Popup menu listener for RMB
 */ 
public class JPopupListener extends MouseAdapter
{
    public JPopupMenu popupMenu_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
        
    public JPopupListener(JPopupMenu popupMenu)
    {
        popupMenu_ = popupMenu;
    }
    
    //--------------------------------------------------------------------------
    //  Overriden MouseAdapter Methods
    //--------------------------------------------------------------------------    
    
    public void mousePressed(MouseEvent e)
    {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e)
    {
        maybeShowPopup(e);
    }

    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------
    
    private void maybeShowPopup(MouseEvent e)
    {
        if (e.isPopupTrigger())
            popupMenu_.show(e.getComponent(), e.getX(), e.getY());
    }
}