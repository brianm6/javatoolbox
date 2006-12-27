package toolbox.util.ui.explorer.action;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import org.apache.commons.io.FilenameUtils;

import toolbox.util.ui.explorer.JFileExplorer;
import toolbox.util.ui.explorer.listener.DriveComboListener;

/**
 * RefreshAction is responsible for refreshing the file explorer.
 */
public class RefreshAction extends AbstractDirAction
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a RefreshAction.
     */
    public RefreshAction(JFileExplorer explorer)
    {
        super("Refresh", explorer);
    }

    //--------------------------------------------------------------------------
    // ActionListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * Delegates to <code>restore()</code>.
     *  
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        refresh();
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Refreshes the file explorer.
     */
    public void refresh()
    {
        String folder = getExplorer().getCurrentPath();
        String file   = FilenameUtils.getName(getExplorer().getFilePath());
        
        new DriveComboListener(getExplorer()).itemStateChanged(
                new ItemEvent(getProxy().getRootsComboBox(), 0, null, 
                    ItemEvent.ITEM_STATE_CHANGED));
               
        getExplorer().selectFolder(folder);
        getProxy().setFileList(folder);
        getExplorer().getFileList().setSelectedValue(file, true);
    }
}