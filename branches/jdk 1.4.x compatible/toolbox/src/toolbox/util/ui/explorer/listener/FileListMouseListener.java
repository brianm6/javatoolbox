package toolbox.util.ui.explorer.listener;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;

import toolbox.util.SwingUtil;
import toolbox.util.ui.explorer.JFileExplorer;

/**
 * Handles double click mouse events on a file in the file list.
 */
public class FileListMouseListener extends AbstractListener 
    implements MouseListener
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a FileListMouseListener.
     * 
     * @param explorer File explorer.
     */
    public FileListMouseListener(JFileExplorer explorer)
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
        JList fileList = (JList) evt.getSource();
        
        if (SwingUtil.isDoubleClick(evt) && fileList.getSelectedIndex() != -1)
        {
            // Double click on a file fires event to listeners 
            getProxy().fireFileDoubleClicked();
        }
        else if (evt.getClickCount() == 1)
        {
            ; // No need to fire a fileSelected event. 
              // FileListSelectionListener has this covered.
        }
        else if ((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
        {
            ; // nothing tied to right mouse button click
        }
    }
    
    
    /**
     * No-op
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e)
    {
    }
    
    
    /**
     * No-op
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e)
    {
    }
    
    
    /**
     * No-op
     * 
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e)
    {
    }
    
    
    /**
     * No-op
     * 
     * @see java.awt.event.MouseListener#mouseReleased(
     *      java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e)
    {
    }
}