package toolbox.util.ui.explorer;

/**
 * Listener interface for JFileExplorer generated events.
 */
public interface FileExplorerListener
{
    /**
     * Called when a file is selected in the file list.
     * 
     * @param file Name of the file selected.
     */
    void fileSelected(String file);    
    
    
    /**
     * Called when a file is double clicked in the details view.
     *
     * @param file Name of the file that was clicked.
     */
    void fileDoubleClicked(String file);
    
    
    /**
     * Called when a directory folder is selected in the tree view.
     * 
     * @param folder Name of the directory selected.
     */
    void folderSelected(String folder);    
    
    
    /**
     * Called when a folder is doubleclicked in the tree view.
     * 
     * @param folder Name of directory double clicked.
     */
    void folderDoubleClicked(String folder);    
}