package toolbox.util.ui.explorer.listener;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

import toolbox.util.ui.JSmartPopupMenu;
import toolbox.util.ui.explorer.JFileExplorer;
import toolbox.util.ui.explorer.action.CreateDirAction;
import toolbox.util.ui.explorer.action.DeleteDirAction;
import toolbox.util.ui.explorer.action.RenameDirAction;

/**
 * Inner class for handling mouse click events on the directory tree.
 */
public class DirTreeMouseListener extends AbstractListener 
    implements MouseListener
{
    private static final Logger logger_ = 
        Logger.getLogger(DirTreeMouseListener.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DirTreeMouseListener.
     * 
     * @param explorer File explorer.
     */
    public DirTreeMouseListener(JFileExplorer explorer)
    {
        super(explorer);
    }

    //--------------------------------------------------------------------------
    // MouseListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see java.awt.event.MouseListener#mouseClicked(
     *      java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent evt)
    {
        if ((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0 ) 
        {
            JPopupMenu popup = getProxy().getFolderPopup();
            
            if (popup == null)
            {
                popup = new JSmartPopupMenu("DirPopup");
                popup.add(new CreateDirAction(getExplorer()));
                popup.add(new DeleteDirAction(getExplorer()));
                popup.add(new RenameDirAction(getExplorer()));
                getProxy().setFolderPopup(popup);
            }
            
            popup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
        else if (evt.getClickCount() == 2)
        {
            getProxy().fireFolderDoubleClicked(getExplorer().getCurrentPath());
        }
    }
    
    
    /**
     * No op.
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e)
    {
    }
    
    
    /**
     * No op.
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e)
    {
    }
    
    
    /**
     * No op.
     * 
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e)
    {
    }
    
    
    /**
     * No op.
     * 
     * @see java.awt.event.MouseListener#mouseReleased(
     *      java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e)
    {
    }
}