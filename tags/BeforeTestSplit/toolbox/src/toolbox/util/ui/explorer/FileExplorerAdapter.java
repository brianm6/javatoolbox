package toolbox.util.ui.explorer;

/**
 * Adapter class for FileExplorerListener.
 */
public class FileExplorerAdapter implements FileExplorerListener
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a FileExplorerAdapter.
     */
    public FileExplorerAdapter()
    {
    }

    //--------------------------------------------------------------------------
    // FileExplorerListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see FileExplorerListener#fileDoubleClicked(String)
     */
    public void fileDoubleClicked(String file)
    {
    }

    
    /**
     * @see FileExplorerListener#folderSelected(String)
     */
    public void folderSelected(String folder)
    {
    }

    
    /**
     * @see FileExplorerListener#folderDoubleClicked(String)
     */
    public void folderDoubleClicked(String folder)
    {
    }
    
    
    /**
     * @see FileExplorerListener#fileSelected(java.lang.String)
     */
    public void fileSelected(String file)
    {
    }
}
