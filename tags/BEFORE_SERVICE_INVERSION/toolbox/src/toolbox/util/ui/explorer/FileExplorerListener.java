package toolbox.util.ui.explorer;

/**
 * Listener interface for JFileExplorer generated events.
 * 
 * @see toolbox.util.ui.explorer.JFileExplorer
 * @see toolbox.util.ui.explorer.FileExplorerAdapter
 */
public interface FileExplorerListener
{
    /**
     * Called when a directory folder is selected in the tree view.
     *
     * @param folder Name of the selected directory.
     */
    void folderSelected(String folder);


    /**
     * Called when a directory folder is double clicked in the tree view.
     *
     * @param folder Name of directory that was double clicked.
     */
    void folderDoubleClicked(String folder);

    
    /**
     * Called when a file is selected in the file list.
     *
     * @param file Name of the selected file.
     */
    void fileSelected(String file);


    /**
     * Called when a file is double clicked in the file list.
     *
     * @param file Name of the file that was clicked.
     */
    void fileDoubleClicked(String file);
}