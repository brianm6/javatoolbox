package toolbox.util.ui.explorer.listener;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

import toolbox.util.ui.JSmartPopupMenu;
import toolbox.util.ui.explorer.JFileExplorer;
import toolbox.util.ui.explorer.action.CreateDirAction;
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
                popup.add(new CreateDirAction(getProxy()));
                popup.add(new RenameDirAction(getProxy()));
                getProxy().setFolderPopup(popup);
            }
            
            popup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
        else if (evt.getClickCount() == 2)
        {
            getProxy().fireFolderDoubleClicked(getExplorer().getCurrentPath());
        }
    }
    
    public void mouseEntered(MouseEvent e)
    {
    }
    
    public void mouseExited(MouseEvent e)
    {
    }
    
    public void mousePressed(MouseEvent e)
    {
    }
    
    public void mouseReleased(MouseEvent e)
    {
    }
}