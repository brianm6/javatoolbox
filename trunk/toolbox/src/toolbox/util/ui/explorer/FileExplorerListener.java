package toolbox.util.ui;

/**
 * Listener defining all events that the JFileExplorer advertises
 */
public interface JFileExplorerListener
{
    /**
     * Called when a file is double clicked in the details view
     *
     * @param  file   Name of the file that was clicked
     */
    public void fileDoubleClicked(String file);
    
    /**
     * Called when a directory folder is selected in the tree view
     * 
     * @param  folder  Name of the directory selected
     */
    public void folderSelected(String folder);    
}