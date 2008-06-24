package toolbox.util.ui.explorer.listener;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import toolbox.util.ui.explorer.JFileExplorer;

/**
 * Handles the changing of the selection of drive letter. When a new drive
 * is selected, the directory and file lists are automatically populated 
 * with the contents of the drive's root directory.
 */
public class DriveComboListener extends AbstractListener implements ItemListener
{
    //--------------------------------------------------------------------------
    // Constrcutors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DriveComboListener.
     * 
     * @param explorer File explorer.
     */
    public DriveComboListener(JFileExplorer explorer)
    {
        super(explorer);
    }

    //--------------------------------------------------------------------------
    // ItemListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see java.awt.event.ItemListener#itemStateChanged(
     *      java.awt.event.ItemEvent)
     */
    public void itemStateChanged(ItemEvent ie)
    {
        if (ie.getStateChange() == ItemEvent.SELECTED)
        {
            String fileRoot = 
                getProxy().getRootsComboBox().getSelectedItem().toString();
            
            getProxy().setFileList(fileRoot);
            getProxy().clear();
            getProxy().setTreeRoot(fileRoot);
            getProxy().setTreeFolders(fileRoot, null);
        }
    }
}