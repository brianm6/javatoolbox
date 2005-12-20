package toolbox.util.ui.explorer.listener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import toolbox.util.ui.explorer.JFileExplorer;

/**
 * Listens for selection changes in the file list, and fires events
 * accordingly.
 */
public class FileListSelectionListener extends AbstractListener 
    implements ListSelectionListener
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a FileListSelectionListener.
     * 
     * @param explorer File explorer.
     */
    public FileListSelectionListener(JFileExplorer explorer)
    {
        super(explorer);
    }

    //--------------------------------------------------------------------------
    // ListSelectionListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(
     *      javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e)
    {
        if (e.getValueIsAdjusting())
            return;
            
        getProxy().fireFileSelected();
    }
}